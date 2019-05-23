package com.ej.restaurant.mapper;


import com.ej.restaurant.model.MerchantInfo;

import java.util.List;

public interface MerchantInfoMapper extends BaseMapper<MerchantInfo> {
    List<MerchantInfo> listMerchantInfoByUser(String userId);
}
