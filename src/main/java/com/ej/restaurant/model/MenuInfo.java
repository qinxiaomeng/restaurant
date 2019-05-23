package com.ej.restaurant.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuInfo extends DataEntity{
    private String menuName;
    private String merchantId;
    private BigDecimal price;
    private BigDecimal vipPrice;
    private String menuImg;
    private int sort;
    private String remarks;
}
