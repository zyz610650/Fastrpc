package com.fastrpc.constants;


import com.fastrpc.config.Config;
import org.apache.curator.framework.CuratorFramework;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zyz
 */


public class ZkContants {
    /**
     * 每次重试的sleep时间
     */
    public static final int BASE_SLEEP_TIME=1000;
    /**
     * 连接zk最大重试次数
     */
    public static final int MAX_RETRIES= 3 ;
    /**
     * rpc在Zk中的根目录
     */
    public static final String ZK_REGISTER_ROOT_PATH="/fastrpc";
    /**
     * 会话超时时间
     */
    public static final int SESSION_TIMEOUT=5000;
    /**
     * 连接超时时间
     */
    public static final int CONNECTION_TIMEOUT=5000;

    /**
     * 命名空间 用于在同一zk集群中实现不同应用之间的相互隔离
     */
    public static final String NAMESPACE="fastrpc";

    /**
     * zk的ip:port
     */
    public static final String host = Config.getZkHost();




}
