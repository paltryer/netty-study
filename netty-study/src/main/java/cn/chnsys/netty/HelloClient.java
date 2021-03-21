package cn.chnsys.netty;

import com.sun.corba.se.internal.CosNaming.BootstrapServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

/**
 * hello client
 *
 * @author Wangchao
 * @version 1.0
 */
public class HelloClient {

    public static void main(String[] args) throws InterruptedException {
        //1.启动类
        new Bootstrap()
                //添加 EventLoop
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)

                //添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override//在连接建立后被调用
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        //添加字符转换器
                        channel.pipeline().addLast(new StringEncoder());

                    }
                })
                .connect(new InetSocketAddress("localhost", 8080))
                .sync()
                .channel()
                //向服务器发送数据
                .writeAndFlush("you are my superhero!");

    }
}
