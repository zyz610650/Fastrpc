package com.fastrpc.config;

import com.fastrpc.compress.impl.CompressImpl;
import com.fastrpc.serializer.impl.SerializeImpl;
import com.fastrpc.utils.CpuUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author zyz
 * @title:
 * @seq:
 * @address:
 * @idea:
 */
public class Config {

    static Properties properties;

    static {
        try (InputStream in = Config.class.getResourceAsStream("/application.properties")) {
            properties = new Properties();
            properties.load(in);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getServerPort(){
        String value=properties.getProperty("server.port");
        int port;
        if (value==null) {
            port= Integer.parseInt("8800");
        } else {
            port=Integer.valueOf(value);
        }
        return port;
    }

    public static String getServerHost(){
        String value=properties.getProperty("server.host");
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
        String value=properties.getProperty("windows.cpuNum");
        int cpuNum;
        if (value==null) {
            cpuNum= CpuUtil.getCpus();
        } else {
            cpuNum=Integer.parseInt(value);
        }
        return cpuNum+1;
    }

    /**
     * 获取使用的压缩算法
     * @return
     */
    public static CompressImpl.Algorithm getZipAlgorithm()
    {
            String value=properties.getProperty("server.zip");
            if (value==null) {
                value="Gzip";
            }
            return CompressImpl.Algorithm.valueOf(value);
    }

    /**
     * 获取序列化算法
     * @return
     */
    public static SerializeImpl.Algorithm getSerializeAlgorithm()
    {
        String value=properties.getProperty("server.serializer");
       if (value==null) {
           value="Kryo";
       }
        return SerializeImpl.Algorithm.valueOf(value);
    }


}
