package com.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_category")
public class Category extends BaseEntity {

    /** 分类名称 */
    private String categoryName;
    /** 父分类ID，0表示顶级 */
    private Long parentId;
    /** 分类层级 1-4 */
    private Integer level;
    /** 排序序号 */
    private Integer sortOrder;
}
