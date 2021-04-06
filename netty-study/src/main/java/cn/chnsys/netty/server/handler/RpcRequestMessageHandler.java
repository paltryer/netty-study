package cn.chnsys.netty.server.handler;

import cn.chnsys.netty.message.RpcRequestMessage;
import cn.chnsys.netty.message.RpcResponseMessage;
import cn.chnsys.netty.server.service.HelloService;
import cn.chnsys.netty.server.service.ServicesFactory;
import com.sun.xml.internal.ws.util.ServiceFinder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * rpc 远程调用handler
 *
 * @author Wangchao
 * @version 1.0
 */

@Slf4j
@ChannelHandler.Sharable

public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    //SimpleChannelInboundHandler 关注某一种类型的入栈事件
    //这样获取的是这个类的类型
    //Class<?> aClass = Class.forName(message.getInterfaceName());


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage message) {
        RpcResponseMessage rpcResponseMessage = new RpcResponseMessage();
        rpcResponseMessage.setSequenceId(message.getSequenceId());
        try {
            //获取对象
            Object service = ServicesFactory.getService(Class.forName(message.getInterfaceName()));
            //获取要调用的方法
            Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
            //得到调用方法的返回值
            Object invoke = method.invoke(service, message.getParameterValue());
            rpcResponseMessage.setReturnValue(invoke);
        } catch (Exception e) {
            e.printStackTrace();
            //出现异常设置异常信息   这里返回异常信息不要全部返回，否则会超出最大字节限制
            rpcResponseMessage.setExceptionValue(new Exception("远程调用出错" + e.getCause().getMessage()));
        }
        //将rpc响应信息写入到channel中
        ctx.writeAndFlush(rpcResponseMessage);

    }


    /**
     * 测试远程调用代码  --无用
     */
    public static void main(String[] args) throws Exception {
        RpcRequestMessage message = new RpcRequestMessage(1,
                "cn.chnsys.netty.server.service.HelloService",
                "sayHello",
                String.class,
                new Class[]{String.class},
                new Object[]{"superhero!"});
        HelloService service = ServicesFactory.getService(Class.forName(message.getInterfaceName()));
        Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
        Object invoke = method.invoke(service, message.getParameterValue());
        System.out.println(invoke);


    }
}
