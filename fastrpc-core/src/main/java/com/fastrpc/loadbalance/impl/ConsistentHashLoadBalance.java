package com.fastrpc.loadbalance.impl;

import com.fastrpc.loadbalance.AbstractLoadBalance;
import com.fastrpc.transport.netty.message.RpcRequestMessage;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author zyz
 * Ketama算法
 */

public class ConsistentHashLoadBalance extends AbstractLoadBalance {

    // Key: rpcServiceName
    private final ConcurrentMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();
    @Override
    protected String doSelect(List<String> serviceAddresses, RpcRequestMessage msg) {
        // 获取调用服务名
        String rpcServiceName = msg.getInterfaceName();
        // 生成调用列表hashCode
        int identityHashCode = System.identityHashCode(rpcServiceName);
        // 以调用rpcServiceName名为key,获取一致性hash选择器
        ConsistentHashSelector selector = selectors.get(rpcServiceName);
        // 若不存在则创建新的选择器
        if (selector == null || selector.getIdentityHashCode() != identityHashCode) {
            // 创建ConsistentHashSelector时会生成所有虚拟结点
            selectors.put(rpcServiceName, new ConsistentHashSelector(serviceAddresses,identityHashCode));
            // 获取选择器
            selector = selectors.get(rpcServiceName);
        }
        // 选择结点
        return selector.select(msg);
    }



    private static final class ConsistentHashSelector {

        private final TreeMap<Long, String> virtualInvokers; // 虚拟结点

        private final int replicaNumber = 160;   // 副本数

        private final int identityHashCode;// hashCode

//        private final int[]                     argumentIndex;   // 参数索引数组

        public ConsistentHashSelector(List<String> invokers, int identityHashCode) {
            // 创建TreeMap 来保存结点
            this.virtualInvokers = new TreeMap<>();
            // 生成调用结点HashCode
            this.identityHashCode = System.identityHashCode(invokers);

            // 创建虚拟结点
            // 对每个invoker生成replicaNumber个虚拟结点，并存放于TreeMap中
            for (String invoker : invokers) {

                for (int i = 0; i < replicaNumber / 4; i++) {
                    // 根据md5算法为每4个结点生成一个消息摘要，摘要长为16字节128位。 md5就是一个长16字节占128位的bit数组
                    //这里的意思就是每个节点扩展未160个虚拟节点，然后将虚拟节点分组 4 个一组，
                    //4个的原因是 md5共16字节 ,这一个组里的每个虚拟节点占用生成的md5数组中的4个字节
                    //正好4*4 所以分为4个一组
                    byte[] digest = md5(invoker + i);
                    // 随后将128位分为4部分，0-31,32-63,64-95,95-128，并生成4个32位数，存于long中，long的高32位都为0 long64位
                    // 并作为虚拟结点的key。
                    for (int h = 0; h < 4; h++) {
                        long m = hash(digest, h);
                        virtualInvokers.put(m, invoker);
                    }
                }
            }
        }

        public int getIdentityHashCode() {
            return identityHashCode;
        }

        // 选择结点
        public String select(RpcRequestMessage rpcRequestMessage) {
            // 根据调用参数来生成Key

            String key = toKey(rpcRequestMessage);
            // 根据这个参数生成消息摘要
            byte[] digest = md5(key);
            //调用hash(digest, 0)，将消息摘要转换为hashCode，这里仅取0-31位来生成HashCode
            //调用sekectForKey方法选择结点。
            String invoker = sekectForKey(hash(digest, 0));
            return invoker;
        }

        private String toKey(RpcRequestMessage msg) {
            StringBuilder buf = new StringBuilder();
            // 由于hash.arguments没有进行配置，因为只取方法的第1个参数作为key
            buf.append(msg.getMethodName());
            Object[] parameters = msg.getParameters();
            for (Object o : parameters) {
                buf.append(o);
            }
            return buf.toString();
        }

        //根据hashCode选择结点
        private String sekectForKey(long hash) {
            String invoker;
            Long key = hash;
            // 若HashCode直接与某个虚拟结点的key一样，则直接返回该结点
            if (!virtualInvokers.containsKey(key)) {
                // 若不一致，找到一个最小上届的key所对应的结点。
                SortedMap<Long, String> tailMap = virtualInvokers.tailMap(key);
                // 若存在则返回，例如hashCode落在图中[1]的位置
                // 若不存在，例如hashCode落在[2]的位置，那么选择treeMap中第一个结点
                // 使用TreeMap的firstKey方法，来选择最小上界。
                if (tailMap.isEmpty()) {
                    key = virtualInvokers.firstKey();
                } else {

                    key = tailMap.firstKey();
                }
            }
            invoker = virtualInvokers.get(key);
            return invoker;
        }

        private long hash(byte[] digest, int number) {
            return (((long) (digest[3 + number * 4] & 0xFF) << 24)
                    | ((long) (digest[2 + number * 4] & 0xFF) << 16)
                    | ((long) (digest[1 + number * 4] & 0xFF) << 8)
                    | (digest[0 + number * 4] & 0xFF))
                    & 0xFFFFFFFFL;
        }

        private byte[] md5(String value) {
            MessageDigest md5;
            try {
                md5 = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            md5.reset();
            byte[] bytes = null;
            try {
                bytes = value.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            md5.update(bytes);
            return md5.digest();
        }

    }
}
