package com.purchase.controller;

import com.purchase.common.Result;
import com.purchase.service.CategoryService;
import com.purchase.vo.CategoryTreeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/categories")
@Tag(name = "分类管理", description = "商品分类接口")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @PostMapping(value = "/tree")
    @Operation(summary = "获取分类树", description = "查询完整4级类目树")
    public Result<List<CategoryTreeVO>> tree() {
        return Result.success(categoryService.categoryTree());
    }
}
