package com.purchase.service;

import com.purchase.entity.Category;
import com.purchase.vo.CategoryTreeVO;

import java.util.List;

public interface CategoryService {

    /** 查询完整分类树 */
    List<CategoryTreeVO> categoryTree();

    /** 查询指定分类及其所有子孙分类ID */
    List<Long> findDescendantIds(Long categoryId);

    /** 根据ID查询分类 */
    Category getCategoryById(Long id);

    /** 根据ID查询分类（仅未删除） */
    Category getActiveCategoryById(Long id);
}
