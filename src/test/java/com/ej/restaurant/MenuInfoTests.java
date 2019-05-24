package com.ej.restaurant;

import com.ej.restaurant.model.MenuInfo;
import com.ej.restaurant.service.MenuInfoService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MenuInfoTests {

    @Autowired
    private MenuInfoService menuInfoService;

    @Test
    public void createNewMenu(){

        String imgStr = "https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1558659998&di=12fb6e345c6c23088e685d9ad5dd10a2&src=http://pic0.huitu.com/res/20170716/1367773_20170716140534961121_1.jpg";

        menuInfoService.saveMenu("红烧仔鸡", "123", new BigDecimal(55.00f), new BigDecimal(50.00f), imgStr, 1, "推荐");
        menuInfoService.saveMenu("红烧腐竹", "123", new BigDecimal(23.00f), new BigDecimal(20.00f), imgStr, 1, "推荐");
    }

    @Test
    public void listMenuTest(){
        List<MenuInfo> menus = menuInfoService.listMenuInfoByMerchantIdJoinTypeInfo("123");

        Assert.assertEquals(3, menus.size());
    }
}
