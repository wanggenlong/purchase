package com.purchase.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.purchase.common.PurchaseConstants;
import com.purchase.config.DataModeProperties;
import com.purchase.entity.Product;
import com.purchase.mapper.ProductMapper;
import com.purchase.service.ProductService;
import com.purchase.vo.ProductVO;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Resource
    private DataModeProperties dataModeProperties;

    @Override
    public List<ProductVO> listProducts() {
        if (PurchaseConstants.DATA_MODE_DEV.equals(dataModeProperties.getMode())) {
            return listProductsFromMysql();
        }
        return Arrays.asList(
                new ProductVO(1L, "演示商品A", "SKU-A001", 120, new BigDecimal("39.90")),
                new ProductVO(2L, "演示商品B", "SKU-B002", 85, new BigDecimal("59.90"))
        );
    }

    private List<ProductVO> listProductsFromMysql() {
        return lambdaQuery()
                .eq(Product::getIsDelete, 0)
                .orderByAsc(Product::getId)
                .list()
                .stream()
                .map(product ->
                        new ProductVO(product.getId(), product.getProductName(), product.getSkuCode(), product.getStock(), product.getPurchasePrice())
                )
                .collect(Collectors.toList());
    }
}
