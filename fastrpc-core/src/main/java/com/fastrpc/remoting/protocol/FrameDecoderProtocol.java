package com.fastrpc.remoting.protocol;

import com.fastrpc.remoting.message.Message;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

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
public class FrameDecoderProtocol extends LengthFieldBasedFrameDecoder  {


    public FrameDecoderProtocol()
    {
        this(RpcMessageProtocol.MAX_FRAME_LENGTH,12,4,0,0);

    }
    public FrameDecoderProtocol(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                                int lengthAdjustment, int initialBytesToStrip)
    {
        super( maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

}
