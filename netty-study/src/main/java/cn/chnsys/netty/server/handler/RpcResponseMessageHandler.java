package cn.chnsys.netty.server.handler;

import cn.chnsys.netty.message.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 用来 接收 rpc 远程调用的响应消息的 handler
 *
 * @author Wangchao
 * @version 1.0
 * @description TODO
 * @date 2021/4/5 0:32
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        //直接打印一下  方便验证
        log.debug("{}", msg);
        log.debug("you are my superhero ! —_-！");
    }
}
