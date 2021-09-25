package com.fastrpc.remoting.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

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
        this(RpcMessageProtocolConstants.MAX_FRAME_LENGTH,12,4,0,0);
//        log.debug("[FrameDecoder receive message===============]");
    }
    public FrameDecoderProtocol(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                                int lengthAdjustment, int initialBytesToStrip)
    {
        super( maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

}
