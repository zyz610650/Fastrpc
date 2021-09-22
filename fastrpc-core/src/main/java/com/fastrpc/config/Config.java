package com.fastrpc.config;

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
        if (value==null) port= Integer.parseInt("8800");
        else port=Integer.valueOf(value);
        return port;
    }

    /**
     * 获取CPU核心数 用于指定处理任务的线程数 没有指定则从系统获取
     * @return
     */
    public static int getServerCpuNum(){
        String value=properties.getProperty("windows.cpuNum");
        int cpuNum;
        if (value==null) cpuNum= CpuUtil.getCpus();
        else cpuNum=Integer.parseInt(value);
        return cpuNum;
    }


}
