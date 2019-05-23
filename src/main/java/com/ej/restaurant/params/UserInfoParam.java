package com.ej.restaurant.params;

import lombok.Data;

@Data
public class UserInfoParam extends PageParam {
    private String mobile;
    private String status;
}
