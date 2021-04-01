package cn.chnsys.netty.server.handler;

import cn.chnsys.netty.message.GroupChatResponseMessage;
import cn.chnsys.netty.message.GroupCreateRequestMessage;
import cn.chnsys.netty.message.GroupCreateResponseMessage;
import cn.chnsys.netty.server.session.Group;
import cn.chnsys.netty.server.session.GroupSession;
import cn.chnsys.netty.server.session.GroupSessionFactory;
import cn.chnsys.netty.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Set;

/**
 * 创建群 handler
 *
 * @author wangchao
 * @version 1.0
 */
@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {

    //当收到消息类型为GroupCreateRequestMessage类型的消息时被调用
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage message) throws Exception {
        String groupName = message.getGroupName();
        Set<String> members = message.getMembers();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);
        if (group == null) {
            //发送成功进群消息
            List<Channel> membersChannel = groupSession.getMembersChannel(groupName);
            for (Channel channel : membersChannel) {
                channel.writeAndFlush(new GroupCreateResponseMessage(true, "您已被拉入群：" + groupName));
            }
            ctx.writeAndFlush(new GroupChatResponseMessage(true, groupName + ":群创建成功！"));

        } else {
            ctx.writeAndFlush(new GroupChatResponseMessage(false, groupName + ":群已存在！"));
        }

    }
}
