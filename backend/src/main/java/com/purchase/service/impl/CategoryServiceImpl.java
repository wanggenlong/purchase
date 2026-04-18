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

        Map<Long, List<CategoryTreeVO>> childrenMap = voList.stream()
                .filter(vo -> vo.getParentId() != null && vo.getParentId() > 0)
                .collect(Collectors.groupingBy(CategoryTreeVO::getParentId));

        for (CategoryTreeVO vo : voList) {
            vo.setChildren(childrenMap.get(vo.getId()));
        }

        return voList.stream()
                .filter(vo -> vo.getParentId() == null || vo.getParentId() == 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> findDescendantIds(Long categoryId) {
        if (categoryId == null) {
            return List.of();
        }
        List<Long> ids = new ArrayList<>();
        ids.add(categoryId);
        collectDescendantIds(categoryId, ids);
        return ids;
    }

    private void collectDescendantIds(Long parentId, List<Long> ids) {
        List<Category> children = lambdaQuery()
                .eq(Category::getIsDelete, 0)
                .eq(Category::getParentId, parentId)
                .list();
        for (Category child : children) {
            ids.add(child.getId());
            collectDescendantIds(child.getId(), ids);
        }
    }

    private CategoryTreeVO toTreeVO(Category category) {
        CategoryTreeVO vo = new CategoryTreeVO();
        vo.setId(category.getId());
        vo.setCategoryName(category.getCategoryName());
        vo.setParentId(category.getParentId());
        vo.setLevel(category.getLevel());
        vo.setSortOrder(category.getSortOrder());
        return vo;
    }
}
