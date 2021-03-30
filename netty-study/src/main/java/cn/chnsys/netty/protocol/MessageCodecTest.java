package cn.chnsys.netty.protocol;

import cn.chnsys.netty.message.LoginRequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author Administrator
 * @version 1.0
 * @description message codec test 自定义消息编码器 测试类
 * @date 2021/3/30 14:49
 */
public class MessageCodecTest {

    public static void main(String[] args) throws Exception {
        LengthFieldBasedFrameDecoder FRAME_DECODER = new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0);
        LoggingHandler LOGGING_HANDLER = new LoggingHandler();
        EmbeddedChannel channel = new EmbeddedChannel(
                FRAME_DECODER,
                //LengthFieldBasedFrameDecoder 解决粘包，半包问题
                LOGGING_HANDLER,
                new MessageCodec());
        LoginRequestMessage loginRequestMessage = new LoginRequestMessage("zhangsan", "superhero");
        channel.writeOutbound(loginRequestMessage);

        //decode
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, loginRequestMessage, buffer);
        ByteBuf slice1 = buffer.slice(0, 100);
        ByteBuf slice2 = buffer.slice(100, buffer.readableBytes() - 100);
        slice1.retain();
        channel.writeInbound(slice1);
        channel.writeInbound(slice2);
    }
}
