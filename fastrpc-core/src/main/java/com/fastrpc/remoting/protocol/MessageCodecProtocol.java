package com.fastrpc.remoting.protocol;

import com.fastrpc.remoting.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
 * 字节流里的数据都是按照 RpcProtocol协议里面参数定义的顺序往流里写的
 */
public class MessageCodecProtocol extends MessageToMessageCodec<ByteBuf, Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> list) throws Exception {
        ByteBuf buf=ctx.alloc().buffer();
        RpcProtocol rpcProtocol=RpcProtocol.getPolProtocol();
        //魔数 4byte
        buf.readBytes(rpcProtocol.MAGIC_NUMBER);
        // 版本 1byte
        buf.readBytes(rpcProtocol.getVERSION());
        // 消息类型 1byte
        buf.readBytes(msg.getMessageType());
        //消息压缩类型
        buf.readBytes(rpcProtocol.getCOMPRESSTYPE());
        //消息序列 服务器和客户端通信标识信息 4byte
        buf.readBytes(rpcProtocol.getSEQUENCEID());
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        ObjectOutputStream oos=new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();
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

        Class<? extends Message> message = Message.getMessageType(messageType);

        Object object=message.getClass();


    }
}
