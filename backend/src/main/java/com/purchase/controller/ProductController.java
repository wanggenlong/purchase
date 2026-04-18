package com.purchase.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.purchase.common.Result;
import com.purchase.dto.ProductPageQueryDTO;
import com.purchase.service.ProductService;
import com.purchase.vo.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/products")
@Tag(name = "商品管理", description = "商品列表查询接口")
public class ProductController {

    @Resource
    private ProductService productService;

    @PostMapping(value = "/list")
    @Operation(summary = "获取商品列表", description = "分页查询商品")
    public Result<IPage<ProductVO>> list(@RequestBody ProductPageQueryDTO queryDTO) {
        return Result.success(productService.pageProducts(queryDTO));
    }
}
