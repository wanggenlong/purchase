package com.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_purchase_order")
public class PurchaseOrder extends BaseEntity {

    private String orderNo;
    private String supplierName;
    private BigDecimal totalAmount;
    private String orderStatus;
}
