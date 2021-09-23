package com.fastrpc.remoting.protocol;


import com.fastrpc.config.Config;
import com.fastrpc.remoting.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author zyz
 * @title:
 * @seq:
 * @address:
 * @idea:
 */

/**
 * 消息编码器
 * 字节流里的数据都是按照 RpcProtocol协议里面参数定义的顺序往流里写的
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodecProtocol extends MessageToMessageCodec<ByteBuf, Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> list) throws Exception {
        ByteBuf buf=ctx.alloc().buffer();
        //魔数 4byte
        buf.readBytes(RpcMessageProtocol.MAGIC_NUMBER);
        // 版本 1byte
        buf.readBytes(RpcMessageProtocol.VERSION);
        // 消息类型 1byte
        buf.readBytes(msg.getMessageType());
        //消息压缩类型
        buf.readBytes(RpcMessageProtocol.COMPRESSTYPE);
        //消息序列 服务器和客户端通信标识信息 4byte
        buf.readBytes(msg.getSeqId());
        //序列化和压缩
        byte[] bytes = Config.getSerializeAlgorithm().serialize(msg);
        bytes = Config.getZipAlgorithm().compress(bytes);
        int len=bytes.length;
        //数据长度
        buf.writeInt(len);
        //内容
        buf.writeBytes(bytes);
        list.add(buf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
        String maicNum = in.readCharSequence(4, Charset.forName("UTF-8")).toString();
        Byte version=in.readByte();
        Byte serializeType=in.readByte();
        Byte messageType=in.readByte();
        Byte compressType=in.readByte();
        int seqId=in.readInt();
        int len=in.readInt();
        byte[] bytes=new byte[len];
        in.readBytes(bytes,0,len);
        //解压
        bytes=Config.getZipAlgorithm().decompress(bytes);
        //反序列化
        Class<? extends Message>  clazz=Message.getMessageType(messageType);
        Message msg = (Message) Config.getSerializeAlgorithm().deserialize(clazz, bytes);
        log.debug("decode message: [{}{}{}{}{}{}]",maicNum,version,serializeType,messageType,compressType,seqId,len);
        log.debug("message: [{}]", msg);
        list.add(msg);


    }
}
