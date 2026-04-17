package com.purchase.service.impl;

import com.purchase.config.DataModeProperties;
import com.purchase.entity.Product;
import com.purchase.mapper.ProductMapper;
import com.purchase.vo.ProductVO;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductServiceImplMybatisPlusTest {

    @Test
    void shouldUseMybatisPlusMapperWhenDevMode() throws Exception {
        DataModeProperties properties = new DataModeProperties();
        properties.setMode("dev");
        ProductMapper productMapper = mock(ProductMapper.class);
        Product product = new Product();
        product.setId(100L);
        product.setProductName("Mapper商品");
        product.setSkuCode("MAPPER-001");
        product.setStock(12);
        product.setPurchasePrice(new BigDecimal("99.90"));
        when(productMapper.selectList(any())).thenReturn(Collections.singletonList(product));

        ProductServiceImpl service = new ProductServiceImpl();
        setField(service, "dataModeProperties", properties);
        setField(service, "productMapper", productMapper);
        List<ProductVO> result = service.listProducts();

        assertEquals(1, result.size());
        assertEquals("Mapper商品", result.get(0).getProductName());
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
