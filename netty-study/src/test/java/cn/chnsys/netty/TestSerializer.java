package cn.chnsys.netty;

import cn.chnsys.netty.message.LoginRequestMessage;
import cn.chnsys.netty.protocol.MessageCodecSharable;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 测试编解码器是否生效
 *
 * @author Wangchao
 * @version 1.0
 */
public class TestSerializer {

    public static void main(String[] args) {
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        LoggingHandler loggingHandler = new LoggingHandler();
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(loggingHandler, messageCodecSharable, loggingHandler);
        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123456");
        embeddedChannel.writeOutbound(message);


    }
}
