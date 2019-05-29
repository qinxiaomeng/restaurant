package com.ej.restaurant.service;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class HelloSender {
    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void send(){
        String context = "hello" + new Date();
        System.out.println("Sender : " + context);

        this.rabbitTemplate.convertAndSend("hello", context);
    }


    public void sendMessage(){
        String context = "message" + new Date();
        System.out.println("Sender : " + context);

        this.rabbitTemplate.convertAndSend("message", context);
    }

    //topic exchange

    public void send1(){
        String context = "hi, i am message 1";
        System.out.println("Sender : " + context);
        this.rabbitTemplate.convertAndSend("exchange", "topic.message", context);
    }

    public void send2() {
        String context = "hi, i am messages 2";
        System.out.println("Sender : " + context);
        this.rabbitTemplate.convertAndSend("exchange", "topic.messages", context);
    }

}
