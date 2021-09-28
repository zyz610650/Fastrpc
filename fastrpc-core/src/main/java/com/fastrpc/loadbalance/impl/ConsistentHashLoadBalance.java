package com.fastrpc.loadbalance.impl;



import org.apache.commons.lang.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author zyz
 */


public class ConsistentHashLoadBalance {
    private static String[] servers = {"192.168.0.0:111", "192.168.0.1:111",
            "192.168.0.2:111", "192.168.0.3:111", "192.168.0.4:111"};

    private static List<String> realNodes=new LinkedList<>();
    private static SortedMap<Integer,String> sortedMap=new TreeMap<>();
    private static final int NUM_HOST=5;

    static {
        for (int i=0;i<servers.length;i++)
        {
            realNodes.add(servers[i]);
        }
        for (String str:realNodes)
        {
            for (int i=1;i<=NUM_HOST;i++)
            {
                String nodeName=str+i;
                int hash=getHash(nodeName);
                sortedMap.put(hash,nodeName);
                System.out.println("虚拟节点hash:" + hash + "【" + nodeName + "】放入");
            }
        }
    }
    private static String getServer(String key)
    {
        int hash=getHash(key);
        String host;
        SortedMap<Integer,String> subMap=sortedMap.tailMap(hash);
        Integer index;
        if (subMap.isEmpty())
        {
             index=sortedMap.firstKey();
             host=sortedMap.get(index);
        }else {
             index=subMap.firstKey();
             host=subMap.get(index);
        }
        return host;
    }

    private static int getHash(String str) {

        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash ^ str.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 如果算出来的值为负数则取其绝对值
        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return hash;
    }

    public static void main(String[] args) {
        String[] keys = {"太阳", "月亮", "星星","JLU","HENY"};
        for(int i=0; i<keys.length; i++)
            System.out.println("[" + keys[i] + "]的hash值为" + getHash(keys[i])
                    + ", 被路由到结点[" + getServer(keys[i]) + "]");
    }


}
