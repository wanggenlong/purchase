# 商品模块 CRUD + 分页 + Excel导出 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将商品列表改造为完整的 CRUD 功能（分页查询、新增、删除、修改、Excel导出），补齐 Category 4级类目树。

**Architecture:** MyBatis-Plus 全链路（Page + LambdaQueryWrapper + IService），EasyExcel 导出，前端 ElCascader 级联选择器 + ElDialog 弹窗表单。

**Tech Stack:** Spring Boot 3.5.12, MyBatis-Plus 3.5.15, EasyExcel 3.3.4, Vue 2.6.14, Element UI 2.15.13

---

## File Structure

### 新增文件（后端）

| 文件路径 | 职责 |
|----------|------|
| `backend/src/main/java/com/purchase/entity/Category.java` | 分类实体，含 parentId/level/sortOrder |
| `backend/src/main/java/com/purchase/mapper/CategoryMapper.java` | 分类 Mapper |
| `backend/src/main/java/com/purchase/service/CategoryService.java` | 分类 Service 接口 |
| `backend/src/main/java/com/purchase/service/impl/CategoryServiceImpl.java` | 分类 Service 实现 |
| `backend/src/main/java/com/purchase/controller/CategoryController.java` | 分类 Controller（树查询） |
| `backend/src/main/java/com/purchase/dto/ProductPageQueryDTO.java` | 商品分页查询参数 |
| `backend/src/main/java/com/purchase/dto/ProductCreateDTO.java` | 商品新增参数 |
| `backend/src/main/java/com/purchase/dto/ProductUpdateDTO.java` | 商品修改参数 |
| `backend/src/main/java/com/purchase/dto/ProductDeleteDTO.java` | 商品删除参数 |
| `backend/src/main/java/com/purchase/vo/CategoryTreeVO.java` | 分类树节点 VO |
| `backend/src/main/java/com/purchase/vo/ProductExportVO.java` | Excel 导出 VO |

### 修改文件（后端）

| 文件路径 | 变更内容 |
|----------|----------|
| `backend/src/main/java/com/purchase/entity/Product.java` | 增加 categoryId、description |
| `backend/src/main/java/com/purchase/vo/ProductVO.java` | 增加 categoryName、description，移除 @AllArgsConstructor |
| `backend/src/main/java/com/purchase/service/ProductService.java` | 增加 page/add/update/delete/export 方法 |
| `backend/src/main/java/com/purchase/service/impl/ProductServiceImpl.java` | 移除 mock，实现新方法 |
| `backend/src/main/java/com/purchase/controller/ProductController.java` | 增加新接口 |

### 修改文件（前端）

| 文件路径 | 变更内容 |
|----------|----------|
| `frontend/src/api/purchase.js` | 增加 CRUD + 分类树 + 导出接口 |
| `frontend/src/views/ProductList.vue` | 完整改造为 CRUD 页面 |

### 修改文件（文档/数据库）

| 文件路径 | 变更内容 |
|----------|----------|
| `docs/base/db.md` | 更新 DDL + 类目初始数据 |
| `docs/base/changelog.md` | 新增版本记录 |

---

## Task 1: 数据库 DDL 更新

**Files:**
- Modify: `docs/base/db.md`

- [ ] **Step 1: 更新 t_category 表 DDL，增加 parent_id/level/sort_order 字段和初始类目数据**

将 t_category 建表语句替换为：

```sql
DROP TABLE IF EXISTS t_category;
CREATE TABLE IF NOT EXISTS t_category (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    category_name VARCHAR(64) NOT NULL COMMENT '分类名称',
    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父分类ID，0表示顶级',
    level TINYINT NOT NULL DEFAULT 1 COMMENT '分类层级 1-4',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序序号',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记 0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_parent_id (parent_id),
    KEY idx_category_name (category_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';
```

在 t_category 建表后追加类目初始数据：

```sql
-- 一级类目
INSERT INTO t_category (id, category_name, parent_id, level, sort_order) VALUES
(1, '电子产品', 0, 1, 1),
(2, '办公用品', 0, 1, 2),
(3, '家居生活', 0, 1, 3),
(4, '食品饮料', 0, 1, 4);

-- 二级类目
INSERT INTO t_category (id, category_name, parent_id, level, sort_order) VALUES
(101, '通讯设备', 1, 2, 1),
(102, '计算机设备', 1, 2, 2),
(103, '办公设备', 1, 2, 3),
(104, '文具用品', 2, 2, 1),
(105, '纸制品', 2, 2, 2),
(106, '清洁用品', 3, 2, 1),
(107, '家纺用品', 3, 2, 2),
(108, '休闲零食', 4, 2, 1),
(109, '饮品', 4, 2, 2);

-- 三级类目
INSERT INTO t_category (id, category_name, parent_id, level, sort_order) VALUES
(1011, '智能手机', 101, 3, 1),
(1012, '功能机', 101, 3, 2),
(1021, '笔记本电脑', 102, 3, 1),
(1022, '台式机', 102, 3, 2),
(1031, '打印机', 103, 3, 1),
(1032, '扫描仪', 103, 3, 2),
(1041, '书写工具', 104, 3, 1),
(1042, '文件管理', 104, 3, 2),
(1051, '复印纸', 105, 3, 1),
(1052, '打印纸', 105, 3, 2),
(1061, '清洁剂', 106, 3, 1),
(1062, '清洁工具', 106, 3, 2),
(1071, '毛巾浴巾', 107, 3, 1),
(1072, '床品套件', 107, 3, 2),
(1081, '坚果炒货', 108, 3, 1),
(1082, '饼干糕点', 108, 3, 2),
(1091, '茶饮', 109, 3, 1),
(1092, '咖啡', 109, 3, 2);

-- 四级类目
INSERT INTO t_category (id, category_name, parent_id, level, sort_order) VALUES
(10111, '5G手机', 1011, 4, 1),
(10112, '4G手机', 1011, 4, 2),
(10121, '老人机', 1012, 4, 1),
(10211, '游戏本', 1021, 4, 1),
(10212, '商务本', 1021, 4, 2),
(10213, '轻薄本', 1021, 4, 3),
(10221, '品牌整机', 1022, 4, 1),
(10222, '组装机', 1022, 4, 2),
(10311, '激光打印机', 1031, 4, 1),
(10312, '喷墨打印机', 1031, 4, 2),
(10321, '平板扫描仪', 1032, 4, 1),
(10411, '中性笔', 1041, 4, 1),
(10412, '钢笔', 1041, 4, 2),
(10421, '文件夹', 1042, 4, 1),
(10422, '档案盒', 1042, 4, 2),
(10511, 'A4复印纸', 1051, 4, 1),
(10512, 'A3复印纸', 1051, 4, 2),
(10521, 'A4打印纸', 1052, 4, 1),
(10611, '多功能清洁剂', 1061, 4, 1),
(10612, '玻璃清洁剂', 1061, 4, 2),
(10621, '拖把', 1062, 4, 1),
(10622, '扫帚', 1062, 4, 2),
(10711, '面巾', 1071, 4, 1),
(10712, '浴巾', 1071, 4, 2),
(10721, '四件套', 1072, 4, 1),
(10722, '被芯', 1072, 4, 2),
(10811, '混合坚果', 1081, 4, 1),
(10812, '夏威夷果', 1081, 4, 2),
(10821, '苏打饼干', 1082, 4, 1),
(10822, '曲奇饼干', 1082, 4, 2),
(10911, '绿茶', 1091, 4, 1),
(10912, '红茶', 1091, 4, 2),
(10921, '速溶咖啡', 1092, 4, 1),
(10922, '咖啡豆', 1092, 4, 2);
```

- [ ] **Step 2: 更新 t_product 表 DDL，增加 description 字段**

在 t_product 建表语句中 `purchase_price` 行后增加：

```sql
description VARCHAR(512) DEFAULT NULL COMMENT '商品描述',
```

- [ ] **Step 3: 执行 DDL 到 MySQL**

在 MySQL 中执行更新后的 DDL（先 DROP 旧表再重建）。

---

## Task 2: 后端 Category 模块

**Files:**
- Create: `backend/src/main/java/com/purchase/entity/Category.java`
- Create: `backend/src/main/java/com/purchase/mapper/CategoryMapper.java`
- Create: `backend/src/main/java/com/purchase/service/CategoryService.java`
- Create: `backend/src/main/java/com/purchase/service/impl/CategoryServiceImpl.java`
- Create: `backend/src/main/java/com/purchase/controller/CategoryController.java`
- Create: `backend/src/main/java/com/purchase/vo/CategoryTreeVO.java`

- [ ] **Step 1: 创建 Category 实体**

```java
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
```

- [ ] **Step 2: 创建 CategoryTreeVO**

```java
package com.purchase.vo;

import lombok.Data;

import java.util.List;

@Data
public class CategoryTreeVO {

    /** 分类ID */
    private Long id;
    /** 分类名称 */
    private String categoryName;
    /** 父分类ID */
    private Long parentId;
    /** 层级 */
    private Integer level;
    /** 排序 */
    private Integer sortOrder;
    /** 子分类列表 */
    private List<CategoryTreeVO> children;
}
```

- [ ] **Step 3: 创建 CategoryMapper**

```java
package com.purchase.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.purchase.entity.Category;

public interface CategoryMapper extends BaseMapper<Category> {
}
```

- [ ] **Step 4: 创建 CategoryService 接口**

```java
package com.purchase.service;

import com.purchase.vo.CategoryTreeVO;

import java.util.List;

public interface CategoryService {

    /** 查询完整分类树 */
    List<CategoryTreeVO> categoryTree();

    /** 查询指定分类及其所有子孙分类ID */
    List<Long> findDescendantIds(Long categoryId);
}
```

- [ ] **Step 5: 创建 CategoryServiceImpl**

```java
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
```

- [ ] **Step 6: 创建 CategoryController**

```java
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
```

---

## Task 3: 后端 Product DTO/VO 改造

**Files:**
- Modify: `backend/src/main/java/com/purchase/entity/Product.java`
- Modify: `backend/src/main/java/com/purchase/vo/ProductVO.java`
- Create: `backend/src/main/java/com/purchase/dto/ProductPageQueryDTO.java`
- Create: `backend/src/main/java/com/purchase/dto/ProductCreateDTO.java`
- Create: `backend/src/main/java/com/purchase/dto/ProductUpdateDTO.java`
- Create: `backend/src/main/java/com/purchase/dto/ProductDeleteDTO.java`
- Create: `backend/src/main/java/com/purchase/vo/ProductExportVO.java`

- [ ] **Step 1: 修改 Product 实体，增加 categoryId 和 description**

```java
package com.purchase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_product")
public class Product extends BaseEntity {

    /** 商品名称 */
    private String productName;
    /** SKU编码 */
    private String skuCode;
    /** 分类ID */
    private Long categoryId;
    /** 库存数量 */
    private Integer stock;
    /** 采购价 */
    private BigDecimal purchasePrice;
    /** 商品描述 */
    private String description;
}
```

- [ ] **Step 2: 改造 ProductVO，增加 categoryName 和 description**

```java
package com.purchase.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVO {

    /** 商品ID */
    private Long id;
    /** 商品名称 */
    private String productName;
    /** SKU编码 */
    private String skuCode;
    /** 分类ID */
    private Long categoryId;
    /** 分类名称（完整路径，如：电子产品/通讯设备/智能手机/5G手机） */
    private String categoryName;
    /** 库存数量 */
    private Integer stock;
    /** 采购价 */
    private BigDecimal purchasePrice;
    /** 商品描述 */
    private String description;
}
```

- [ ] **Step 3: 创建 ProductPageQueryDTO**

```java
package com.purchase.dto;

import lombok.Data;

@Data
public class ProductPageQueryDTO {

    /** 当前页 */
    private Integer pageNum = 1;
    /** 每页条数 */
    private Integer pageSize = 10;
    /** 商品名称（模糊） */
    private String productName;
    /** SKU编码（模糊） */
    private String skuCode;
    /** 分类ID（精确，含子分类） */
    private Long categoryId;
}
```

- [ ] **Step 4: 创建 ProductCreateDTO**

```java
package com.purchase.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCreateDTO {

    /** 商品名称 */
    @NotBlank(message = "商品名称不能为空")
    private String productName;
    /** SKU编码 */
    @NotBlank(message = "SKU编码不能为空")
    private String skuCode;
    /** 分类ID */
    @NotNull(message = "分类不能为空")
    private Long categoryId;
    /** 采购价 */
    @NotNull(message = "采购价不能为空")
    @DecimalMin(value = "0.01", message = "采购价必须大于0")
    private BigDecimal purchasePrice;
    /** 商品描述 */
    private String description;
}
```

- [ ] **Step 5: 创建 ProductUpdateDTO**

```java
package com.purchase.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateDTO {

    /** 商品ID */
    @NotNull(message = "商品ID不能为空")
    private Long id;
    /** 商品名称 */
    @NotBlank(message = "商品名称不能为空")
    private String productName;
    /** SKU编码 */
    @NotBlank(message = "SKU编码不能为空")
    private String skuCode;
    /** 分类ID */
    @NotNull(message = "分类不能为空")
    private Long categoryId;
    /** 采购价 */
    @NotNull(message = "采购价不能为空")
    @DecimalMin(value = "0.01", message = "采购价必须大于0")
    private BigDecimal purchasePrice;
    /** 商品描述 */
    private String description;
}
```

- [ ] **Step 6: 创建 ProductDeleteDTO**

```java
package com.purchase.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductDeleteDTO {

    /** 商品ID */
    @NotNull(message = "商品ID不能为空")
    private Long id;
}
```

- [ ] **Step 7: 创建 ProductExportVO（EasyExcel 注解）**

```java
package com.purchase.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductExportVO {

    @ExcelProperty("ID")
    private Long id;
    @ExcelProperty("商品名称")
    private String productName;
    @ExcelProperty("SKU编码")
    private String skuCode;
    @ExcelProperty("分类")
    private String categoryName;
    @ExcelProperty("库存")
    private Integer stock;
    @ExcelProperty("采购价")
    private BigDecimal purchasePrice;
    @ExcelProperty("描述")
    private String description;
}
```

---

## Task 4: 后端 Product Service 改造

**Files:**
- Modify: `backend/src/main/java/com/purchase/service/ProductService.java`
- Modify: `backend/src/main/java/com/purchase/service/impl/ProductServiceImpl.java`

- [ ] **Step 1: 改造 ProductService 接口**

```java
package com.purchase.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.purchase.dto.ProductCreateDTO;
import com.purchase.dto.ProductPageQueryDTO;
import com.purchase.dto.ProductUpdateDTO;
import com.purchase.vo.ProductExportVO;
import com.purchase.vo.ProductVO;

import java.util.List;

public interface ProductService {

    /** 分页查询商品 */
    IPage<ProductVO> pageProducts(ProductPageQueryDTO queryDTO);

    /** 新增商品 */
    void addProduct(ProductCreateDTO createDTO);

    /** 修改商品 */
    void updateProduct(ProductUpdateDTO updateDTO);

    /** 删除商品（逻辑删除） */
    void deleteProduct(Long id);

    /** 查询导出数据 */
    List<ProductExportVO> listExportData(ProductPageQueryDTO queryDTO);
}
```

- [ ] **Step 2: 改造 ProductServiceImpl，移除 mock 逻辑，实现全部方法**

```java
package com.purchase.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.purchase.common.exception.BusinessException;
import com.purchase.dto.ProductCreateDTO;
import com.purchase.dto.ProductPageQueryDTO;
import com.purchase.dto.ProductUpdateDTO;
import com.purchase.entity.Category;
import com.purchase.entity.Product;
import com.purchase.mapper.ProductMapper;
import com.purchase.service.CategoryService;
import com.purchase.service.ProductService;
import com.purchase.vo.ProductExportVO;
import com.purchase.vo.ProductVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Resource
    private CategoryService categoryService;

    @Override
    public IPage<ProductVO> pageProducts(ProductPageQueryDTO queryDTO) {
        Page<Product> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        var query = lambdaQuery()
                .eq(Product::getIsDelete, 0)
                .like(StringUtils.isNotBlank(queryDTO.getProductName()), Product::getProductName, queryDTO.getProductName())
                .like(StringUtils.isNotBlank(queryDTO.getSkuCode()), Product::getSkuCode, queryDTO.getSkuCode());
        if (queryDTO.getCategoryId() != null) {
            query.in(Product::getCategoryId, categoryService.findDescendantIds(queryDTO.getCategoryId()));
        }
        IPage<Product> productPage = query.orderByDesc(Product::getUpdateTime).page(page);
        return productPage.convert(this::toProductVO);
    }

    @Override
    public void addProduct(ProductCreateDTO createDTO) {
        validateSkuCodeUnique(createDTO.getSkuCode(), null);
        validateCategoryLeaf(createDTO.getCategoryId());

        Product product = new Product();
        product.setProductName(createDTO.getProductName());
        product.setSkuCode(createDTO.getSkuCode());
        product.setCategoryId(createDTO.getCategoryId());
        product.setStock(0);
        product.setPurchasePrice(createDTO.getPurchasePrice());
        product.setDescription(createDTO.getDescription());
        save(product);
    }

    @Override
    public void updateProduct(ProductUpdateDTO updateDTO) {
        Product existing = lambdaQuery()
                .eq(Product::getId, updateDTO.getId())
                .eq(Product::getIsDelete, 0)
                .one();
        if (existing == null) {
            throw new BusinessException("商品不存在");
        }
        validateSkuCodeUnique(updateDTO.getSkuCode(), updateDTO.getId());
        validateCategoryLeaf(updateDTO.getCategoryId());

        existing.setProductName(updateDTO.getProductName());
        existing.setSkuCode(updateDTO.getSkuCode());
        existing.setCategoryId(updateDTO.getCategoryId());
        existing.setPurchasePrice(updateDTO.getPurchasePrice());
        existing.setDescription(updateDTO.getDescription());
        updateById(existing);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = getById(id);
        if (product == null || product.getIsDelete() == 1) {
            throw new BusinessException("商品不存在");
        }
        removeById(id);
    }

    @Override
    public List<ProductExportVO> listExportData(ProductPageQueryDTO queryDTO) {
        var query = lambdaQuery()
                .eq(Product::getIsDelete, 0)
                .like(StringUtils.isNotBlank(queryDTO.getProductName()), Product::getProductName, queryDTO.getProductName())
                .like(StringUtils.isNotBlank(queryDTO.getSkuCode()), Product::getSkuCode, queryDTO.getSkuCode());
        if (queryDTO.getCategoryId() != null) {
            query.in(Product::getCategoryId, categoryService.findDescendantIds(queryDTO.getCategoryId()));
        }
        List<Product> products = query.orderByDesc(Product::getUpdateTime).list();

        return products.stream()
                .map(this::toExportVO)
                .collect(Collectors.toList());
    }

    /** 校验SKU编码唯一性 */
    private void validateSkuCodeUnique(String skuCode, Long excludeId) {
        Product existing = lambdaQuery()
                .eq(Product::getSkuCode, skuCode)
                .eq(Product::getIsDelete, 0)
                .ne(excludeId != null, Product::getId, excludeId)
                .one();
        if (existing != null) {
            throw new BusinessException("SKU编码已存在");
        }
    }

    /** 校验分类必须为末级（第4级） */
    private void validateCategoryLeaf(Long categoryId) {
        Category category = categoryService.lambdaQuery()
                .eq(Category::getId, categoryId)
                .eq(Category::getIsDelete, 0)
                .one();
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        if (category.getLevel() != 4) {
            throw new BusinessException("商品分类必须选择最末级（第四级）");
        }
    }

    /** 构建分类完整路径名称 */
    private String buildCategoryPath(Long categoryId) {
        if (categoryId == null) {
            return "";
        }
        List<String> pathNames = new ArrayList<>();
        Long currentId = categoryId;
        for (int i = 0; i < 4 && currentId != null && currentId > 0; i++) {
            Category category = categoryService.getById(currentId);
            if (category == null) {
                break;
            }
            pathNames.addFirst(category.getCategoryName());
            currentId = category.getParentId();
        }
        return String.join("/", pathNames);
    }

    private ProductVO toProductVO(Product product) {
        ProductVO vo = new ProductVO();
        vo.setId(product.getId());
        vo.setProductName(product.getProductName());
        vo.setSkuCode(product.getSkuCode());
        vo.setCategoryId(product.getCategoryId());
        vo.setCategoryName(buildCategoryPath(product.getCategoryId()));
        vo.setStock(product.getStock());
        vo.setPurchasePrice(product.getPurchasePrice());
        vo.setDescription(product.getDescription());
        return vo;
    }

    private ProductExportVO toExportVO(Product product) {
        ProductExportVO vo = new ProductExportVO();
        vo.setId(product.getId());
        vo.setProductName(product.getProductName());
        vo.setSkuCode(product.getSkuCode());
        vo.setCategoryName(buildCategoryPath(product.getCategoryId()));
        vo.setStock(product.getStock());
        vo.setPurchasePrice(product.getPurchasePrice());
        vo.setDescription(product.getDescription());
        return vo;
    }
}
```

---

## Task 5: 后端 Product Controller 改造

**Files:**
- Modify: `backend/src/main/java/com/purchase/controller/ProductController.java`

- [ ] **Step 1: 改造 ProductController，增加全部 CRUD + 导出接口**

```java
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
```

---

## Task 6: 后端 Security 配置更新

**Files:**
- Modify: `backend/src/main/resources/application-security.yml`

- [ ] **Step 1: 在 permit-all 中添加 categories 路径**

在 `application-security.yml` 的 `permit-all` 列表中添加：

```yaml
      - /categories/**
```

---

## Task 7: 前端 API 层改造

**Files:**
- Modify: `frontend/src/api/purchase.js`

- [ ] **Step 1: 增加商品 CRUD + 分类树 + 导出接口**

```javascript
import request from '../utils/request';

export function getHealthStatus() {
  return request({
    url: '/health/check',
    method: 'post'
  });
}

export function getProductPage(data) {
  return request({
    url: '/products/page',
    method: 'post',
    data
  });
}

export function addProduct(data) {
  return request({
    url: '/products/add',
    method: 'post',
    data
  });
}

export function updateProduct(data) {
  return request({
    url: '/products/update',
    method: 'post',
    data
  });
}

export function deleteProduct(data) {
  return request({
    url: '/products/delete',
    method: 'post',
    data
  });
}

export function exportProduct(data) {
  return request({
    url: '/products/export',
    method: 'post',
    data,
    responseType: 'blob'
  });
}

export function getCategoryTree() {
  return request({
    url: '/categories/tree',
    method: 'post'
  });
}

export function getPurchaseOrders() {
  return request({
    url: '/purchase-orders/list',
    method: 'post'
  });
}

export function generatePasswordHash(password) {
  return request({
    url: '/debug/hash',
    method: 'post',
    data: { password }
  });
}

export function verifyPassword(rawPassword, hashedPassword) {
  return request({
    url: '/debug/verify',
    method: 'post',
    data: { rawPassword, hashedPassword }
  });
}
```

---

## Task 8: 前端 ProductList.vue 完整改造

**Files:**
- Modify: `frontend/src/views/ProductList.vue`

- [ ] **Step 1: 完整重写 ProductList.vue**

```vue
<template>
  <div class="product-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="queryForm.productName"
        placeholder="商品名称"
        clearable
        class="search-input"
        @keyup.enter.native="handleSearch"
      />
      <el-input
        v-model="queryForm.skuCode"
        placeholder="SKU编码"
        clearable
        class="search-input"
        @keyup.enter.native="handleSearch"
      />
      <el-cascader
        v-model="queryForm.categoryIds"
        :options="categoryTree"
        :props="cascaderProps"
        placeholder="商品分类"
        clearable
        class="search-cascader"
      />
      <el-button type="primary" @click="handleSearch">查询</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增商品</el-button>
      <el-button icon="el-icon-download" @click="handleExport">导出Excel</el-button>
    </div>

    <!-- 数据表格 -->
    <el-table :data="productList" style="width: 100%;" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="productName" label="商品名称" min-width="160" show-overflow-tooltip />
      <el-table-column prop="skuCode" label="SKU编码" width="140" />
      <el-table-column prop="categoryName" label="分类" min-width="200" show-overflow-tooltip>
        <template slot-scope="scope">
          <el-tag size="small" type="info" v-if="scope.row.categoryName">{{ scope.row.categoryName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="80" align="right" />
      <el-table-column prop="purchasePrice" label="采购价" width="100" align="right">
        <template slot-scope="scope">
          ¥{{ scope.row.purchasePrice }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" align="center">
        <template slot-scope="scope">
          <el-button type="text" class="action-link" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="text" class="action-link delete-link" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-bar">
      <span class="total-text">共 {{ total }} 条</span>
      <el-pagination
        background
        layout="prev, pager, next"
        :current-page="queryForm.pageNum"
        :page-size="queryForm.pageSize"
        :total="total"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px" :close-on-click-modal="false">
      <el-form ref="productForm" :model="productForm" :rules="productRules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="商品名称" prop="productName">
              <el-input v-model="productForm.productName" placeholder="请输入商品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="SKU编码" prop="skuCode">
              <el-input v-model="productForm.skuCode" placeholder="请输入SKU编码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="商品分类" prop="categoryIds">
          <el-cascader
            v-model="productForm.categoryIds"
            :options="categoryTree"
            :props="cascaderProps"
            placeholder="请选择分类（需选到第四级）"
            style="width: 100%;"
          />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="采购价" prop="purchasePrice">
              <el-input-number
                v-model="productForm.purchasePrice"
                :min="0.01"
                :precision="2"
                :step="1"
                controls-position="right"
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="商品描述" prop="description">
          <el-input v-model="productForm.description" type="textarea" :rows="3" placeholder="请输入商品描述" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getProductPage, addProduct, updateProduct, deleteProduct, exportProduct, getCategoryTree } from '../api/purchase';

export default {
  name: 'ProductListView',
  data() {
    return {
      loading: false,
      submitLoading: false,
      productList: [],
      total: 0,
      categoryTree: [],
      cascaderProps: {
        value: 'id',
        label: 'categoryName',
        children: 'children',
        checkStrictly: false,
        emitPath: false
      },
      queryForm: {
        pageNum: 1,
        pageSize: 10,
        productName: '',
        skuCode: '',
        categoryIds: []
      },
      dialogVisible: false,
      isEdit: false,
      productForm: {
        id: null,
        productName: '',
        skuCode: '',
        categoryIds: [],
        categoryId: null,
        purchasePrice: null,
        description: ''
      },
      productRules: {
        productName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
        skuCode: [{ required: true, message: '请输入SKU编码', trigger: 'blur' }],
        categoryIds: [{ required: true, message: '请选择商品分类', trigger: 'change', type: 'array', min: 4 }],
        purchasePrice: [{ required: true, message: '请输入采购价', trigger: 'blur' }]
      }
    };
  },
  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑商品' : '新增商品';
    }
  },
  created() {
    this.loadCategoryTree();
    this.loadProducts();
  },
  methods: {
    async loadCategoryTree() {
      const result = await getCategoryTree();
      this.categoryTree = result.data || [];
    },
    async loadProducts() {
      this.loading = true;
      try {
        const params = {
          pageNum: this.queryForm.pageNum,
          pageSize: this.queryForm.pageSize,
          productName: this.queryForm.productName || undefined,
          skuCode: this.queryForm.skuCode || undefined,
          categoryId: this.queryForm.categoryIds.length > 0 ? this.queryForm.categoryIds[this.queryForm.categoryIds.length - 1] : undefined
        };
        const result = await getProductPage(params);
        this.productList = result.data.records;
        this.total = result.data.total;
      } finally {
        this.loading = false;
      }
    },
    handleSearch() {
      this.queryForm.pageNum = 1;
      this.loadProducts();
    },
    handleReset() {
      this.queryForm = {
        pageNum: 1,
        pageSize: 10,
        productName: '',
        skuCode: '',
        categoryIds: []
      };
      this.loadProducts();
    },
    handlePageChange(page) {
      this.queryForm.pageNum = page;
      this.loadProducts();
    },
    handleAdd() {
      this.isEdit = false;
      this.productForm = {
        id: null,
        productName: '',
        skuCode: '',
        categoryIds: [],
        categoryId: null,
        purchasePrice: null,
        description: ''
      };
      this.dialogVisible = true;
      this.$nextTick(() => {
        this.$refs.productForm && this.$refs.productForm.clearValidate();
      });
    },
    handleEdit(row) {
      this.isEdit = true;
      this.productForm = {
        id: row.id,
        productName: row.productName,
        skuCode: row.skuCode,
        categoryIds: this.buildCategoryPath(row.categoryId),
        categoryId: row.categoryId,
        purchasePrice: row.purchasePrice,
        description: row.description || ''
      };
      this.dialogVisible = true;
      this.$nextTick(() => {
        this.$refs.productForm && this.$refs.productForm.clearValidate();
      });
    },
    buildCategoryPath(categoryId) {
      const path = [];
      const findPath = (nodes, targetId, currentPath) => {
        for (const node of nodes) {
          const newPath = [...currentPath, node.id];
          if (node.id === targetId) {
            path.push(...newPath);
            return true;
          }
          if (node.children && node.children.length > 0) {
            if (findPath(node.children, targetId, newPath)) return true;
          }
        }
        return false;
      };
      findPath(this.categoryTree, categoryId, []);
      return path;
    },
    handleSubmit() {
      this.$refs.productForm.validate(valid => {
        if (!valid) return;
        this.submitLoading = true;

        const categoryId = this.productForm.categoryIds[this.productForm.categoryIds.length - 1];
        const data = {
          productName: this.productForm.productName,
          skuCode: this.productForm.skuCode,
          categoryId: categoryId,
          purchasePrice: this.productForm.purchasePrice,
          description: this.productForm.description
        };

        const request = this.isEdit
          ? updateProduct({ ...data, id: this.productForm.id })
          : addProduct(data);

        request.then(() => {
          this.$message.success(this.isEdit ? '修改成功' : '新增成功');
          this.dialogVisible = false;
          this.loadProducts();
        }).catch(err => {
          this.$message.error(err.response?.data?.msg || '操作失败');
        }).finally(() => {
          this.submitLoading = false;
        });
      });
    },
    handleDelete(row) {
      this.$confirm('确定删除商品"' + row.productName + '"？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteProduct({ id: row.id }).then(() => {
          this.$message.success('删除成功');
          this.loadProducts();
        }).catch(err => {
          this.$message.error(err.response?.data?.msg || '删除失败');
        });
      }).catch(() => {});
    },
    async handleExport() {
      try {
        const params = {
          productName: this.queryForm.productName || undefined,
          skuCode: this.queryForm.skuCode || undefined,
          categoryId: this.queryForm.categoryIds.length > 0 ? this.queryForm.categoryIds[this.queryForm.categoryIds.length - 1] : undefined
        };
        const result = await exportProduct(params);
        const blob = new Blob([result], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = '商品列表.xlsx';
        link.click();
        window.URL.revokeObjectURL(url);
        this.$message.success('导出成功');
      } catch (err) {
        this.$message.error('导出失败');
      }
    }
  }
};
</script>

<style scoped>
.product-container {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.08);
}

.search-bar {
  padding: 16px 24px;
  border-bottom: 1px solid #e0e0e0;
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.search-input {
  width: 200px;
}

.search-cascader {
  width: 260px;
}

.action-bar {
  padding: 12px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-bar {
  padding: 12px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #e0e0e0;
}

.total-text {
  color: #5f6368;
  font-size: 13px;
}

.action-link {
  color: #1a73e8 !important;
  font-size: 13px;
  padding: 0;
}

.delete-link {
  color: #d93025 !important;
}

.action-link:hover,
.delete-link:hover {
  opacity: 0.8;
}
</style>
```

---

## Task 9: 更新 changelog

**Files:**
- Modify: `docs/base/changelog.md`

- [ ] **Step 1: 新增版本记录**

在 changelog.md 顶部新增：

```markdown
## [0.0.2] - 2026-04-18

### 新增
- 商品分页查询，支持按 productName/skuCode 模糊过滤和 categoryId 分类筛选
- 商品新增、修改、删除（逻辑删除）功能
- 商品 Excel 导出功能（EasyExcel）
- 商品分类 4 级类目树接口（Category 模块）
- 前端商品列表完整 CRUD 页面，含搜索、分页、弹窗表单、级联分类选择器

### 优化
- Product 实体补齐 categoryId、description 字段
- ProductVO 增加 categoryName（完整分类路径）、description 字段
- 移除 ProductServiceImpl 中的 mock 分支逻辑，仅保留 dev 模式
```

---

## Task 10: 前后端联调

**Files:** 无新增/修改

- [ ] **Step 1: 启动后端（dev 模式）**

Run: `cd backend && mvn spring-boot:run -Dspring-boot.run.profiles=dev`

验证后端启动成功，日志无报错。

- [ ] **Step 2: 启动前端**

Run: `cd frontend && npm run serve`

验证前端启动成功。

- [ ] **Step 3: 浏览器联调测试**

打开 http://localhost:8080，登录后进入商品列表页，验证：

1. 分页查询正常，搜索/重置生效
2. 分类级联选择器加载4级类目树
3. 新增商品弹窗提交成功
4. 编辑商品弹窗回填数据，修改提交成功
5. 删除商品弹出确认，逻辑删除生效
6. Excel 导出生成文件可打开
7. SKU 编码重复时提示错误

- [ ] **Step 4: 修复联调中发现的问题**

如有接口参数不匹配、前端展示异常等，即时修复。
