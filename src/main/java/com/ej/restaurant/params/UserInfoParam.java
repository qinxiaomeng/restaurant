package com.ej.restaurant.params;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserInfoParam {
    @NotEmpty
    private String mobile;
    private String loginName;
    @NotEmpty
    private String password;
    private String rearks;
}
