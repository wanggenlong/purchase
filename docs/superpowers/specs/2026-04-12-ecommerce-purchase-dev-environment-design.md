# 电商采购管理系统开发环境设计文档

## 1. 目标

从零开始在 `D:\Users\genlong.wang\OneDrive\计算机学习\my_code\purchase` 下搭建一个可直接运行的“电商采购管理系统”开发环境，满足以下目标：

- 严格遵守 `cluade.md` 里的目录、技术栈、端口和文档规范。
- 建立完整目录：`backend/`、`frontend/`、`doc/`。
- 后端基于 Spring Boot 2.7.18 + Maven + JDK11 + MyBatis-Plus + Druid。
- 前端基于 Vue 2.6.14 + Element UI 2.15.13。
- 依赖安装过程优先复用本地 Maven 仓库 `D:\Users\genlong.wang\.m2\repository` 中已存在的 JAR，仅在版本不匹配时才触发下载。
- 最终前后端均可启动，并能完成一次简单 HTTP 联调。

## 2. 设计约束

### 2.1 固定目录约束

项目根目录固定建立以下目录：

- `backend/`：Spring Boot Maven 后端项目
- `frontend/`：Vue2 + Element UI 前端项目
- `doc/`：业务文档、数据库文档、版本变更文档

同时补充设计文档目录：

- `docs/superpowers/specs/`：存放本次设计与后续方案文档

### 2.2 固定技术栈约束

后端：

- Spring Boot 2.7.18
- Spring Framework 5.3.31
- Druid 连接池
- MyBatis-Plus 3.5.3
- RestTemplate + OkHttp3
- Lombok
- Logback
- commons-io
- Apache Commons
- Caffeine
- EasyExcel

前端：

- Vue 2.6.14
- Element UI 2.15.13
- Axios
- Vue Router
- Vuex
- js-cookie
- ECharts

### 2.3 固定端口约束

- 前端：`8080`
- 后端：`8081`

## 3. 推荐方案

本次采用“正式脚手架 + 优先真实数据库 + 可兜底联调”的方案。

### 3.1 核心思路

1. 先建立完整正式工程结构，而不是只做演示代码。
2. 后端优先连接本机 MySQL；若本机 MySQL 未就绪，则保留完整数据库配置、DDL 和切换能力。
3. 在数据库不可用时，允许通过最小 Mock/内存数据方式先跑通前后端 HTTP 联调，保证开箱即用。
4. 所有文档同步创建，确保后续可以继续扩展成正式项目。

### 3.2 为什么选这个方案

- 比纯 Mock 方案更贴近正式开发环境。
- 比一次性构建过多业务模块更容易成功落地。
- 能同时满足“完整搭建”和“最终联调成功”两个目标。

## 4. 系统结构设计

## 4.1 后端结构

后端目录遵循 Maven 标准结构：

- `backend/pom.xml`
- `backend/src/main/java/com/purchase/...`
- `backend/src/main/resources/application.yml`
- `backend/src/main/resources/mapper/`
- `backend/src/test/java/`

Java 包结构：

- `common`：统一返回体、常量、异常定义、基础工具
- `config`：CORS、MyBatis-Plus、RestTemplate、自动填充等配置
- `controller`：RESTful 接口
- `service`
- `service/impl`
- `mapper`
- `entity`
- `dto`
- `vo`

### 4.1.1 首批落地模块

为保证最小可用和业务贴题，首批实现以下模块：

- 健康检查模块
- 商品模块
- 采购单模块

目标是完成一条完整链路：

前端页面 → Axios 请求 → Spring Boot Controller → Service → Mapper/Mock → 统一 Result 返回。

### 4.1.2 通用能力

后端统一提供：

- `Result` 统一返回体
- 全局异常处理
- 全局参数校验
- CORS 跨域配置
- MyBatis-Plus 分页插件
- 逻辑删除字段 `is_delete`
- `create_time` / `update_time` 自动填充

## 4.2 前端结构

前端采用 Vue2 标准结构：

- `frontend/package.json`
- `frontend/public/`
- `frontend/src/api/`
- `frontend/src/views/`
- `frontend/src/router/`
- `frontend/src/store/`
- `frontend/src/utils/`
- `frontend/src/components/`

### 4.2.1 页面规划

首批页面：

- `Dashboard`：展示系统欢迎信息与后端连通状态
- `ProductList`：展示商品列表
- `PurchaseOrderList`：展示采购单列表示例

### 4.2.2 请求规划

前端通过 Axios 统一访问后端，默认 baseURL 指向 `http://localhost:8081`。

首批接口：

- `GET /api/health`
- `GET /api/products`
- `GET /api/purchase-orders`

## 5. 数据库设计

## 5.1 表设计范围

本次先提供正式 DDL，并建立最小业务表：

- `t_category`
- `t_product`
- `t_purchase_order`

## 5.2 DDL 规范

遵循 `cluade.md`：

- 存储引擎 `InnoDB`
- 字符集 `utf8mb4`
- 主键自增
- 所有字段均带中文注释
- 具备必要索引
- 不建立物理外键
- 公共字段统一包含：`id`、`create_time`、`update_time`、`is_delete`

## 5.3 数据库运行策略

采用以下优先级：

1. 优先检测本机 MySQL 环境是否可用。
2. 若可用，则自动按配置连接并执行初始化指引。
3. 若不可用，则项目仍可通过 Mock 数据完成接口联调，同时保留真实数据库配置与文档。

这样可以保证本次交付不被本机数据库缺失完全阻塞。

## 6. 依赖与安装策略

## 6.1 Maven 依赖策略

Maven 本地仓库固定为：

- `D:\Users\genlong.wang\.m2\repository`

执行原则：

1. 创建 `pom.xml` 时固定依赖版本。
2. 在触发 Maven 构建前，先检查本地仓库中是否已存在目标 GAV 对应目录和 JAR。
3. 若目标版本已存在，则直接复用，不重复下载。
4. 若不存在或版本不匹配，则允许 Maven 下载缺失依赖。

说明：Maven 本身也会优先使用本地缓存，因此本次会额外做显式检测，以满足“先检查本地已有包”的要求。

## 6.2 前端依赖策略

前端依赖通过 `npm install` 安装。

交付结果要求：

- `package.json` 完整
- 依赖可安装
- 开发命令可直接运行

## 7. 联调与验证设计

## 7.1 启动方式

后端：

- `mvn spring-boot:run`
  或
- IntelliJ IDEA 运行启动类

前端：

- `npm install`
- `npm run dev`

## 7.2 验证标准

必须验证以下内容：

1. 后端服务启动成功，监听 `8081`
2. 前端服务启动成功，监听 `8080`
3. 浏览器访问前端页面成功
4. 前端调用后端接口成功
5. 联调页面能展示后端返回的数据或状态

## 7.3 失败兜底

如果真实 MySQL 不可用，则：

- 不放弃后端工程初始化
- 不放弃前端工程初始化
- 启用最小可用 Mock 数据逻辑
- 保证前后端 HTTP 请求仍可测通

## 8. 文档规划

本次除设计文档外，还会补充以下业务文档：

- `doc/biz_purchase.md`
- `doc/db.md`
- `doc/changelog.md`

其中：

- `biz_purchase.md`：说明本次最小采购管理业务范围
- `db.md`：记录完整 DDL
- `changelog.md`：记录初始化版本内容

## 9. 非目标范围

本次不追求一次性完成完整电商采购系统的所有业务功能，例如：

- 登录鉴权
- 复杂审批流
- 多角色权限系统
- 复杂报表分析

这些内容会为后续扩展预留结构，但不作为当前环境搭建的完成条件。

## 10. 成功标准

当满足以下条件时，视为本次开发环境搭建设计成功：

- 项目目录完整建立
- 前后端技术栈落地完成
- Maven 依赖按本地仓库优先复用策略执行
- 文档文件完整生成
- 前后端均可启动
- 存在至少一条前端调后端的成功 HTTP 链路

## 11. 需确认的实现假设

由于当前对话未得到用户对数据库模式 A/B/C 的显式回复，本设计文档采用如下默认假设继续推进：

- 默认采用“C 策略”：优先接入本机 MySQL，若不可用则切换到 Mock 方案保障联调成功。

若用户后续明确要求仅真实数据库模式或仅 Mock 模式，则实现计划可在不推翻当前总体结构的前提下做小范围调整。
