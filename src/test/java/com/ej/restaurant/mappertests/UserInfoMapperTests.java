package com.ej.restaurant.mappertests;

import com.ej.restaurant.mapper.UserInfoMapper;
import com.ej.restaurant.model.UserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserInfoMapperTests {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Test
    public void createTest(){
        UserInfo user = new UserInfo();
        user.preInsert();
        user.setMobile("22222222222");
        user.setLoginName("222222");
        user.setPassword("222222");

        userInfoMapper.insert(user);
    }
}
