package com.purchase.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PurchaseOrderVO {

    private Long id;
    private String orderNo;
    private String supplierName;
    private BigDecimal totalAmount;
    private String orderStatus;

}
