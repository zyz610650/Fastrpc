package com.fastrpc.zk.utils;

import io.netty.util.internal.ConcurrentSet;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Curator(Zookeeper client) utils
 *
 * @author yvioo
 */
public class CuratorUtils {

    private static final int BASE_SLEEP_TIME=1000;
    private static final int MAX_RETRIES= 3 ;
    private static final String ZK_REGISTER_ROOT_PATH="/my-rpc";
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP=new ConcurrentHashMap<>();
    private static final Set<String> REGISTERED_PATH_PATH_SET=ConcurrentHashMap.newKeySet();
    private static CuratorFramework zkClient;


    public static void createPersistentNode(CuratorFramework zkClient,String path)
    {
        try {
            if (REGISTERED_PATH_PATH_SET.contains(path)||zkClient.checkExists().forPath(path)!=null)
            {
                System.out.println("node exists");
            } else {
              zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);

            }
            REGISTERED_PATH_PATH_SET.add(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getChildrenNodes(CuratorFramework zkClient,String ServiceName)
    {
        if (SERVICE_ADDRESS_MAP.containsKey(ServiceName))
        {
            return SERVICE_ADDRESS_MAP.get(ServiceName);
        }
        List<String> list=null;
        String servicePath=ZK_REGISTER_ROOT_PATH+"/"+ServiceName;

        try {
            list=zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(ServiceName,list);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    void crateListen(String path,CuratorFramework zkClient) throws Exception {
        PathChildrenCache pathChildrenCache=new PathChildrenCache(zkClient,path,true);
        PathChildrenCacheListener pathChildrenCacheListener=new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                List<String> list = curatorFramework.getChildren().forPath(path);
                SERVICE_ADDRESS_MAP.put(path,list);
            }
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }

    public static void ckearRegistry(CuratorFramework zkClient, InetSocketAddress inetSocketAddress)
    {
        REGISTERED_PATH_PATH_SET.parallelStream().forEach(p->{
            if (p.endsWith(inetSocketAddress.toString())) {
                try {
                    zkClient.delete().forPath(p);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        Map<String, String> SERVICE_ADDRESS_MAP=new ConcurrentHashMap<>();
        Set<String> REGISTERED_PATH_PATH_SET=ConcurrentHashMap.newKeySet();

        SERVICE_ADDRESS_MAP.put("11","211");
        System.out.println(REGISTERED_PATH_PATH_SET.size());
        System.out.println(SERVICE_ADDRESS_MAP.size());
    }

}
