# 商品模块 CRUD + 分页 + Excel导出 设计文档

## 概述

将商品列表从简单列表查询改造为完整的 CRUD 功能，包含分页查询、模糊过滤、新增、删除、修改、Excel 导出。同时补齐 Category 模块后端代码，实现 4 级类目树结构。

## 需求确认

| 需求项 | 决策 |
|--------|------|
| 分页查询 | MyBatis-Plus Page + PaginationInnerInterceptor |
| 模糊过滤 | productName、skuCode 模糊查询，categoryId 精确匹配 |
| 删除方式 | 逻辑删除（is_delete=1） |
| Excel 导出 | EasyExcel，按当前筛选条件导出全部匹配记录 |
| 新增/编辑交互 | 弹窗 Dialog |
| categoryId | 对接 t_category 表，4 级级联选择器，存最末级 ID；搜索时选择非末级则匹配该级及所有子孙 |
| 库存字段 | 只读，不在新增/编辑表单中展示 |
| 分类来源 | 新建 Category 后端模块（entity/mapper/service/controller） |
| dev 模式 | 仅实现 dev 模式，移除 mock 分支逻辑 |
| 测试 | 不写测试用例 |

## 数据库变更

### t_category 表改造

当前 t_category 表缺少 `parent_id` 和 `level` 字段，无法支持树形结构。需修改为：

```sql
ALTER TABLE t_category ADD COLUMN parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父分类ID，0表示顶级' AFTER category_name;
ALTER TABLE t_category ADD COLUMN level TINYINT NOT NULL DEFAULT 1 COMMENT '分类层级 1-4' AFTER parent_id;
ALTER TABLE t_category ADD COLUMN sort_order INT NOT NULL DEFAULT 0 COMMENT '排序序号' AFTER level;
ALTER TABLE t_category ADD COLUMN KEY idx_parent_id (parent_id);
```

### t_product 表变更

Product 实体类需补齐 `categoryId` 和 `description` 字段：

```sql
ALTER TABLE t_product ADD COLUMN description VARCHAR(512) DEFAULT NULL COMMENT '商品描述' AFTER purchase_price;
```

### 初始化类目数据

插入标准 4 级类目数据（综合电商全品类），覆盖：电子产品、办公用品、家居生活、食品饮料等大类。

## 后端设计

### 技术方案

MyBatis-Plus 全链路：`Page` + `LambdaQueryWrapper` + `IService` 内置方法 + EasyExcel 导出。

### 新增文件清单

| 文件 | 说明 |
|------|------|
| `entity/Category.java` | 分类实体，含 parentId/level/sortOrder |
| `mapper/CategoryMapper.java` | 分类 Mapper |
| `service/CategoryService.java` | 分类 Service 接口 |
| `service/impl/CategoryServiceImpl.java` | 分类 Service 实现 |
| `controller/CategoryController.java` | 分类 Controller（树查询接口） |
| `dto/ProductPageQueryDTO.java` | 商品分页查询参数 |
| `dto/ProductCreateDTO.java` | 商品新增参数 |
| `dto/ProductUpdateDTO.java` | 商品修改参数 |
| `vo/ProductVO.java` | 改造，增加 categoryName/description |
| `vo/CategoryTreeVO.java` | 分类树节点 VO |
| `vo/ProductExportVO.java` | Excel 导出 VO（EasyExcel 注解） |

### 修改文件清单

| 文件 | 变更 |
|------|------|
| `entity/Product.java` | 增加 categoryId、description 字段 |
| `service/ProductService.java` | 增加 page/add/update/delete/export 方法 |
| `service/impl/ProductServiceImpl.java` | 移除 mock 逻辑，实现新方法 |
| `controller/ProductController.java` | 增加新接口 |
| `vo/ProductVO.java` | 增加 categoryName、description 字段 |
| `mapper/ProductMapper.java` | 无变更（用 BaseMapper 即可） |

### 接口设计

| 接口 | 路径 | 方法 | 说明 |
|------|------|------|------|
| 分页查询 | `/products/page` | POST | pageNum/pageSize/productName/skuCode/categoryId |
| 新增商品 | `/products/add` | POST | productName/skuCode/categoryId/purchasePrice/description |
| 修改商品 | `/products/update` | POST | id + 全部可编辑字段 |
| 删除商品 | `/products/delete` | POST | id，逻辑删除 |
| Excel导出 | `/products/export` | POST | 同分页查询参数，返回流 |
| 分类树 | `/categories/tree` | POST | 返回完整4级类目树 |

### 分页查询参数 (ProductPageQueryDTO)

```java
Integer pageNum = 1;    // 当前页
Integer pageSize = 10;  // 每页条数
String productName;      // 模糊查询
String skuCode;          // 模糊查询
Long categoryId;         // 精确匹配（含子分类）
```

### 分页查询逻辑

categoryId 查询时需匹配该分类及其所有子孙分类 ID。实现方式：先查出目标分类的所有子孙 ID 集合，再用 `IN` 查询。对于4级类目，数据量小，直接递归查库即可。

### Excel 导出逻辑

- 使用相同筛选参数（不含分页），查询全部匹配记录
- EasyExcel 写入 HttpServletResponse 流
- ProductExportVO 用 `@ExcelProperty` 注解定义列名和顺序
- 文件名：`商品列表_yyyyMMddHHmmss.xlsx`

### Category 树查询逻辑

- 查询所有未删除分类，按 sortOrder 排序
- 在内存中组装为树形结构返回
- 前端 ElCascader 组件消费此数据

## 前端设计

### 修改文件清单

| 文件 | 变更 |
|------|------|
| `views/ProductList.vue` | 完整改造为 CRUD 页面 |
| `api/purchase.js` | 增加商品 CRUD + 分类树 + 导出接口 |

### ProductList.vue 结构

```
┌─ 搜索栏 ─────────────────────────────────────────────┐
│ [商品名称] [SKU编码] [分类级联选择器] [查询] [重置]   │
├─ 操作栏 ─────────────────────────────────────────────┤
│ [+ 新增商品]                          [导出Excel]     │
├─ 数据表格 ────────────────────────────────────────────┤
│ ID | 商品名称 | SKU编码 | 分类 | 库存 | 采购价 | 操作 │
├─ 分页栏 ─────────────────────────────────────────────┤
│ 共X条                            < 1 2 3 >           │
└──────────────────────────────────────────────────────┘

┌─ 新增/编辑 Dialog ──────────────────────────────────┐
│ 商品名称*     SKU编码*                               │
│ 商品分类* (4级级联)                                  │
│ 采购价*                                             │
│ 商品描述                                             │
│                              [取消] [确定]           │
└──────────────────────────────────────────────────────┘
```

### UI 风格

- 主色 #1a73e8（谷歌蓝），与 Login 页面一致
- Element UI 组件：ElTable、ElPagination、ElDialog、ElForm、ElCascader
- 表格操作列：文字链接（编辑/删除）
- 删除确认：ElMessageBox.confirm
- 导出：axios POST，responseType 为 blob，前端触发下载

## 错误处理

- SKU 编码唯一性校验：新增/修改时检查 uk_sku_code，重复抛 BusinessException
- 分类不存在校验：新增/修改时验证 categoryId 存在且为末级
- 导出空数据提示：无匹配记录时返回友好提示
