package com.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_category")
public class Category extends BaseEntity {

    /** 分类编码，1位=一级，3位=二级，4位=三级，5位=四级 */
    private String categoryNo;
    /** 分类名称 */
    private String categoryName;
    /** 父分类编码，空字符串表示顶级 */
    private String parentNo;
    /** 分类层级 1-4 */
    private Integer level;
    /** 排序序号 */
    private Integer sortOrder;
}
