package com.ej.restaurant.controller;

import com.ej.restaurant.model.MenuInfo;
import com.ej.restaurant.params.MenuInfoParam;
import com.ej.restaurant.result.DLResponseObject;
import com.ej.restaurant.service.MenuInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(value = "菜单", description = "餐厅菜单")
@RestController
@RequestMapping("/wx/menu")
@Slf4j
public class MenuInfoController extends BaseController{

    @Autowired
    private MenuInfoService menuInfoService;

    @ApiOperation(value = "菜单列表")
    @Cacheable(value = "allMenus", key = "#merchantId")
    @GetMapping("allMenus")
    public DLResponseObject<List<MenuInfo>> getAllMenus(@RequestParam(required=true) String merchantId){
        log.info(">>>>>>>>>>>>> {}", "我将执行方法获取数据");
        List<MenuInfo> menus = menuInfoService.listMenuInfoByMerchantIdJoinTypeInfo(merchantId);
        return DLResponseObject.generate(menus);
    }

    @ApiOperation(value = "添加新菜")
    @CacheEvict(value = "allMenus", key = "#merchantId", allEntries = true)
    @PostMapping("create")
    public DLResponseObject createMenu(@Valid MenuInfoParam menuInfoParam){
        menuInfoService.saveMenu(menuInfoParam.getMenuName(), menuInfoParam.getMerchantId(), menuInfoParam.getPrice(),
                menuInfoParam.getVipPrice(), menuInfoParam.getMenuImg(), menuInfoParam.getSort(), menuInfoParam.getRemarks());

        return DLResponseObject.success();
    }
}
