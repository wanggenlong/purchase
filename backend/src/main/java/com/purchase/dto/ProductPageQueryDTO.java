package com.purchase.dto;

import lombok.Data;

@Data
public class ProductPageQueryDTO {

    /** 当前页 */
    private Integer pageNum = 1;
    /** 每页条数 */
    private Integer pageSize = 10;
    /** 商品名称（模糊） */
    private String productName;
    /** SKU编码（模糊） */
    private String skuCode;
    /** 分类编码（精确，含子分类） */
    private String categoryNo;
}
