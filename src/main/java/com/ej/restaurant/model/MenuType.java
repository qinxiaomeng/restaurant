package com.ej.restaurant.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuType extends DataEntity{
    private String typeName;
    private String merchantId;
    private int sort;
}
