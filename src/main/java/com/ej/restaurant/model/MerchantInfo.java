package com.ej.restaurant.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantInfo extends DataEntity{
    private String merchantName;
    private String certType;
    private String certNumber;
    private String ownerName;
    private String ownerCertType;
    private String ownerCertNumber;
    private String ownerMobile;
    private String busiAddr;
    private String regAddr;
    private String userId;
    private Long expDate;
}
