package com.ej.restaurant.service;

import com.ej.restaurant.mapper.MerchantInfoMapper;
import com.ej.restaurant.model.MerchantInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantInfoService extends BaseService<MerchantInfoMapper, MerchantInfo>{
    @Autowired
    MerchantInfoMapper merchantInfoMapper;

    public List<MerchantInfo> listMerchantInfoByUser(String userId){
        return merchantInfoMapper.listMerchantInfoByUser(userId);
    }

    public void addMerchantInfo(MerchantInfo merchantInfo){
        insert(merchantInfo);
    }
}
