package com.purchase.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVO {

    /** 商品ID */
    private Long id;
    /** 商品名称 */
    private String productName;
    /** SKU编码 */
    private String skuCode;
    /** 分类ID */
    private Long categoryId;
    /** 分类名称（完整路径，如：电子产品/通讯设备/智能手机/5G手机） */
    private String categoryName;
    /** 库存数量 */
    private Integer stock;
    /** 采购价 */
    private BigDecimal purchasePrice;
    /** 商品描述 */
    private String description;
}
