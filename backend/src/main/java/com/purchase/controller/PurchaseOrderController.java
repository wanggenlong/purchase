package com.purchase.controller;

import com.purchase.common.Result;
import com.purchase.service.PurchaseOrderService;
import com.purchase.vo.PurchaseOrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/purchase-orders")
@Tag(name = "采购订单", description = "采购订单查询接口")
public class PurchaseOrderController {

    @Resource
    private PurchaseOrderService purchaseOrderService;

    @PostMapping(value = "/list")
    @Operation(summary = "获取采购订单列表", description = "查询所有采购订单")
    public Result<List<PurchaseOrderVO>> list() {
        return Result.success(purchaseOrderService.listPurchaseOrders());
    }
}
