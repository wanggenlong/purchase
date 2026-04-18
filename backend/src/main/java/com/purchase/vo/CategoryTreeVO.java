package com.purchase.vo;

import lombok.Data;

import java.util.List;

@Data
public class CategoryTreeVO {

    /** 分类ID */
    private Long id;
    /** 分类名称 */
    private String categoryName;
    /** 父分类ID */
    private Long parentId;
    /** 层级 */
    private Integer level;
    /** 排序 */
    private Integer sortOrder;
    /** 子分类列表 */
    private List<CategoryTreeVO> children;
}
