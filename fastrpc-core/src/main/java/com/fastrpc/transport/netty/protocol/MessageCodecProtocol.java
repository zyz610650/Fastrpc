package com.fastrpc.transport.netty.protocol;


import com.fastrpc.Exception.RpcException;
import com.fastrpc.compress.Compress;
import com.fastrpc.constants.RpcMessageProtocolConstants;
import com.fastrpc.enums.CompressTypeEnum;
import com.fastrpc.enums.SerializationTypeEnum;
import com.fastrpc.extension.ExtensionLoader;
import com.fastrpc.serializer.Serializer;
import com.fastrpc.transport.netty.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;


import java.util.Arrays;
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
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> list) {
        ByteBuf buf=ctx.alloc().buffer();
        //魔数 4byte
        buf.writeBytes(RpcMessageProtocolConstants.MAGIC_NUMBER);
        // 版本 1byte
        buf.writeByte(RpcMessageProtocolConstants.VERSION);
        // 字节的序列化方式 jdk 0 , json 1 kryo 2 1byte
        buf.writeByte(RpcMessageProtocolConstants.SERIALIZETYPE);
        // 消息类型 1byte
        buf.writeByte(msg.getMessageType());
        //消息压缩类型 1byte
        buf.writeByte(RpcMessageProtocolConstants.COMPRESSTYPE);
        //消息序列 服务器和客户端通信标识信息 4byte
        buf.writeInt(msg.getSeqId());
        //序列化和压缩
       // byte[] bytes =SerializeImpl.Algorithm.Kryo.serialize(msg);
        byte[] bytes = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension().serialize(msg);
        bytes = ExtensionLoader.getExtensionLoader(Compress.class).getExtension().compress(bytes);
        int len=bytes.length;
        //message length
        buf.writeInt(len);
        //message
        buf.writeBytes(bytes);

        list.add(buf);

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
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
        checkMagicNumber(maicNum);
        checkVersion(version);
        //解压
        bytes=ExtensionLoader.getExtensionLoader(Compress.class).getExtension(CompressTypeEnum.getName(compressType)).decompress(bytes);

        //反序列化
        Class<? extends Message>  clazz=Message.getMessageType(messageType);
        //Message msg= SerializeImpl.Algorithm.Kryo.deserialize(clazz,bytes);
       Message msg = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(SerializationTypeEnum.getName(serializeType)).deserialize(clazz, bytes);
//        log.debug("decode message: [{}{}{}{}{}{}]",maicNum,version,serializeType,messageType,compressType,seqId,len);
        log.debug("Message: [{}]", msg);
        list.add(msg);

    }

    /**
     * check version
     * @param version
     */
    private void checkVersion(Byte version)
    {
        if (version!=RpcMessageProtocolConstants.VERSION)
        {
            throw new RuntimeException("version isn't compatible" + version);
        }
    }

    /**
     * check magicNum
     * @param maicNum
     */
    private void checkMagicNumber(byte[] maicNum)
    {
        if (maicNum.length!=RpcMessageProtocolConstants.MAGIC_NUMBER.length) {
            throw new RpcException("Unknown magic code: " + Arrays.toString(maicNum));
        }
        for (int i=0;i<maicNum.length;i++)
        {
            if (maicNum[i]!=RpcMessageProtocolConstants.MAGIC_NUMBER[i]) {
                throw new RpcException ("Unknown magic code: " + Arrays.toString(maicNum));
            }
        }
    }
}
