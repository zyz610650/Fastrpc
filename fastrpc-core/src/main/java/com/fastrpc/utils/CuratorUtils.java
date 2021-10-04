package com.fastrpc.utils;

import ch.qos.logback.classic.Logger;
import com.fastrpc.Exception.RpcException;
import com.fastrpc.constants.ZkContants;
import com.fastrpc.transport.message.RpcRequestMessage;
import com.fastrpc.service.InfoService;
import com.fastrpc.service.User;
import com.fastrpc.registry.ZkService;
import com.fastrpc.registry.impl.ZkServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Curator(Zookeeper client) utils
 * CuratorFramework is thread safety
 *
 * REGISTERED_PATH_PATH_SET: Netty服务器中对于ZK节点的缓存
 * SERVICE_ADDRESS_MAP:  Netty客户端对于ZK节点的缓存
 * @author yvioo
 */
@Slf4j
public class CuratorUtils {

    /**
     * 缓存path  防止重复创建zk节点
     */
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP=new ConcurrentHashMap<>();
    /**
     *  缓存zk节点 获得节点时使用
     */
    private static final Set<String> REGISTERED_PATH_PATH_SET=ConcurrentHashMap.newKeySet();
    private static CuratorFramework zkClient;

    /**
     * 连接到zk
     * @return
     */
    public static CuratorFramework getZkClient()
    {
        if (zkClient!=null&&zkClient.getState()== CuratorFrameworkState.STARTED)
        {
            return zkClient;
        }

        RetryPolicy retryPolicy=new ExponentialBackoffRetry(ZkContants.BASE_SLEEP_TIME,ZkContants.MAX_RETRIES);
        zkClient= CuratorFrameworkFactory.builder()
                .connectString(ZkContants.host)
                .sessionTimeoutMs(ZkContants.SESSION_TIMEOUT)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
        log.error("connect to ZooKeeper successfully");
        try {
            // 连接超时触发事件
            if (!zkClient.blockUntilConnected(ZkContants.CONNECTION_TIMEOUT, TimeUnit.SECONDS))
            {
                log.error("Time out waiting to connect to ZooKeeper");
                throw new RuntimeException("Time out waiting to connect to ZooKeeper");
            }
        } catch (InterruptedException e) {
            throw new RpcException(e.getMessage());
        }
        return zkClient;
    }

    /**
     * 创建持久节点
     * @param path
     */
    public static void createPersistentNode(String path)
    {

        zkClient=getZkClient();
        try {
            if (REGISTERED_PATH_PATH_SET.contains(path)||zkClient.checkExists().forPath(path)!=null)
            {
                log.info("The node alerady exists, it is: [{}]",path);
            }else {

                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("The node is created successfully, it is:[{}]",path);
            }
            REGISTERED_PATH_PATH_SET.add(path);
        } catch (Exception e) {
            log.error("create persistent node for path [{}] fail", path);
            throw new RpcException(e.getMessage());
        }
    }

    /**
     * 删除某一个注册到zk上的服务节点
     * @param
     */
    public static void delNode(InetSocketAddress inetSocketAddress)
    {
        //清除Set缓存和注册的zk
        REGISTERED_PATH_PATH_SET
                .stream()
                .filter(s-> s.endsWith(inetSocketAddress.toString().replace("/","")))
                .forEach(p-> {
                REGISTERED_PATH_PATH_SET.remove(p);
                    try {
                        zkClient.delete().forPath(p);
                    } catch (Exception e) {
                        log.info("The node isn't deleted : [{}]",e.getMessage());
                        throw new RpcException(e.getMessage());
                    }
                });



    }

    /**
     *    清除fastrpc注册到zk上的所有节点
     */
    public static void delRpcServiceName(String rpcServiceName)
    {
        try {
            //提供rpcService服务的主机下线 则清除zk中记录的rpcServiceName
            String servicePath=getPath(rpcServiceName);
            getZkClient().delete().deletingChildrenIfNeeded().forPath(servicePath);
            //清除缓存
            REGISTERED_PATH_PATH_SET.remove(servicePath);

            if (getZkClient().getChildren().forPath(ZkContants.ZK_REGISTER_ROOT_PATH).size()==0)
            {
                //清除fastrpc注册到zk上的所有节点
                getZkClient().delete().deletingChildrenIfNeeded().forPath(ZkContants.ZK_REGISTER_ROOT_PATH);
            }

        } catch (Exception e) {
            throw new RpcException(e.getMessage());
        }
    }

    /**
     * 获取zk中的节点值
     * @param rpcServiceName
     * @return
     */
    public static List<String> getChildNodes(String rpcServiceName)
    {
        if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName))
        {
            return SERVICE_ADDRESS_MAP.get(rpcServiceName);
        }
        String servicePath=getPath(rpcServiceName);
        List<String> res;
        zkClient=getZkClient();
        try {
            res=zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName,res);
            addListener(rpcServiceName);
        } catch (Exception e) {
            log.error("get children nodes for path [{}] fail",servicePath);
            throw new RpcException(e.getMessage());
        }
        return res;
    }

    /**
     * 为rpc服务添加监听器 增删改节点的值后触发
     * @param rpcServiceName
     */
    private static void addListener(String rpcServiceName)
    {
        String path=getPath(rpcServiceName);
        PathChildrenCache pathChildrenCache=new PathChildrenCache(zkClient,path,true);
        PathChildrenCacheListener pathChildrenCacheListener= (curatorFramework, pathChildrenCacheEvent) -> {
           List<String> serviceAddress=curatorFramework.getChildren().forPath(path);
           SERVICE_ADDRESS_MAP.put(rpcServiceName,serviceAddress);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);

    }

    /**
     * 拼接注册到zk的rpc服务的完整地址
     * @param rpcServiceName
     * @return
     */
    public static String getPath(String rpcServiceName)
    {
        return ZkContants.ZK_REGISTER_ROOT_PATH+"/"+rpcServiceName;
    }

//    public static void main(String[] args) {
//
//        createPersistentNode("/fastrpc/redisService/192.168.2.1:8080");
//        createPersistentNode("/fastrpc/redisService/122.4.2.1:7464");
//        createPersistentNode("/fastrpc/redisService/32.22.2.1:2568");
//        createPersistentNode("/fastrpc/redisService/192.33.2.1:80");
//        RpcRequestMessage msg=new RpcRequestMessage(1,"redisService","say",
//                new Class[]{String.class, User.class},new Object[]{"zyz",new User()});
//        RpcRequestMessage msg1=new RpcRequestMessage(1,"redisService","sayHello",
//                new Class[]{String.class, User.class},new Object[]{"zyz",new User()});
//        RpcRequestMessage msg2=new RpcRequestMessage(1,"redisService","say",
//                new Class[]{String.class, User.class},new Object[]{"zyz",new User()});
//        RpcRequestMessage msg3=new RpcRequestMessage(1,"redisService","sayH",
//                new Class[]{String.class, User.class},new Object[]{"zyzdsa",new User()});
//        RpcRequestMessage msg4=new RpcRequestMessage(1,"redisService","say",
//                new Class[]{String.class, InfoService.class},new Object[]{"zyz",new User()});
//        ZkService zkService=new ZkServiceImpl();
//        System.out.println(zkService.getRpcService(msg));
//        System.out.println(zkService.getRpcService(msg1));
//        System.out.println(zkService.getRpcService(msg2));
//        System.out.println(zkService.getRpcService(msg3));
//        System.out.println(zkService.getRpcService(msg4));
//    }
}
