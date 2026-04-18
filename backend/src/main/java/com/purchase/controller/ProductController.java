package com.purchase.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.purchase.common.Result;
import com.purchase.dto.ProductCreateDTO;
import com.purchase.dto.ProductDeleteDTO;
import com.purchase.dto.ProductPageQueryDTO;
import com.purchase.dto.ProductUpdateDTO;
import com.purchase.service.ProductService;
import com.purchase.vo.ProductExportVO;
import com.purchase.vo.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/products")
@Tag(name = "商品管理", description = "商品CRUD及导出接口")
@Slf4j
public class ProductController {

    @Resource
    private ProductService productService;

    @PostMapping(value = "/page")
    @Operation(summary = "分页查询商品", description = "支持按商品名称、SKU编码模糊过滤，按分类筛选")
    public Result<IPage<ProductVO>> page(@RequestBody ProductPageQueryDTO queryDTO) {
        return Result.success(productService.pageProducts(queryDTO));
    }

    @PostMapping(value = "/add")
    @Operation(summary = "新增商品", description = "新增商品，SKU编码唯一，分类必须为末级")
    public Result<Void> add(@Valid @RequestBody ProductCreateDTO createDTO) {
        productService.addProduct(createDTO);
        return Result.success(null);
    }

    @PostMapping(value = "/update")
    @Operation(summary = "修改商品", description = "修改商品信息，SKU编码唯一")
    public Result<Void> update(@Valid @RequestBody ProductUpdateDTO updateDTO) {
        productService.updateProduct(updateDTO);
        return Result.success(null);
    }

    @PostMapping(value = "/delete")
    @Operation(summary = "删除商品", description = "逻辑删除商品")
    public Result<Void> delete(@Valid @RequestBody ProductDeleteDTO deleteDTO) {
        productService.deleteProduct(deleteDTO.getId());
        return Result.success(null);
    }

    @PostMapping(value = "/export")
    @Operation(summary = "导出商品Excel", description = "按筛选条件导出全部匹配记录")
    public void export(@RequestBody ProductPageQueryDTO queryDTO, HttpServletResponse response) {
        try {
            List<ProductExportVO> data = productService.listExportData(queryDTO);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String fileName = URLEncoder.encode("商品列表_" + timestamp + ".xlsx", StandardCharsets.UTF_8);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

            EasyExcel.write(response.getOutputStream(), ProductExportVO.class)
                    .sheet("商品列表")
                    .doWrite(data);
        } catch (Exception e) {
            log.error("ProductController.export_error,导出失败", e);
            throw new RuntimeException("导出失败", e);
        }
    }
}
