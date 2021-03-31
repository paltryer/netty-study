package cn.chnsys.netty.client;

import cn.chnsys.netty.message.LoginRequestMessage;
import cn.chnsys.netty.protocol.MessageCodecSharable;
import cn.chnsys.netty.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Scanner;

/**
 * @author wangchao
 * @version 1.0
 * @description 聊天客户端
 * @date 2021/3/31 17:43
 */
@Slf4j
public class ChatClient {

    public static void main(String[] args) {

        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler();
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast("client handler", new ChannelInboundHandlerAdapter() {
                        //连接建立后 触发 active 事件
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            new Thread(() -> {
                                //用来接收用户控制台输入
                                Scanner scanner = new Scanner(System.in);
                                System.out.println("请输入用户名：");
                                String username = scanner.nextLine();
                                System.out.println("请输入密码：");
                                String password = scanner.nextLine();
                                LoginRequestMessage message = new LoginRequestMessage(username, password);
                                //发送消息，首先经过messageCodec编码发送 -> 记录日志
                                ctx.writeAndFlush(message);
                                System.out.println("等待后续操作。。");
                                try {
                                    System.in.read();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }, "system in").start();
                        }

                        //接收服务器响应的数据
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.debug("msg:{}", msg);
                        }
                    });
                }
            });
            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }

    }

}
