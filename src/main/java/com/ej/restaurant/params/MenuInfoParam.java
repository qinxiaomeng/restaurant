package com.ej.restaurant.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuInfoParam {
    @NotEmpty(message="菜单名称不能为空")
    private String menuName;
    @NotEmpty(message="商户标识不能为空")
    private String merchantId;
    @NotNull
    @Min(value = 1, message = "价格必须大于1")
    private BigDecimal price;
    @NotNull
    @Min(value = 1, message = "会员价格必须大于1")
    private BigDecimal vipPrice;
    private String menuImg;
    private int sort;
    private String remarks;
}
