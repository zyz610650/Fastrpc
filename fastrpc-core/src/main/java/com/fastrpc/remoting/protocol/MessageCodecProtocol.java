package com.fastrpc.remoting.protocol;


import com.fastrpc.config.Config;
import com.fastrpc.remoting.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;


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
        log.debug("[================codec send message==========]");
        ByteBuf buf=ctx.alloc().buffer();
        //魔数 4byte
        buf.writeBytes(RpcMessageProtocol.MAGIC_NUMBER);
        // 版本 1byte
        buf.writeByte(RpcMessageProtocol.VERSION);
        // 字节的序列化方式 jdk 0 , json 1 kryo 2 1byte
        buf.writeByte(RpcMessageProtocol.SERIALIZETYPE);
        // 消息类型 1byte
        buf.writeByte(msg.getMessageType());
        //消息压缩类型 1byte
        buf.writeByte(RpcMessageProtocol.COMPRESSTYPE);
        //消息序列 服务器和客户端通信标识信息 4byte
        buf.writeInt(msg.getSeqId());
        //序列化和压缩
        byte[] bytes = Config.getSerializeAlgorithm().serialize(msg);
        bytes = Config.getZipAlgorithm().compress(bytes);
        int len=bytes.length;
        //数据长度
        buf.writeInt(len);
        //内容
        buf.writeBytes(bytes);
        System.out.println("发送");
        list.add(buf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
        log.debug("[================codec receive message==========]");
        byte[] maicNum =new byte[4];
        in.readBytes(maicNum,0,4);

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
