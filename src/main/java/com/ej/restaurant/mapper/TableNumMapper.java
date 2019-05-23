package com.ej.restaurant.mapper;

import com.ej.restaurant.model.TableNum;

import java.util.List;

public interface TableNumMapper extends BaseMapper<TableNum> {
    List<TableNum> listTableInfoByMerchantId(String merchantId);
    List<TableNum> listTableInfoByPositionId(String positionId);
}
