package com.ej.restaurant.service;

import com.ej.restaurant.enums.DLExceptionType;
import com.ej.restaurant.mapper.UserInfoMapper;
import com.ej.restaurant.model.UserInfo;
import com.ej.restaurant.params.UserInfoParam;
import com.ej.restaurant.utils.DLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
public class UserInfoService extends BaseService<UserInfoMapper, UserInfo> {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Transactional(readOnly = false)
    public void saveUser(String mobile, String password){
        UserInfo userInfo = new UserInfo();
        userInfo.setPassword(password);
        userInfo.setLoginName(mobile);
        userInfo.setMobile(mobile);
        insert(userInfo);
    }

    public UserInfo login(String loginName, String password){
        UserInfo userInfo = userInfoMapper.getUserByLoginName(loginName);
        if(userInfo == null || !password.equals(userInfo.getPassword()) ){
            throw new DLException(DLExceptionType.LOGINNAME_PW_ERROR);
        }
        return userInfo;
    }

    public List<UserInfo> listUserInfo(UserInfoParam params){
        List<UserInfo> userInfos = userInfoMapper.listUsers(params);
        return userInfos;
    }

    public void updateUserInfo(UserInfo userInfo){
        update(userInfo);
    }
}
