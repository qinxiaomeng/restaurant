package com.ej.restaurant.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PositionInfo extends DataEntity{
    private String name;
    private String merchantId;
    private int sort;
}
