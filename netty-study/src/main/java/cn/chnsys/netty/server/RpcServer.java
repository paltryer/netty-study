package cn.chnsys.netty.server;

import cn.chnsys.netty.protocol.MessageCodecSharable;
import cn.chnsys.netty.protocol.ProcotolFrameDecoder;
import cn.chnsys.netty.server.handler.RpcRequestMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * rpc 远程调用的server
 *
 * @author Wangchao
 * @version 1.0
 */
@Slf4j
public class RpcServer {

    public static void main(String[] args) {

        //netty提供的日志handler
        LoggingHandler loggingHandler = new LoggingHandler();
        //自定义的可共享的 编码器 的handler
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        //自定义的处理rpcRuequestMessage的Handler
        RpcRequestMessageHandler rpcRequestMessageHandler = new RpcRequestMessageHandler();

        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap().group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            //解决粘包，半包问题的 LengthFieldBasedFrameDecoder 的子类 规定解码的长度、偏移量的
                            channel.pipeline().addLast(new ProcotolFrameDecoder());
                            channel.pipeline().addLast(loggingHandler);
                            channel.pipeline().addLast(messageCodecSharable);
                            channel.pipeline().addLast(rpcRequestMessageHandler);
                        }
                    });
            Channel channel = serverBootstrap.bind(8080).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }


    }
}
