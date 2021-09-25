package com.fastrpc.remoting.protocol;

import com.fastrpc.remoting.message.Message;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteOrder;

/**
 * @author zyz
 * @title:
 * @seq:
 * @address:
 * @idea:
 */

/**
 * 用于处理半包粘包
 */
@Slf4j
public class FrameDecoderProtocol extends LengthFieldBasedFrameDecoder  {


    public FrameDecoderProtocol()
    {
        this(RpcMessageProtocol.MAX_FRAME_LENGTH,12,4,0,0);
//        log.debug("[FrameDecoder receive message===============]");
    }
    public FrameDecoderProtocol(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                                int lengthAdjustment, int initialBytesToStrip)
    {
        super( maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

}
