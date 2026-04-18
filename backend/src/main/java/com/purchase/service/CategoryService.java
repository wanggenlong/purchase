package com.purchase.service;

import com.purchase.vo.CategoryTreeVO;

import java.util.List;

public interface CategoryService {

    /** 查询完整分类树 */
    List<CategoryTreeVO> categoryTree();

    /** 查询指定分类及其所有子孙分类ID */
    List<Long> findDescendantIds(Long categoryId);
}
