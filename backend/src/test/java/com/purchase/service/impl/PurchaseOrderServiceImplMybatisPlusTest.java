package com.purchase.service.impl;

import com.purchase.config.DataModeProperties;
import com.purchase.entity.PurchaseOrder;
import com.purchase.mapper.PurchaseOrderMapper;
import com.purchase.vo.PurchaseOrderVO;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PurchaseOrderServiceImplMybatisPlusTest {

    @Test
    void shouldUseMybatisPlusMapperWhenDevMode() throws Exception {
        DataModeProperties properties = new DataModeProperties();
        properties.setMode("dev");
        PurchaseOrderMapper purchaseOrderMapper = mock(PurchaseOrderMapper.class);
        PurchaseOrder order = new PurchaseOrder();
        order.setId(200L);
        order.setOrderNo("MAPPER-PO-001");
        order.setSupplierName("Mapper供应商");
        order.setTotalAmount(new BigDecimal("188.80"));
        order.setOrderStatus("CREATED");
        when(purchaseOrderMapper.selectList(any())).thenReturn(Collections.singletonList(order));

        PurchaseOrderServiceImpl service = new PurchaseOrderServiceImpl();
        setField(service, "dataModeProperties", properties);
        setField(service, "purchaseOrderMapper", purchaseOrderMapper);
        List<PurchaseOrderVO> result = service.listPurchaseOrders();

        assertEquals(1, result.size());
        assertEquals("MAPPER-PO-001", result.get(0).getOrderNo());
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
