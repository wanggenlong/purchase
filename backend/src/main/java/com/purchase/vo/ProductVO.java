package com.purchase.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVO {

    private final Long id;
    private final String productName;
    private final String skuCode;
    private final Integer stock;
    private final BigDecimal purchasePrice;

    public ProductVO(Long id, String productName, String skuCode, Integer stock, BigDecimal purchasePrice) {
        this.id = id;
        this.productName = productName;
        this.skuCode = skuCode;
        this.stock = stock;
        this.purchasePrice = purchasePrice;
    }

}
