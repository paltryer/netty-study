package cn.chnsys.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * hello netty Server
 *
 * @author Wangchao
 * @version 1.0
 */
public class HelloServer {

    public static void main(String[] args) {
        //1.启动器
        new ServerBootstrap().
                group(new NioEventLoopGroup())
                //选择ServerSocketChannel 的实现
                .channel(NioServerSocketChannel.class)
                .childHandler(
                        // channel 代表和客户端进行数据读写的通道Initializer 初始化 负责添加别的handler
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel channel) throws Exception {
                                channel.pipeline().addLast(new StringDecoder());
                                channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println(msg);
                                    }
                                });
                            }
                        }).bind(8080);
    }

}
