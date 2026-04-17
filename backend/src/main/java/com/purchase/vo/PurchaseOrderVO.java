package com.purchase.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseOrderVO {

    private final Long id;
    private final String orderNo;
    private final String supplierName;
    private final BigDecimal totalAmount;
    private final String orderStatus;

    public PurchaseOrderVO(Long id, String orderNo, String supplierName, BigDecimal totalAmount, String orderStatus) {
        this.id = id;
        this.orderNo = orderNo;
        this.supplierName = supplierName;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
    }

}
