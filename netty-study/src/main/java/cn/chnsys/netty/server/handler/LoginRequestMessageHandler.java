package cn.chnsys.netty.server.handler;

import cn.chnsys.netty.message.LoginRequestMessage;
import cn.chnsys.netty.message.LoginResponseMessage;
import cn.chnsys.netty.server.service.UserServiceFactory;
import cn.chnsys.netty.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Administrator
 * @version 1.0
 * @description TODO
 * @date 2021/4/1 14:42
 */
@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    //处理 loginRequest消息处理handler
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LoginRequestMessage message) throws Exception {
        String username = message.getUsername();
        String password = message.getPassword();
        boolean login = UserServiceFactory.getUserService().login(username, password);
        LoginResponseMessage responseMessage;
        if (login) {
            responseMessage = new LoginResponseMessage(true, "登录成功");
            //将用户和channel进行绑定
            SessionFactory.getSession().bind(channelHandlerContext.channel(), username);
        } else {
            responseMessage = new LoginResponseMessage(false, "登录失败！");
        }
        channelHandlerContext.writeAndFlush(responseMessage);
    }
}
