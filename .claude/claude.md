# 电商采购管理系统-AI开发总规则

## 0. 🔥 根目录整体结构

项目根目录固定3个文件夹，禁止修改：

1. backend/  = SpringBoot Maven后端项目
2. frontend/ = Vue2 + ElementUI前端项目
3. docs/        = 业务、数据库、版本、superpowers等文档

## 1. 基础环境

系统：Windows
JDK：21
Maven：3.9.x
MySQL：8.4
IDE：IntelliJ IDEA
架构：前后端分离

## 2. 固定技术栈和组件

### 后端

Spring Boot：3.4.5
Spring Framework：6.2.x
druid 连接池

MyBatis-Plus：3.5.6

JJWT：0.12.6

RestClient (Spring Framework 6.2 内置)

Lombok
logback

commons-io

org.apache.commons
caffeine

easyexcel

Spring Security BCrypt

SpringDoc（OpenAPI 3）

组件：统一返回体、全局异常、CORS跨域、分页、逻辑删除、自动填充时间

本地Maven仓库目录：D:\Users\genlong.wang\.m2\repository

### 前端

Vue：2.6.14
Element UI：2.15.13
Axios

Vue Router

Vuex

js-cookie

ECharts

## 3. 端口强制规范

后端SpringBoot：8081
前端Vue2：8080
禁止端口冲突

## 4. 🔥 完整代码目录结构

### 【后端 - 严格Maven目录结构】

backend/  # 后端根目录
├── pom.xml     # Maven核心配置
└── src/
    ├── main/                # 生产环境代码
    │   ├── java/                # Java源码根目录
    │   │   └── com/
    │   │       └── purchase/       # 项目主包
    │   │           ├── common     # 通用工具/统一返回/异常
    │   │           ├── config          # 配置类（CORS/MyBatis/JWT）
    │   │           ├── controller   # web控制器
    │   │           ├── dto              # 请求参数对象、各层传输对象
    │   │           ├── entity          # 数据库实体类
    │   │           ├── enums        # 枚举
    │   │           ├── filter            # 过滤器
    │   │           ├── mapper      # 数据访问层
    │   │           ├── service        # 业务逻辑
    │   │           │   └── impl       # 业务实现类
    │   │           └── vo                # 响应返回对象
    │   └── resources/     # 配置文件目录
    │       ├── application.yml               # SpringBoot主配置
    │       ├── application-dev.yml       # dev模式配置
    │       ├── application-mock.yml    # mock模式配置
    │       └── mapper/                           # MyBatis XML映射文件
    └── test/                 # 单元测试目录
        └── java/                # 测试代码（包结构同main/java）

### 【前端 - Vue2标准结构】

frontend/
├── package.json
├── src/
│   ├── api/          # 接口请求
│   ├── views/        # 页面组件
│   ├── router/       # 路由
│   ├── utils/        # 工具类
│   ├── components/   # 公共组件
│   └── store/        # 状态管理
└── public/

## 5. 代码强制规范

1. 后端所有接口统一返回 Result 格式
2. 后端尽量采用JDK和SpringBoot新特性
3. 后端Spring依赖注入用@Resource不要用@Autowired
4. 密码 BCrypt 加密存储，禁止明文
5. 添加必要的注释

## 6. 接口统一规范

1. 接口风格：@PostMapping + @RequestBody + @ResponseBody
2. 全局参数校验 + 全局异常捕获
3. 添加基于OpenAPI 3的注解@Tag、@Operation

## 7. 文档规范

1. docs/base/biz_*.md：业务文档。按业务模块拆分，一个模块对应一个文档，比如biz_login.md指登录业务文档
2. docs/base/db.md：数据库文档。存放完整的MySQL DDL（建库+表+索引+注释+引擎）
3. docs/base/changelog.md：版本文档。AI更改代码后，自动写入
4. docs/base下的所有文档仅增量更新，不删除历史内容
5. docs下base以外的其他文件夹或文档（比如superpowers），是自动生成的，无需干预

## 8. 📌 changelog.md 强制格式

## [版本号] - YYYY-MM-DD

### 新增（新功能/新模块）

### 修复（bug/报错）

### 优化（代码/体验/配置）

### 更新（文档/配置修改）

## 9. 数据库DDL规范

1. 存储引擎InnoDB，字符集utf8mb4，主键id自增
2. 所有表字段配完整中文注释，创建必要索引
3. 逻辑关联，不建物理外键
4. 必备公共字段：id、create_time、update_time、is_delete