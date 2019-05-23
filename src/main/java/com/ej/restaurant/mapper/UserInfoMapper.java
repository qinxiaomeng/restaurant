package com.ej.restaurant.mapper;

import com.ej.restaurant.model.UserInfo;
import com.ej.restaurant.params.UserInfoParam;

import java.util.List;

public interface UserInfoMapper extends BaseMapper<UserInfo> {
    List<UserInfo> listUsers(UserInfoParam userInfoParam);
    Integer getCount(UserInfoParam userInfoParam);
    UserInfo getUserByLoginName(String loginName);
}
