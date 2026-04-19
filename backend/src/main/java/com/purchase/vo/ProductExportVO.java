package com.purchase.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ColumnWidth(15)
public class ProductExportVO {

    @ExcelProperty("ID")
    @ColumnWidth(10)
    private Long id;
    @ExcelProperty("商品名称")
    @ColumnWidth(25)
    private String productName;
    @ExcelProperty("SKU编码")
    @ColumnWidth(18)
    private String skuCode;
    @ExcelProperty("分类")
    @ColumnWidth(35)
    private String categoryName;
    @ExcelProperty("库存")
    @ColumnWidth(10)
    private Integer stock;
    @ExcelProperty("采购价")
    @ColumnWidth(12)
    private BigDecimal purchasePrice;
    @ExcelProperty("描述")
    @ColumnWidth(40)
    private String description;
}
