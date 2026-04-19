package com.purchase.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.purchase.entity.Category;
import com.purchase.mapper.CategoryMapper;
import com.purchase.service.CategoryService;
import com.purchase.vo.CategoryTreeVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<CategoryTreeVO> categoryTree() {
        List<Category> allCategories = lambdaQuery()
                .eq(Category::getIsDelete, 0)
                .orderByAsc(Category::getSortOrder)
                .list();

        List<CategoryTreeVO> voList = allCategories.stream()
                .map(this::toTreeVO)
                .collect(Collectors.toList());

        Map<String, List<CategoryTreeVO>> childrenMap = voList.stream()
                .filter(vo -> vo.getParentNo() != null && !vo.getParentNo().isEmpty())
                .collect(Collectors.groupingBy(CategoryTreeVO::getParentNo));

        for (CategoryTreeVO vo : voList) {
            vo.setChildren(childrenMap.get(vo.getCategoryNo()));
        }

        return voList.stream()
                .filter(vo -> vo.getParentNo() == null || vo.getParentNo().isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findDescendantNos(String categoryNo) {
        if (categoryNo == null || categoryNo.isEmpty()) {
            return List.of();
        }
        List<String> nos = new ArrayList<>();
        nos.add(categoryNo);
        collectDescendantNos(categoryNo, nos);
        return nos;
    }

    private void collectDescendantNos(String parentNo, List<String> nos) {
        List<Category> children = lambdaQuery()
                .eq(Category::getIsDelete, 0)
                .eq(Category::getParentNo, parentNo)
                .list();
        for (Category child : children) {
            nos.add(child.getCategoryNo());
            collectDescendantNos(child.getCategoryNo(), nos);
        }
    }

    @Override
    public Category getCategoryByNo(String categoryNo) {
        return lambdaQuery()
                .eq(Category::getCategoryNo, categoryNo)
                .one();
    }

    @Override
    public Category getActiveCategoryByNo(String categoryNo) {
        return lambdaQuery()
                .eq(Category::getCategoryNo, categoryNo)
                .eq(Category::getIsDelete, 0)
                .one();
    }

    private CategoryTreeVO toTreeVO(Category category) {
        CategoryTreeVO vo = new CategoryTreeVO();
        vo.setId(category.getId());
        vo.setCategoryNo(category.getCategoryNo());
        vo.setCategoryName(category.getCategoryName());
        vo.setParentNo(category.getParentNo());
        vo.setLevel(category.getLevel());
        vo.setSortOrder(category.getSortOrder());
        return vo;
    }
}
