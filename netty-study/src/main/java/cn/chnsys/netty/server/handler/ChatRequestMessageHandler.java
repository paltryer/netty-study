package cn.chnsys.netty.server.handler;

import cn.chnsys.netty.message.ChatRequestMessage;
import cn.chnsys.netty.message.ChatResponseMessage;
import cn.chnsys.netty.server.session.Session;
import cn.chnsys.netty.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 聊天请求消息handler
 *
 * @author wangchao
 * @version 1.0
 * 继承simpleChannelInboundHandler可以使handler关注泛型中的类型消息
 */
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage message) throws Exception {
        String to = message.getTo();
        String content = message.getContent();
        Channel channel = SessionFactory.getSession().getChannel(to);
        if (channel != null) {
            //对方在线
            channel.writeAndFlush(new ChatResponseMessage(message.getFrom(), content));
        } else {
            //对方不在线
            ctx.writeAndFlush(new ChatResponseMessage(false, "对方不在线！"));
        }
    }
}
