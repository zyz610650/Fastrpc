package com.fastrpc.annontation.scanner.impl;

import com.fastrpc.annontation.scanner.Scanner;
import com.fastrpc.annotation.RpcReference;
import com.fastrpc.annotation.RpcService;
import com.fastrpc.utils.ScannerUtils;

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
