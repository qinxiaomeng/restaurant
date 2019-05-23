package com.ej.restaurant.mapper;

import com.ej.restaurant.model.PositionInfo;

import java.util.List;

public interface PositionInfoMapper extends BaseMapper<PositionInfo> {
    List<PositionInfo> listPositionByMerchantId(String merchantId);
}
