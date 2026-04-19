package com.purchase.service;

import com.purchase.entity.Category;
import com.purchase.vo.CategoryTreeVO;

import java.util.List;

public interface CategoryService {

    /** 查询完整分类树 */
    List<CategoryTreeVO> categoryTree();

    /** 查询指定分类及其所有子孙分类编码 */
    List<String> findDescendantNos(String categoryNo);

    /** 根据分类编码查询分类 */
    Category getCategoryByNo(String categoryNo);

    /** 根据分类编码查询分类（仅未删除） */
    Category getActiveCategoryByNo(String categoryNo);
}
