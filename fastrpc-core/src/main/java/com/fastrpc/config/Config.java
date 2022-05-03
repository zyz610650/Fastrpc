package com.fastrpc.config;

import com.fastrpc.enums.RpcConfigEnum;
import com.fastrpc.utils.CpuUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 从配置文件中获取fastRpc的配置信息
 * @author zyz
 */
public class Config {

    static Properties properties;
    final static String zkHost="127.0.0.1:2181";
    final static String serverHost="localhost";
    final static Integer serverPort=7788;
    final static Integer connectNum=20;

    static {

        try (InputStream in = Config.class.getResourceAsStream(String.valueOf(RpcConfigEnum.RPC_CONFIG_PATH.getValue()).trim())) {
            properties = new Properties();
            properties.load(in);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取rpc服务器断开号
     * @return
     */
    public static int getServerPort(){
        String value=properties.getProperty(String.valueOf(RpcConfigEnum.SERVER_PORT));
        int port;
        if (value==null) {
            port= serverPort;
        } else {
            port=Integer.valueOf(value);
        }
        return port;
    }

    /**
     * 获取Rpc服务器IP地址
     * @return
     */
    public static String getServerHost(){
        String value=properties.getProperty(String.valueOf(RpcConfigEnum.SERVER_HOST));
        if (value==null) {
            value= serverHost;
        }
        return value;
    }

    /**
     * 获取CPU核心数 用于指定处理任务的线程数 没有指定则从系统获取
     * @return
     */
    public static int getServerCpuNum(){
        String value=properties.getProperty(String.valueOf(RpcConfigEnum.CPU_NUM));
        int cpuNum;
        if (value==null) {
            cpuNum= CpuUtil.getCpus();
        } else {
            cpuNum=Integer.parseInt(value);
        }
        return cpuNum+1;
    }


    /**
     * get zookeeper host
     * @return
     */
    public static String getZkHost() {

        String value=properties.getProperty(String.valueOf(RpcConfigEnum.ZK_ADDRESS));
        if (value==null) {
            value=zkHost;
        }
        return value;
    }

    /**
     * get connecting nums
     * @return
     */
    public static Integer getConnectNums() {

        String value=properties.getProperty(String.valueOf(RpcConfigEnum.CONNECT_NUM));
        if (value==null) {
            return connectNum;
        }
        return Integer.valueOf(value);
    }
}
