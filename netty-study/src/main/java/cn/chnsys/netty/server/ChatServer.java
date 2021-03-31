package cn.chnsys.netty.server;

import cn.chnsys.netty.message.LoginRequestMessage;
import cn.chnsys.netty.message.LoginResponseMessage;
import cn.chnsys.netty.protocol.MessageCodecSharable;
import cn.chnsys.netty.protocol.ProcotolFrameDecoder;
import cn.chnsys.netty.server.service.UserServiceFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 聊天服务器
 *
 * @author wangchao
 * @version 1.0
 */
public class ChatServer {

    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        //日志handler可以被多个channel共享
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(new ProcotolFrameDecoder());
                    pipeline.addLast(LOGGING_HANDLER);
                    pipeline.addLast(MESSAGE_CODEC);
                    pipeline.addLast(new SimpleChannelInboundHandler<LoginRequestMessage>() {
                        //处理 loginRequest消息处理handler
                        @Override
                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, LoginRequestMessage message) throws Exception {
                            String username = message.getUsername();
                            String password = message.getPassword();
                            boolean login = UserServiceFactory.getUserService().login(username, password);
                            LoginResponseMessage responseMessage;
                            if (login) {
                                responseMessage = new LoginResponseMessage(true, "登录成功");
                            } else {
                                responseMessage = new LoginResponseMessage(false, "登录失败！");
                            }
                            channelHandlerContext.writeAndFlush(responseMessage);
                        }
                    });
                }
            });
            ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
