package com.ej.restaurant.servicetests;

import com.ej.restaurant.service.HelloSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RabbitTests {

    @Autowired
    private HelloSender helloSender;

    @Test
    public void hello() throws Exception {
        helloSender.send();
        Thread.sleep(2000l);
    }

    @Test
    public void message() throws Exception {
        for (int i = 0; i < 50; i++) {
            helloSender.sendMessage();
        }
        Thread.sleep(10000l);
    }

    @Test
    public void topicMessageTest() throws Exception{
        helloSender.send1();
        Thread.sleep(2000l);
    }

    @Test
    public void topicMessagesTest() throws Exception{
        helloSender.send2();
        Thread.sleep(2000l);
    }
}
