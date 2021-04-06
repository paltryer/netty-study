package cn.chnsys.netty.server.handler;

import cn.chnsys.netty.message.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    // 用该map 来存储远程调用的返回值 根据sequenceId来区分       线程安全
    public static final Map<Integer, Promise<Object>> PROMISE = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        //直接打印一下  方便验证
        log.debug("{}", msg);

        //根据id网promise放数据   remove : 获取值，并且移除
        Promise<Object> promise = PROMISE.remove(msg.getSequenceId());
        if (promise != null) {
            if (msg.getExceptionValue() != null) {
                promise.setFailure(msg.getExceptionValue());
            } else {
                promise.setSuccess(msg.getReturnValue());
            }
        }

    }
}
