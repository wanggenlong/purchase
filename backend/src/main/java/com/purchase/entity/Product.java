package com.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_product")
public class Product extends BaseEntity {

    /** 商品名称 */
    private String productName;
    /** SKU编码 */
    private String skuCode;
    /** 分类编码 */
    private String categoryNo;
    /** 库存数量 */
    private Integer stock;
    /** 采购价 */
    private BigDecimal purchasePrice;
    /** 商品描述 */
    private String description;
}
