package com.ej.restaurant.controller;

import com.ej.restaurant.model.MenuInfo;
import com.ej.restaurant.result.DLResponseObject;
import com.ej.restaurant.service.MenuInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "菜单", description = "餐厅菜单")
@RestController
@RequestMapping("${wxApiPath}/menu")
public class MenuInfoController extends BaseController{

    @Autowired
    private MenuInfoService menuInfoService;

    @ApiOperation(value = "菜单列表")
    @GetMapping("allMenus")
    public DLResponseObject<List<MenuInfo>> getAllMenus(@RequestParam(required=true) String merchantId){
        List<MenuInfo> menus = menuInfoService.listMenuInfoByMerchantIdJoinTypeInfo(merchantId);
        return DLResponseObject.generate(menus);
    }
}
