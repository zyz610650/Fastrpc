package com.fastrpc.compress.impl;

import com.fastrpc.compress.Compress;
import com.fastrpc.serializer.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author zyz
 * @title:
 * @seq:
 * @address:
 * @idea:
 */

public class CompressImpl {
    private static final int BUFFER_SIZE = 1024 * 4;
    public enum Algorithm implements Compress{
        Gzip{
            @Override
            public byte[] compress(byte[] bytes) {
                if (bytes==null) throw new RuntimeException("bytes is null");

                try (ByteArrayOutputStream out=new ByteArrayOutputStream();
                     GZIPOutputStream gzip = new GZIPOutputStream(out)){
                    gzip.write(bytes);
                    gzip.flush();
                    gzip.finish();
                    return out.toByteArray();
                } catch (IOException e) {
                    throw new RuntimeException("gzip compress error", e);
                }
            }

            @Override
            public byte[] decompress(byte[] bytes) {
                if (bytes == null) {
                    throw new NullPointerException("bytes is null");
                }
                try (ByteArrayOutputStream out=new ByteArrayOutputStream();
                     GZIPInputStream gzip=new GZIPInputStream(new ByteArrayInputStream(bytes))){
                    byte[] buffer=new byte[BUFFER_SIZE];
                    int n;
                    while ((n=gzip.read(buffer))>-1){
                        out.write(buffer,0,n);
                    }
                    return out.toByteArray();
                } catch (IOException e) {
                    throw new RuntimeException("gzip decompress error", e);
                }
            }
        }
    }
}
