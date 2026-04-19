package com.purchase.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCreateDTO {

    /** 商品名称 */
    @NotBlank(message = "商品名称不能为空")
    private String productName;
    /** SKU编码 */
    @NotBlank(message = "SKU编码不能为空")
    private String skuCode;
    /** 分类编码 */
    @NotBlank(message = "分类不能为空")
    private String categoryNo;
    /** 采购价 */
    @NotNull(message = "采购价不能为空")
    @DecimalMin(value = "0.01", message = "采购价必须大于0")
    private BigDecimal purchasePrice;
    /** 商品描述 */
    private String description;
}
