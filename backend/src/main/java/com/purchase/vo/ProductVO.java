package com.purchase.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductVO {

    private Long id;
    private String productName;
    private String skuCode;
    private Integer stock;
    private BigDecimal purchasePrice;

}
