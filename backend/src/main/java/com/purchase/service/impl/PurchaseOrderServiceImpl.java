package com.purchase.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.purchase.common.PurchaseConstants;
import com.purchase.config.DataModeProperties;
import com.purchase.entity.PurchaseOrder;
import com.purchase.mapper.PurchaseOrderMapper;
import com.purchase.service.PurchaseOrderService;
import com.purchase.vo.PurchaseOrderVO;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder> implements PurchaseOrderService {

    @Resource
    private DataModeProperties dataModeProperties;

    @Override
    public List<PurchaseOrderVO> listPurchaseOrders() {
        if (PurchaseConstants.DATA_MODE_DEV.equals(dataModeProperties.getMode())) {
            return listPurchaseOrdersFromMysql();
        }
        return Arrays.asList(
                new PurchaseOrderVO(1L, "PO20260412001", "华北供应商", new BigDecimal("3990.00"), "CREATED"),
                new PurchaseOrderVO(2L, "PO20260412002", "华东供应商", new BigDecimal("2590.00"), "COMPLETED")
        );
    }

    private List<PurchaseOrderVO> listPurchaseOrdersFromMysql() {
        return lambdaQuery()
                .eq(PurchaseOrder::getIsDelete, 0)
                .orderByAsc(PurchaseOrder::getId)
                .list()
                .stream()
                .map(order ->
                        new PurchaseOrderVO(order.getId(), order.getOrderNo(), order.getSupplierName(), order.getTotalAmount(), order.getOrderStatus())
                )
                .collect(Collectors.toList());
    }
}
