package cn.chnsys.netty.server.handler;

import cn.chnsys.netty.message.GroupChatRequestMessage;
import cn.chnsys.netty.message.GroupChatResponseMessage;
import cn.chnsys.netty.server.session.GroupSession;
import cn.chnsys.netty.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

/**
 * 聊天组 发送消息
 *
 * @author wangchao
 * @version 1.0
 */
@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage message) throws Exception {
        String content = message.getContent();
        String groupName = message.getGroupName();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        List<Channel> membersChannel = groupSession.getMembersChannel(groupName);
        for (Channel channel : membersChannel) {
            channel.writeAndFlush(new GroupChatResponseMessage(true, message.getFrom() + " 说：" + content));
        }
    }
}
