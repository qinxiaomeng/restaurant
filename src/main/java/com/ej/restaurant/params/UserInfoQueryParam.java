package com.ej.restaurant.params;

import lombok.Data;

@Data
public class UserInfoQueryParam extends PageParam {
    private String mobile;
    private String status;
}
