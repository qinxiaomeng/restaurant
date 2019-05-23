package com.ej.restaurant.mapper;


import com.ej.restaurant.model.MenuInfo;

import java.util.List;

public interface MenuInfoMapper extends BaseMapper<MenuInfo> {
    List<MenuInfo> listMenuInfoByMerchantIdJoinTypeInfo(String merchantId);
    List<MenuInfo> listMenuInfoByTypeId(String typeId);
}
