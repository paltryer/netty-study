package cn.chnsys.netty.server;

import cn.chnsys.netty.protocol.MessageCodecSharable;
import cn.chnsys.netty.protocol.ProcotolFrameDecoder;
import cn.chnsys.netty.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 聊天服务器
 *
 * @author wangchao
 * @version 1.0
 */
@Slf4j
public class ChatServer {

    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        //日志handler可以被多个channel共享
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        //处理消息的handler
        ChatRequestMessageHandler CHAT_REQUEST_HANDLER = new ChatRequestMessageHandler();
        GroupChatRequestMessageHandler GROUP_CHAT_HANDLER = new GroupChatRequestMessageHandler();
        GroupCreateRequestMessageHandler GROUP_CREATE_HANDLER = new GroupCreateRequestMessageHandler();
        GroupMembersRequestMessageHandler GROUP_MEMBERS_HANDLER = new GroupMembersRequestMessageHandler();
        GroupQuitRequestMessageHandler GROUP_QUIT_HANDLER = new GroupQuitRequestMessageHandler();
        LoginRequestMessageHandler LOGIN_REQUEST_HANDLER = new LoginRequestMessageHandler();
        QuitHandler QUIT_HANDLER = new QuitHandler();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    //自定义编解码器
                    pipeline.addLast(new ProcotolFrameDecoder());

                    //用来判断是不是 读空闲时间过长，或 写空闲时间过长； 5s 内 如果没有收到channel的数据 会产生IdleState#READER_IDLE 事件
                    pipeline.addLast(new IdleStateHandler(5, 0, 0));
                    //双向的处理空闲时间过长handler  ChannelDuplexHandler 可以同时作为入站和出站的处理器
                    pipeline.addLast(new ChannelDuplexHandler() {
                        //用来触发特殊事件  userEventTriggered
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event = (IdleStateEvent) evt;
                            if (event.state() == IdleState.READER_IDLE) {
                                //触发了读空闲事件
                                log.debug("读空闲已经超过五秒！！！");
                                ctx.channel().close();
                            }
                        }
                    });
                    pipeline.addLast(LOGGING_HANDLER);
                    pipeline.addLast(MESSAGE_CODEC);
                    pipeline.addLast(LOGIN_REQUEST_HANDLER);
                    pipeline.addLast(CHAT_REQUEST_HANDLER);
                    pipeline.addLast(GROUP_CHAT_HANDLER);
                    pipeline.addLast(GROUP_CREATE_HANDLER);
                    pipeline.addLast(GROUP_MEMBERS_HANDLER);
                    pipeline.addLast(GROUP_QUIT_HANDLER);
                    pipeline.addLast(QUIT_HANDLER);
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
