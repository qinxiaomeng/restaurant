package com.ej.restaurant.service;

import com.ej.restaurant.mapper.MenuInfoMapper;
import com.ej.restaurant.model.MenuInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
public class MenuInfoService extends BaseService<MenuInfoMapper, MenuInfo> {

    @Transactional(readOnly = false)
    public void saveMenu(String menuName, String merchantId, BigDecimal price, BigDecimal vipPrice, String menuImg, int sort, String remarks){

        MenuInfo menu = new MenuInfo(menuName, merchantId, price, vipPrice, menuImg, sort, remarks);
        insert(menu);
    }

    public List<MenuInfo> listMenuInfoByMerchantIdJoinTypeInfo(String merchantId){
        return dao.listMenuInfoByMerchantIdJoinTypeInfo(merchantId);
    }
}
