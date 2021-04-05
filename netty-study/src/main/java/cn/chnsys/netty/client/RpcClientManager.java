package cn.chnsys.netty.client;

import cn.chnsys.netty.message.RpcRequestMessage;
import cn.chnsys.netty.protocol.MessageCodecSharable;
import cn.chnsys.netty.protocol.ProcotolFrameDecoder;
import cn.chnsys.netty.protocol.SequenceIdGenerator;
import cn.chnsys.netty.server.handler.RpcResponseMessageHandler;
import cn.chnsys.netty.server.service.HelloService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.omg.PortableInterceptor.INACTIVE;

import java.lang.reflect.Proxy;

/**
 * rpc 远程调用的 clientManager
 *
 * @author Wangchao
 * @version 1.0
 */
@Slf4j
public class RpcClientManager {


    private static Channel channel = null;

    private static final Object LOCK = new Object();

    //获取唯一的channel 对象
    public static Channel getChannel() {
        //单例模式
        if (channel != null) {
            return channel;
        }
        synchronized (LOCK) {
            //互斥的双重校验锁
            if (channel != null) {
                return channel;
            }
            initChannel();
            return channel;
        }
    }

    /**
     * 初始化 channel
     */
    private static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler();
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        RpcResponseMessageHandler rpcResponseMessageHandler = new RpcResponseMessageHandler();
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

        try {
            channel = bootstrap.connect("localhost", 8080).sync().channel();

            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("client error", e);
        }
    }

    //测试由channelmanager管理的channel是否能够发送消息
    public static void main(String[] args) {
        HelloService proxyService = getProxyService(HelloService.class);
        proxyService.sayHello("superhero!");
    }

    //创建代理类 代理调用方法
    public static <T> T getProxyService(Class<T> serviceClass) {

        ClassLoader loader = serviceClass.getClassLoader();
        Class<?>[] interfaces = new Class[]{serviceClass};
        Object o = Proxy.newProxyInstance(loader, interfaces, (proxy, method, args) -> {
            //1.将方法调用转换为 RpcRequestMessage 消息对象
            //2.将消息对象发送出去
            RpcRequestMessage rpcRequestMessage = new RpcRequestMessage(SequenceIdGenerator.nextId(),
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args);
            getChannel().writeAndFlush(rpcRequestMessage);
            //3.暂时返回 null
            return null;
        });
        return (T) o;
    }

}
