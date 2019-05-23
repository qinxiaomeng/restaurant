package com.ej.restaurant.mapper;


import com.ej.restaurant.model.MenuType;

import java.util.List;

public interface MenuTypeMapper extends BaseMapper<MenuType> {
    List<MenuType> listMenuTypeByMerchantId(String merchantId);
}
