package com.ej.restaurant.service;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "message")
public class MessageReceiver {

    @RabbitHandler
    public void process(String message){
        System.out.println("Receiver : " + message);
    }
}
