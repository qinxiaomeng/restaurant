package com.ej.restaurant.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo extends DataEntity {
    //private String id;
    private String loginName;
    private String mobile;
    private String password;
    //private Long createDate;
    //private DataStatus status;
}
