package com.fastrpc.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * rpc框架中的参数配置
 * @author zyz
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public enum RpcConfigEnum {

    /**
     * rpc配置文件地址
     */
    RPC_CONFIG_PATH("/application.properties"),
    /**
     * 客户端端口号
     */
    CLIENT_PORT("rpc.client.port"),
    /**
     * 服务器端口号
     */
    SERVER_PORT("rpc.server.port"),
    /**
     * 服务器地址
     */
    SERVER_HOST("rpc.server.host"),
    /**
     * zookeeper host
     */
    ZK_ADDRESS("rpc.zookeeper.host"),
    /**
     *  线程数核数
     */
    CPU_NUM("rpc.cpunums"),

    /**
     * 限制服务器并发量
     */
    CONNECT_NUM("rpc.connect.nums");
    private String value;


}
