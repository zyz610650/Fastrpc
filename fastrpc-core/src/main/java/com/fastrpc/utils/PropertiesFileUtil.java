package com.fastrpc.utils;

import com.fastrpc.enums.RpcConfigEnum;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author zyz
 */

@Slf4j
@NoArgsConstructor

public class PropertiesFileUtil {
    private static  URL url;
    private static  Properties appProperties;

    static {
       url = Thread.currentThread().getContextClassLoader().getResource("/");
        /**
         * 单例 加载application.properties文件
         */
       appProperties = PropertiesFileUtil.getProperties(RpcConfigEnum.RPC_CONFIG_PATH.getValue());
    }


    /**
     * 读取Properties
     * @param fileName
     * @return
     */
    public static Properties getProperties(String fileName)
    {
        String rpcConfigPath=url.getPath()+fileName;
        try(InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(rpcConfigPath)){
            Properties properties=new Properties();
            properties.load(in);
            return properties;
        } catch (IOException e) {
       log.error("load Properties config file fail",e.getMessage());
       throw new RuntimeException(e.getCause());
        }
    }



}
