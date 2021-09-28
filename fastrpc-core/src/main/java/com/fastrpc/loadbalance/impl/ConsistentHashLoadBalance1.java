package com.fastrpc.loadbalance.impl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author zyz
 */

//https://www.zsythink.net/archives/1182
//    https://blog.csdn.net/guolong1983811/article/details/78715470
public class ConsistentHashLoadBalance1 {



    //hash 算法，也很关键
    private long hash(byte[] digest, int number) {

        return (
                (
                        //digest的第4(number 为 0时),8(number 为 1),12(number 为 2),16(number 为 3)字节，&0xFF后，左移24位
                        (long) (digest[3 + number * 4] & 0xFF) << 24
                )
                        |(
                        //digest的第3,7,11,15字节，&0xFF后，左移16位
                        (long) (digest[2 + number * 4] & 0xFF) << 16
                )
                        |(
                        //digest的第2,6,10,14字节，&0xFF后，左移8位
                        (long) (digest[1 + number * 4] & 0xFF) << 8
                )
                        |(
                        //digest的第1,5,9,13字节，&0xFF
                        digest[number * 4] & 0xFF
                )
        )
                & 0xFFFFFFFFL;
    }
    //返回16字节总共128bit位的MD5指纹签名byte[]。
    private byte[] md5(String value) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        md5.reset();
        byte[] bytes;
        try {
            bytes = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        md5.update(bytes);
        return md5.digest();
    }
}
