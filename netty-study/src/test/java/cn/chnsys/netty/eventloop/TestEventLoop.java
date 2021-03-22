package cn.chnsys.netty.eventloop;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * EventLoop test
 *
 * @author Wangchao
 * @version 1.0
 */
@Slf4j
public class TestEventLoop {
    public static void main(String[] args) {
        //1.创建时间循环组
        EventLoopGroup group = new NioEventLoopGroup(2);//io 事件，普通任务，定时任务
        EventLoopGroup defaultEventLoopGroup = new DefaultEventLoopGroup();//普通任务，定时任务
        //System.out.println(NettyRuntime.availableProcessors());


        //2.获取下一个事件循环对象
//        System.out.println(group.next());
//        System.out.println(group.next());
//        System.out.println(group.next());

        //3.执行普通任务
        group.next().submit(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("ok");
        });

        //4.执行定时任务
        group.next().scheduleAtFixedRate(()->{
           log.debug("ok");
        },0,1, TimeUnit.SECONDS);
        log.debug("main");


    }


}
