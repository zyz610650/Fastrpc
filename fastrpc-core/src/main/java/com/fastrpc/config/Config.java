package com.fastrpc.config;

import com.fastrpc.compress.impl.CompressImpl;
import com.fastrpc.enums.RpcConfigEnum;
import com.fastrpc.serializer.impl.SerializeImpl;
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

    static {
        try (InputStream in = Config.class.getResourceAsStream(String.valueOf(RpcConfigEnum.RPC_CONFIG_PATH))) {
            properties = new Properties();
            properties.load(in);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getServerPort(){
        String value=properties.getProperty(String.valueOf(RpcConfigEnum.SERVER_PORT));
        int port;
        if (value==null) {
            port= Integer.parseInt("8800");
        } else {
            port=Integer.valueOf(value);
        }
        return port;
    }

    public static String getServerHost(){
        String value=properties.getProperty(String.valueOf(RpcConfigEnum.SERVER_HOST));
        if (value==null) {
            value= "localhost";
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
            value="127.0.0.1:2181";
        }
        return value;
    }


}
