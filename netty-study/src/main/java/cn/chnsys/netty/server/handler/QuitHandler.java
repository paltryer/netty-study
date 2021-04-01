package cn.chnsys.netty.server.handler;

import cn.chnsys.netty.server.session.Session;
import cn.chnsys.netty.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * 退出的handler
 *
 * @author wangchao
 * @version 1.0
 */
@Slf4j
@ChannelHandler.Sharable
public class QuitHandler extends ChannelInboundHandlerAdapter {

    //当连接断开时触发
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Session session = SessionFactory.getSession();
        session.unbind(ctx.channel());
        log.debug("{} 已经断开连接", ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Session session = SessionFactory.getSession();
        session.unbind(ctx.channel());
        log.debug("{} 异常断开连接，异常是{}", ctx.channel(), cause.getMessage());
    }
}
