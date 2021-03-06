package cn.chnsys.netty.protocol;

import cn.chnsys.netty.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @description 自定义编解码器
 * @date 2021/3/29 17:30
 */
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {

    /**
     * 出栈前将msg 编码成byteBuf
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
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
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(message);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        //7. 长度
        byteBuf.writeInt(bytes.length);
        //8. 写入内容
        byteBuf.writeBytes(bytes);
    }

    /**
     * 解码 将byteBuf转换为message
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int magicNum = byteBuf.readInt();
        byte version = byteBuf.readByte();
        byte serializerType = byteBuf.readByte();
        byte messageType = byteBuf.readByte();
        int sequenceId = byteBuf.readInt();
        byteBuf.readByte();
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes,0,length);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message message = (Message) objectInputStream.readObject();
        log.debug("{}",message);
        list.add(message);
    }
}
