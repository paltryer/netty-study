package cn.chnsys.netty.client;

import cn.chnsys.netty.message.RpcRequestMessage;
import cn.chnsys.netty.protocol.MessageCodecSharable;
import cn.chnsys.netty.protocol.ProcotolFrameDecoder;
import cn.chnsys.netty.server.handler.RpcResponseMessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

/**
 * rpc 远程调用的 client
 *
 * @author Wangchao
 * @version 1.0
 */
public class RpcClient {

    /**
     * 1.创建远程连接
     * 2.初始化的时候添加handler
     * 3.同步的获取channel
     * 4.向channel中发送 RpcRequestMessage 消息内容
     * 5.服务器的处理 RpcRequestMessage 的 RpcRequestMessageHandler 被调用执行
     * 6.反射创建对象，调用方法
     * 7.将返回值包装成 RpcResponseMessage 发送回客户端
     * 8.客户端的处理 RpcResponseMessage 的 RpcResponseMessageHandler 被调用，接收到消息
     * <p>
     * 存在问题：如果调用失败，返回Exception时，会出现1024个字节不够的情况
     */


    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler();
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        RpcResponseMessageHandler rpcResponseMessageHandler = new RpcResponseMessageHandler();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new ProcotolFrameDecoder());
                    pipeline.addLast(loggingHandler);
                    pipeline.addLast(messageCodecSharable);
                    pipeline.addLast(rpcResponseMessageHandler);
                }
            });

            Channel localhost = bootstrap.connect("localhost", 8080).sync().channel();
            localhost.writeAndFlush(new RpcRequestMessage(1,
                    "cn.chnsys.netty.server.service.HelloService",
                    "sayHello",
                    String.class,
                    new Class[]{String.class},
                    new Object[]{"superhero!"}));
            localhost.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
