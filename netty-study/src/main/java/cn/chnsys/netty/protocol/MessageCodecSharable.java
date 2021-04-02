package cn.chnsys.netty.protocol;

import cn.chnsys.netty.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author 可分享的 消息编解码器
 * @version 1.0
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf,Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message message, List<Object> out) throws Exception {
        ByteBuf byteBuf = ctx.alloc().buffer();
        //1. 4字节的魔数
        byteBuf.writeBytes(new byte[]{1, 2, 3, 4});
        //2. 1字节的版本
        byteBuf.writeByte(1);
        //3. 1字节序列化方式 jdk 0， json 1
        byteBuf.writeByte(0);
        //4. 1字节的指令类型
        byteBuf.writeByte(message.getMessageType());
        //5. 4字节指令请求序号
        byteBuf.writeInt(message.getSequenceId());
        //无意义，对齐填充
        byteBuf.writeByte(0xff);
        //6. 获取内容的字节数组
        byte[] serialize = Serializer.Algorithm.Java.serialize(message);
        //7. 长度
        byteBuf.writeInt(serialize.length);
        //8. 写入内容
        byteBuf.writeBytes(serialize);
        out.add(byteBuf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {

        int magicNum = byteBuf.readInt();
        byte version = byteBuf.readByte();
        byte serializerType = byteBuf.readByte();
        byte messageType = byteBuf.readByte();
        int sequenceId = byteBuf.readInt();
        byteBuf.readByte();
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes,0,length);
        Message message = Serializer.Algorithm.Java.deserialize(Message.class, bytes);
        list.add(message);
    }
}
