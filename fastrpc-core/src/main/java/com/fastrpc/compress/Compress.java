package com.fastrpc.compress;

import com.fastrpc.enums.CompressTypeEnum;
import com.fastrpc.extension.SPI;

/**
 * @author zyz
 * @title:
 * @seq:
 * @address:
 * @idea:
 */
@SPI("gzip")
public interface Compress {

    byte[] compress(byte[] bytes);

    byte[] decompress(byte[] bytes);
}
