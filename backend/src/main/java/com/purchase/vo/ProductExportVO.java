package com.purchase.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductExportVO {

    @ExcelProperty("ID")
    private Long id;
    @ExcelProperty("商品名称")
    private String productName;
    @ExcelProperty("SKU编码")
    private String skuCode;
    @ExcelProperty("分类")
    private String categoryName;
    @ExcelProperty("库存")
    private Integer stock;
    @ExcelProperty("采购价")
    private BigDecimal purchasePrice;
    @ExcelProperty("描述")
    private String description;
}
