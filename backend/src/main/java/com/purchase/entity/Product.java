package com.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_product")
public class Product extends BaseEntity {

    private String productName;
    private String skuCode;
    private Integer stock;
    private BigDecimal purchasePrice;

}
