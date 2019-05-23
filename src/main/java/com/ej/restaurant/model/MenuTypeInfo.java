package com.ej.restaurant.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuTypeInfo extends DataEntity{
    private String menuTypeId;
    private String menuInfoId;
}
