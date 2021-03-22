package com.atguigu.amqp;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class Springboot02AmqpApplicationTests {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {


        //rabbitTemplate.send(exchange,routingkey,message);

        //rabbitTemplate.convertAndSend(exchange,routingkey,object);
        Map<String,Object> map = new HashMap<>();
        map.put("msg","this is first message");
        map.put("data", Arrays.asList("helloworld",1,2,"paltryer"));
        rabbitTemplate.convertAndSend("exchange.direct","atguigu",map);

    }

    @Test
    public void receive(){
        Object atguigu = rabbitTemplate.receiveAndConvert("atguigu");
        System.out.println(atguigu.getClass());
        System.out.println(atguigu);
    }

}
