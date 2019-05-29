package com.ej.restaurant.servicetests;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisServiceTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testString(){
        redisTemplate.opsForValue().set("qxm", "秦晓萌");
    }

    @Test
    public void getStringTest(){
        String s = (String) redisTemplate.opsForValue().get("simon");
        Assert.assertEquals("秦晓萌", s);
    }



}
