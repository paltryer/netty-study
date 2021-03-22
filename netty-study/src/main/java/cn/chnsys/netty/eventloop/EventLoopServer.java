package cn.chnsys.netty.eventloop;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import sun.rmi.runtime.Log;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * eventloop 服务器
 *
 * @author Wangchao
 * @version 1.0
 */
@Slf4j
public class EventLoopServer {


    public static void main(String[] args) {
        System.out.println(getInt());

    //细分2：创建一个独立的 EventLoopGroup
        EventLoopGroup group = new DefaultEventLoopGroup();

        new ServerBootstrap()
                //1.细分1：boos 只负责ServerSocketChannel 上accept事件  and worker 只负责SocketChannel 上的读写事件
                .group(new NioEventLoopGroup(),new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        channel.pipeline().addLast(group,"handler1",new ChannelInboundHandlerAdapter() {

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                //ByteBuf
                                ByteBuf buf = (ByteBuf) msg;
                                log.debug(buf.toString(Charset.defaultCharset()));
                            }
                        });
                    }
                })
                .bind(8080);
    }


    public static int getInt(){

        int i = 10;
        try {
            i = 11;
            return i;
        }finally {
            i = 12;
        }
    }


    

}
