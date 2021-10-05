package com.fastrpc.annontation.impl;

import com.fastrpc.annontation.Scanner;
import com.fastrpc.annotation.RpcReference;
import com.fastrpc.annotation.RpcService;
import com.fastrpc.annotation.ScannerUtils;

import java.util.Set;

/**
 * @author: @zyz
 */
public class ScannerJdkImpl implements Scanner {
    @Override
    public Set<Class<?>> scanService(String... packageNames) {
        for (String packageName: packageNames) {
            ScannerUtils.loadClassOrInterface(RpcService.class,packageName);
        }
        return ScannerUtils.getClass(RpcReference.class);
    }

    @Override
    public Set<Class<?>>  scanReference(String... packageNames) {
        return  null;
    }
}
