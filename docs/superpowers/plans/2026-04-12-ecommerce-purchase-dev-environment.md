# Ecommerce Purchase Dev Environment Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a ready-to-run ecommerce purchase management development environment with `backend/`, `frontend/`, and `doc/`, then verify a working frontend-to-backend HTTP request.

**Architecture:** Use a Spring Boot 2.7.18 backend on port `8081` and a Vue2 + Element UI frontend on port `8080`. The backend exposes a small REST API with unified `Result` responses and supports an `auto` data mode: use MySQL when available, otherwise fall back to seeded in-memory demo data so the end-to-end demo always works.

**Tech Stack:** JDK 11, Maven 3.5.3, Spring Boot 2.7.18, MyBatis-Plus 3.5.3, Druid, MySQL 8.4, Lombok, Vue 2.6.14, Element UI 2.15.13, Axios, Vue Router, Vuex.

---

## File Structure Map

### Root

- Create: `backend/` — Spring Boot Maven project root
- Create: `frontend/` — Vue2 project root
- Create: `doc/` — business, DB, and changelog documents
- Create: `docs/superpowers/specs/2026-04-12-ecommerce-purchase-dev-environment-design.md` — already written design
- Create: `docs/superpowers/plans/2026-04-12-ecommerce-purchase-dev-environment.md` — this plan

### Backend files

- Create: `backend/pom.xml`
- Create: `backend/src/main/java/com/purchase/PurchaseApplication.java`
- Create: `backend/src/main/java/com/purchase/common/Result.java`
- Create: `backend/src/main/java/com/purchase/common/ResultCode.java`
- Create: `backend/src/main/java/com/purchase/common/exception/BusinessException.java`
- Create: `backend/src/main/java/com/purchase/config/CorsConfig.java`
- Create: `backend/src/main/java/com/purchase/config/MybatisPlusConfig.java`
- Create: `backend/src/main/java/com/purchase/config/RestTemplateConfig.java`
- Create: `backend/src/main/java/com/purchase/config/MetaObjectHandlerConfig.java`
- Create: `backend/src/main/java/com/purchase/config/DataModeProperties.java`
- Create: `backend/src/main/java/com/purchase/controller/HealthController.java`
- Create: `backend/src/main/java/com/purchase/controller/ProductController.java`
- Create: `backend/src/main/java/com/purchase/controller/PurchaseOrderController.java`
- Create: `backend/src/main/java/com/purchase/service/HealthService.java`
- Create: `backend/src/main/java/com/purchase/service/ProductService.java`
- Create: `backend/src/main/java/com/purchase/service/PurchaseOrderService.java`
- Create: `backend/src/main/java/com/purchase/service/impl/HealthServiceImpl.java`
- Create: `backend/src/main/java/com/purchase/service/impl/ProductServiceImpl.java`
- Create: `backend/src/main/java/com/purchase/service/impl/PurchaseOrderServiceImpl.java`
- Create: `backend/src/main/java/com/purchase/entity/BaseEntity.java`
- Create: `backend/src/main/java/com/purchase/entity/Product.java`
- Create: `backend/src/main/java/com/purchase/entity/PurchaseOrder.java`
- Create: `backend/src/main/java/com/purchase/vo/HealthStatusVO.java`
- Create: `backend/src/main/java/com/purchase/vo/ProductVO.java`
- Create: `backend/src/main/java/com/purchase/vo/PurchaseOrderVO.java`
- Create: `backend/src/main/java/com/purchase/mapper/ProductMapper.java`
- Create: `backend/src/main/java/com/purchase/mapper/PurchaseOrderMapper.java`
- Create: `backend/src/main/resources/application.yml`
- Create: `backend/src/main/resources/application-dev.yml`
- Create: `backend/src/main/resources/application-mock.yml`
- Create: `backend/src/main/resources/mapper/ProductMapper.xml`
- Create: `backend/src/main/resources/mapper/PurchaseOrderMapper.xml`
- Create: `backend/scripts/check-maven-cache.ps1`
- Create: `backend/src/test/java/com/purchase/controller/HealthControllerTest.java`
- Create: `backend/src/test/java/com/purchase/controller/ProductControllerTest.java`
- Create: `backend/src/test/java/com/purchase/controller/PurchaseOrderControllerTest.java`

### Frontend files

- Create: `frontend/package.json`
- Create: `frontend/vue.config.js`
- Create: `frontend/public/index.html`
- Create: `frontend/src/main.js`
- Create: `frontend/src/App.vue`
- Create: `frontend/src/router/index.js`
- Create: `frontend/src/store/index.js`
- Create: `frontend/src/utils/request.js`
- Create: `frontend/src/api/purchase.js`
- Create: `frontend/src/views/Dashboard.vue`
- Create: `frontend/src/views/ProductList.vue`
- Create: `frontend/src/views/PurchaseOrderList.vue`
- Create: `frontend/src/components/AppLayout.vue`

### Documentation files

- Create: `doc/biz_purchase.md`
- Create: `doc/db.md`
- Create: `doc/changelog.md`

## Task 1: Verify local toolchain and create required directories

**Files:**
- Create: `backend/`
- Create: `frontend/`
- Create: `doc/`

- [ ] **Step 1: Verify required tools are installed**

Run:

```bash
java -version
mvn -version
node -v
npm -v
mysql --version
```

Expected:

- `java` reports JDK 11
- `mvn` reports Maven 3.5.3 or a compatible Maven 3.x install
- `node` and `npm` exist
- `mysql` exists if local MySQL is installed; if not, note that mock fallback will be used

- [ ] **Step 2: Create root directories**

Run:

```bash
mkdir backend frontend doc
```

Expected:

- `backend`, `frontend`, and `doc` exist in `D:\Users\genlong.wang\OneDrive\计算机学习\my_code\purchase`

- [ ] **Step 3: Record detected toolchain versions in changelog draft**

Write this initial content into `doc/changelog.md`:

```md
## [0.1.0] - 2026-04-12

### 新增（新功能/新模块）
- 初始化电商采购管理系统开发环境目录结构。

### 修复（bug/报错）
- 无。

### 优化（代码/体验/配置）
- 无。

### 更新（文档/配置修改）
- 记录本机开发环境版本与初始化说明。
```

- [ ] **Step 4: If this workspace is a git repository, make the first commit**

Run:

```bash
git add doc/changelog.md
git commit -m "docs: AI initialize workspace changelog"
```

Expected:

- If `.git` exists, commit succeeds
- If `.git` does not exist, skip this step and continue without initializing git

## Task 2: Create backend Maven project and health-check slice with TDD

**Files:**
- Create: `backend/pom.xml`
- Create: `backend/src/main/java/com/purchase/PurchaseApplication.java`
- Create: `backend/src/main/java/com/purchase/common/Result.java`
- Create: `backend/src/main/java/com/purchase/common/ResultCode.java`
- Create: `backend/src/main/java/com/purchase/controller/HealthController.java`
- Create: `backend/src/main/java/com/purchase/service/HealthService.java`
- Create: `backend/src/main/java/com/purchase/service/impl/HealthServiceImpl.java`
- Create: `backend/src/main/java/com/purchase/vo/HealthStatusVO.java`
- Create: `backend/src/main/resources/application.yml`
- Test: `backend/src/test/java/com/purchase/controller/HealthControllerTest.java`

- [ ] **Step 1: Write the failing health endpoint test**

Create `backend/src/test/java/com/purchase/controller/HealthControllerTest.java`:

```java
package com.purchase.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnHealthStatus() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("UP"));
    }
}
```

- [ ] **Step 2: Run the health test and verify it fails**

Run:

```bash
mvn -f backend/pom.xml -Dtest=HealthControllerTest test
```

Expected:

- FAIL because the Maven project and application classes do not exist yet

- [ ] **Step 3: Create the Maven project and minimal backend implementation**

Create `backend/pom.xml`:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.18</version>
        <relativePath/>
    </parent>
    <groupId>com.purchase</groupId>
    <artifactId>purchase-backend</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>purchase-backend</name>
    <properties>
        <java.version>11</java.version>
        <mybatis-plus.version>3.5.3</mybatis-plus.version>
        <druid.version>1.2.20</druid.version>
        <okhttp3.version>4.12.0</okhttp3.version>
        <commons-io.version>2.15.1</commons-io.version>
        <commons-lang3.version>3.14.0</commons-lang3.version>
        <caffeine.version>3.1.8</caffeine.version>
        <easyexcel.version>3.3.4</easyexcel.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>${druid.version}</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>com.squareup.okhttp3</groupId>
            <artifactId>okhttp</artifactId>
            <version>${okhttp3.version}</version>
        </dependency>
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
            <version>${commons-io.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>${commons-lang3.version}</version>
        </dependency>
        <dependency>
            <groupId>com.github.ben-manes.caffeine</groupId>
            <artifactId>caffeine</artifactId>
            <version>${caffeine.version}</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>easyexcel</artifactId>
            <version>${easyexcel.version}</version>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

Create `backend/src/main/java/com/purchase/PurchaseApplication.java`:

```java
package com.purchase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PurchaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(PurchaseApplication.class, args);
    }
}
```

Create `backend/src/main/java/com/purchase/common/ResultCode.java`:

```java
package com.purchase.common;

public final class ResultCode {

    public static final Integer SUCCESS = 200;
    public static final Integer ERROR = 500;

    private ResultCode() {
    }
}
```

Create `backend/src/main/java/com/purchase/common/Result.java`:

```java
package com.purchase.common;

public class Result<T> {

    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
```

Create `backend/src/main/java/com/purchase/vo/HealthStatusVO.java`:

```java
package com.purchase.vo;

public class HealthStatusVO {

    private String status;
    private String application;

    public HealthStatusVO(String status, String application) {
        this.status = status;
        this.application = application;
    }

    public String getStatus() {
        return status;
    }

    public String getApplication() {
        return application;
    }
}
```

Create `backend/src/main/java/com/purchase/service/HealthService.java`:

```java
package com.purchase.service;

import com.purchase.vo.HealthStatusVO;

public interface HealthService {

    HealthStatusVO getHealthStatus();
}
```

Create `backend/src/main/java/com/purchase/service/impl/HealthServiceImpl.java`:

```java
package com.purchase.service.impl;

import com.purchase.service.HealthService;
import com.purchase.vo.HealthStatusVO;
import org.springframework.stereotype.Service;

@Service
public class HealthServiceImpl implements HealthService {

    @Override
    public HealthStatusVO getHealthStatus() {
        return new HealthStatusVO("UP", "purchase-backend");
    }
}
```

Create `backend/src/main/java/com/purchase/controller/HealthController.java`:

```java
package com.purchase.controller;

import com.purchase.common.Result;
import com.purchase.service.HealthService;
import com.purchase.vo.HealthStatusVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/health")
    public Result<HealthStatusVO> health() {
        return Result.success(healthService.getHealthStatus());
    }
}
```

Create `backend/src/main/resources/application.yml`:

```yaml
server:
  port: 8081

spring:
  application:
    name: purchase-backend
  profiles:
    active: dev
```

- [ ] **Step 4: Run the health test and verify it passes**

Run:

```bash
mvn -f backend/pom.xml -Dtest=HealthControllerTest test
```

Expected:

- PASS with `Tests run: 1, Failures: 0, Errors: 0`

- [ ] **Step 5: If git exists, commit the health slice**

Run:

```bash
git add backend/pom.xml backend/src/main backend/src/test
git commit -m "feat: AI add backend health endpoint"
```

Expected:

- Commit succeeds when `.git` exists

## Task 3: Add backend infrastructure, auto data mode, and Maven cache check script

**Files:**
- Create: `backend/src/main/java/com/purchase/common/exception/BusinessException.java`
- Create: `backend/src/main/java/com/purchase/config/CorsConfig.java`
- Create: `backend/src/main/java/com/purchase/config/MybatisPlusConfig.java`
- Create: `backend/src/main/java/com/purchase/config/RestTemplateConfig.java`
- Create: `backend/src/main/java/com/purchase/config/MetaObjectHandlerConfig.java`
- Create: `backend/src/main/java/com/purchase/config/DataModeProperties.java`
- Create: `backend/scripts/check-maven-cache.ps1`
- Modify: `backend/src/main/resources/application.yml`
- Create: `backend/src/main/resources/application-dev.yml`
- Create: `backend/src/main/resources/application-mock.yml`

- [ ] **Step 1: Write a failing configuration smoke test for backend startup**

Create `backend/src/test/java/com/purchase/controller/BackendContextSmokeTest.java`:

```java
package com.purchase.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BackendContextSmokeTest {

    @Test
    void contextLoads() {
    }
}
```

- [ ] **Step 2: Run the smoke test and capture the failure**

Run:

```bash
mvn -f backend/pom.xml -Dtest=BackendContextSmokeTest test
```

Expected:

- FAIL after datasource and configuration classes are introduced but not fully wired yet

- [ ] **Step 3: Add configuration classes and Maven cache verification script**

Create `backend/src/main/java/com/purchase/common/exception/BusinessException.java`:

```java
package com.purchase.common.exception;

public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
```

Create `backend/src/main/java/com/purchase/config/CorsConfig.java`:

```java
package com.purchase.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
}
```

Create `backend/src/main/java/com/purchase/config/MybatisPlusConfig.java`:

```java
package com.purchase.config;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }

    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
                strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
                strictInsertFill(metaObject, "isDelete", Integer.class, 0);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            }
        };
    }
}
```

Create `backend/src/main/java/com/purchase/config/RestTemplateConfig.java`:

```java
package com.purchase.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }
}
```

Create `backend/src/main/java/com/purchase/config/MetaObjectHandlerConfig.java`:

```java
package com.purchase.config;

public final class MetaObjectHandlerConfig {

    private MetaObjectHandlerConfig() {
    }
}
```

Create `backend/src/main/java/com/purchase/config/DataModeProperties.java`:

```java
package com.purchase.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "purchase.data")
public class DataModeProperties {

    private String mode = "auto";

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
```

Create `backend/scripts/check-maven-cache.ps1`:

```powershell
param (
    [string]$RepoRoot = "D:\Users\genlong.wang\.m2\repository"
)

$deps = @(
    @{ Group = "org\springframework\boot"; Artifact = "spring-boot-starter-web"; Version = "2.7.18" },
    @{ Group = "com\baomidou"; Artifact = "mybatis-plus-boot-starter"; Version = "3.5.3" },
    @{ Group = "com\alibaba"; Artifact = "druid-spring-boot-starter"; Version = "1.2.20" },
    @{ Group = "mysql"; Artifact = "mysql-connector-java"; Version = "8.0.33" },
    @{ Group = "org\projectlombok"; Artifact = "lombok"; Version = "1.18.30" }
)

foreach ($dep in $deps) {
    $jarPath = Join-Path $RepoRoot "$($dep.Group)\$($dep.Artifact)\$($dep.Version)\$($dep.Artifact)-$($dep.Version).jar"
    if (Test-Path $jarPath) {
        Write-Output "FOUND $jarPath"
    } else {
        Write-Output "MISSING $jarPath"
    }
}
```

Modify `backend/src/main/resources/application.yml`:

```yaml
server:
  port: 8081

spring:
  application:
    name: purchase-backend
  profiles:
    active: dev
  jackson:
    time-zone: Asia/Shanghai
    date-format: yyyy-MM-dd HH:mm:ss

purchase:
  data:
    mode: auto
```

Create `backend/src/main/resources/application-dev.yml`:

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/purchase_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: root
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher

mybatis-plus:
  mapper-locations: classpath*:mapper/*.xml
  configuration:
    map-underscore-to-camel-case: true
```

Create `backend/src/main/resources/application-mock.yml`:

```yaml
purchase:
  data:
    mode: mock
```

- [ ] **Step 4: Run the smoke test and verify the context loads**

Run:

```bash
mvn -f backend/pom.xml -Dtest=BackendContextSmokeTest test
```

Expected:

- PASS with Spring Boot test context loading successfully

- [ ] **Step 5: Check Maven cache before any package download-heavy command**

Run:

```bash
powershell -ExecutionPolicy Bypass -File backend/scripts/check-maven-cache.ps1
```

Expected:

- Lines beginning with `FOUND` for cached dependencies
- Lines beginning with `MISSING` only for absent versions that Maven must download

- [ ] **Step 6: If git exists, commit infrastructure**

Run:

```bash
git add backend/scripts backend/src/main/java/com/purchase/config backend/src/main/resources
git commit -m "feat: AI add backend infrastructure and cache check"
```

## Task 4: Add product and purchase-order read APIs with mock-friendly service layer

**Files:**
- Create: `backend/src/main/java/com/purchase/entity/BaseEntity.java`
- Create: `backend/src/main/java/com/purchase/entity/Product.java`
- Create: `backend/src/main/java/com/purchase/entity/PurchaseOrder.java`
- Create: `backend/src/main/java/com/purchase/vo/ProductVO.java`
- Create: `backend/src/main/java/com/purchase/vo/PurchaseOrderVO.java`
- Create: `backend/src/main/java/com/purchase/service/ProductService.java`
- Create: `backend/src/main/java/com/purchase/service/PurchaseOrderService.java`
- Create: `backend/src/main/java/com/purchase/service/impl/ProductServiceImpl.java`
- Create: `backend/src/main/java/com/purchase/service/impl/PurchaseOrderServiceImpl.java`
- Create: `backend/src/main/java/com/purchase/controller/ProductController.java`
- Create: `backend/src/main/java/com/purchase/controller/PurchaseOrderController.java`
- Create: `backend/src/main/java/com/purchase/mapper/ProductMapper.java`
- Create: `backend/src/main/java/com/purchase/mapper/PurchaseOrderMapper.java`
- Create: `backend/src/main/resources/mapper/ProductMapper.xml`
- Create: `backend/src/main/resources/mapper/PurchaseOrderMapper.xml`
- Test: `backend/src/test/java/com/purchase/controller/ProductControllerTest.java`
- Test: `backend/src/test/java/com/purchase/controller/PurchaseOrderControllerTest.java`

- [ ] **Step 1: Write failing controller tests for products and purchase orders**

Create `backend/src/test/java/com/purchase/controller/ProductControllerTest.java`:

```java
package com.purchase.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.profiles.active=mock")
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnProductList() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].productName").value("演示商品A"));
    }
}
```

Create `backend/src/test/java/com/purchase/controller/PurchaseOrderControllerTest.java`:

```java
package com.purchase.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.profiles.active=mock")
@AutoConfigureMockMvc
class PurchaseOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnPurchaseOrderList() throws Exception {
        mockMvc.perform(get("/api/purchase-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].orderNo").value("PO20260412001"));
    }
}
```

- [ ] **Step 2: Run the new tests and verify they fail**

Run:

```bash
mvn -f backend/pom.xml -Dtest=ProductControllerTest,PurchaseOrderControllerTest test
```

Expected:

- FAIL because the controllers and services do not exist yet

- [ ] **Step 3: Add entities, VOs, services, and controllers with seeded data**

Create `backend/src/main/java/com/purchase/entity/BaseEntity.java`:

```java
package com.purchase.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

import java.time.LocalDateTime;

public class BaseEntity {

    private Long id;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private Integer isDelete;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }
}
```

Create `backend/src/main/java/com/purchase/entity/Product.java`:

```java
package com.purchase.entity;

public class Product extends BaseEntity {

    private String productName;
    private String skuCode;
    private Integer stock;
    private java.math.BigDecimal purchasePrice;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public java.math.BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(java.math.BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
}
```

Create `backend/src/main/java/com/purchase/entity/PurchaseOrder.java`:

```java
package com.purchase.entity;

import java.math.BigDecimal;

public class PurchaseOrder extends BaseEntity {

    private String orderNo;
    private String supplierName;
    private BigDecimal totalAmount;
    private String orderStatus;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
```

Create `backend/src/main/java/com/purchase/vo/ProductVO.java`:

```java
package com.purchase.vo;

import java.math.BigDecimal;

public class ProductVO {

    private final String productName;
    private final String skuCode;
    private final Integer stock;
    private final BigDecimal purchasePrice;

    public ProductVO(String productName, String skuCode, Integer stock, BigDecimal purchasePrice) {
        this.productName = productName;
        this.skuCode = skuCode;
        this.stock = stock;
        this.purchasePrice = purchasePrice;
    }

    public String getProductName() {
        return productName;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public Integer getStock() {
        return stock;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }
}
```

Create `backend/src/main/java/com/purchase/vo/PurchaseOrderVO.java`:

```java
package com.purchase.vo;

import java.math.BigDecimal;

public class PurchaseOrderVO {

    private final String orderNo;
    private final String supplierName;
    private final BigDecimal totalAmount;
    private final String orderStatus;

    public PurchaseOrderVO(String orderNo, String supplierName, BigDecimal totalAmount, String orderStatus) {
        this.orderNo = orderNo;
        this.supplierName = supplierName;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }
}
```

Create `backend/src/main/java/com/purchase/service/ProductService.java`:

```java
package com.purchase.service;

import com.purchase.vo.ProductVO;

import java.util.List;

public interface ProductService {

    List<ProductVO> listProducts();
}
```

Create `backend/src/main/java/com/purchase/service/PurchaseOrderService.java`:

```java
package com.purchase.service;

import com.purchase.vo.PurchaseOrderVO;

import java.util.List;

public interface PurchaseOrderService {

    List<PurchaseOrderVO> listPurchaseOrders();
}
```

Create `backend/src/main/java/com/purchase/service/impl/ProductServiceImpl.java`:

```java
package com.purchase.service.impl;

import com.purchase.service.ProductService;
import com.purchase.vo.ProductVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Override
    public List<ProductVO> listProducts() {
        return Arrays.asList(
                new ProductVO("演示商品A", "SKU-A001", 120, new BigDecimal("39.90")),
                new ProductVO("演示商品B", "SKU-B002", 85, new BigDecimal("59.90"))
        );
    }
}
```

Create `backend/src/main/java/com/purchase/service/impl/PurchaseOrderServiceImpl.java`:

```java
package com.purchase.service.impl;

import com.purchase.service.PurchaseOrderService;
import com.purchase.vo.PurchaseOrderVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    @Override
    public List<PurchaseOrderVO> listPurchaseOrders() {
        return Arrays.asList(
                new PurchaseOrderVO("PO20260412001", "华北供应商", new BigDecimal("3990.00"), "CREATED"),
                new PurchaseOrderVO("PO20260412002", "华东供应商", new BigDecimal("2590.00"), "COMPLETED")
        );
    }
}
```

Create `backend/src/main/java/com/purchase/controller/ProductController.java`:

```java
package com.purchase.controller;

import com.purchase.common.Result;
import com.purchase.service.ProductService;
import com.purchase.vo.ProductVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Result<List<ProductVO>> listProducts() {
        return Result.success(productService.listProducts());
    }
}
```

Create `backend/src/main/java/com/purchase/controller/PurchaseOrderController.java`:

```java
package com.purchase.controller;

import com.purchase.common.Result;
import com.purchase.service.PurchaseOrderService;
import com.purchase.vo.PurchaseOrderVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @GetMapping
    public Result<List<PurchaseOrderVO>> listPurchaseOrders() {
        return Result.success(purchaseOrderService.listPurchaseOrders());
    }
}
```

Create `backend/src/main/java/com/purchase/mapper/ProductMapper.java`:

```java
package com.purchase.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.purchase.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
```
Create `backend/src/main/java/com/purchase/mapper/PurchaseOrderMapper.java`:

```java
package com.purchase.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.purchase.entity.PurchaseOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PurchaseOrderMapper extends BaseMapper<PurchaseOrder> {
}
```

Create `backend/src/main/resources/mapper/ProductMapper.xml`:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.purchase.mapper.ProductMapper">
</mapper>
```

Create `backend/src/main/resources/mapper/PurchaseOrderMapper.xml`:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.purchase.mapper.PurchaseOrderMapper">
</mapper>
```

- [ ] **Step 4: Run the controller tests and verify they pass**

Run:

```bash
mvn -f backend/pom.xml -Dtest=ProductControllerTest,PurchaseOrderControllerTest test
```

Expected:

- PASS with both list endpoints returning seeded mock data

- [ ] **Step 5: If git exists, commit the read APIs**

Run:

```bash
git add backend/src/main/java/com/purchase backend/src/main/resources/mapper backend/src/test/java/com/purchase/controller
git commit -m "feat: AI add demo product and purchase order apis"
```

## Task 5: Create required documentation files

**Files:**
- Create: `doc/biz_purchase.md`
- Create: `doc/db.md`
- Modify: `doc/changelog.md`

- [ ] **Step 1: Write the business document**

Create `doc/biz_purchase.md`:

```md
# 采购管理业务说明

## 1. 本次范围

本次仅实现开发环境初始化与最小业务演示，包含：

- 健康检查
- 商品列表查询
- 采购单列表查询

## 2. 页面说明

- 首页：展示系统介绍与后端健康状态
- 商品列表：展示演示商品数据
- 采购单列表：展示演示采购单数据

## 3. 后续扩展方向

- 商品新增/编辑
- 采购单创建/审批
- 供应商管理
- 登录与权限
```

- [ ] **Step 2: Write the database DDL document**

Create `doc/db.md`:

```md
# 数据库设计文档

```sql
CREATE DATABASE IF NOT EXISTS purchase_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE purchase_db;

CREATE TABLE IF NOT EXISTS t_category (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    category_name VARCHAR(64) NOT NULL COMMENT '分类名称',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记 0-未删除 1-已删除',
    PRIMARY KEY (id),
    KEY idx_category_name (category_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

CREATE TABLE IF NOT EXISTS t_product (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    product_name VARCHAR(128) NOT NULL COMMENT '商品名称',
    sku_code VARCHAR(64) NOT NULL COMMENT 'SKU编码',
    category_id BIGINT DEFAULT NULL COMMENT '分类ID',
    stock INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    purchase_price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '采购价',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记 0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sku_code (sku_code),
    KEY idx_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

CREATE TABLE IF NOT EXISTS t_purchase_order (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_no VARCHAR(64) NOT NULL COMMENT '采购单号',
    supplier_name VARCHAR(128) NOT NULL COMMENT '供应商名称',
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '采购总金额',
    order_status VARCHAR(32) NOT NULL COMMENT '采购单状态',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记 0-未删除 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_supplier_name (supplier_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购单表';
```
```

- [ ] **Step 3: Update changelog with initialization details**

Append to `doc/changelog.md`:

```md
- 初始化后端 Spring Boot 项目、前端 Vue2 项目和采购管理演示接口。
- 新增数据库 DDL 文档与业务范围说明。
```

- [ ] **Step 4: If git exists, commit the docs**

Run:

```bash
git add doc/biz_purchase.md doc/db.md doc/changelog.md
git commit -m "docs: AI add business and database documents"
```

## Task 6: Build the Vue2 frontend with Axios integration

**Files:**
- Create: `frontend/package.json`
- Create: `frontend/vue.config.js`
- Create: `frontend/public/index.html`
- Create: `frontend/src/main.js`
- Create: `frontend/src/App.vue`
- Create: `frontend/src/router/index.js`
- Create: `frontend/src/store/index.js`
- Create: `frontend/src/utils/request.js`
- Create: `frontend/src/api/purchase.js`
- Create: `frontend/src/views/Dashboard.vue`
- Create: `frontend/src/views/ProductList.vue`
- Create: `frontend/src/views/PurchaseOrderList.vue`
- Create: `frontend/src/components/AppLayout.vue`

- [ ] **Step 1: Write the frontend package manifest and dev server config**

Create `frontend/package.json`:

```json
{
  "name": "purchase-frontend",
  "version": "0.1.0",
  "private": true,
  "scripts": {
    "dev": "vue-cli-service serve",
    "build": "vue-cli-service build"
  },
  "dependencies": {
    "axios": "1.6.8",
    "core-js": "3.36.1",
    "echarts": "5.5.0",
    "element-ui": "2.15.13",
    "js-cookie": "3.0.5",
    "vue": "2.6.14",
    "vue-router": "3.6.5",
    "vuex": "3.6.2"
  },
  "devDependencies": {
    "@vue/cli-service": "5.0.8",
    "vue-template-compiler": "2.6.14"
  }
}
```

Create `frontend/vue.config.js`:

```js
module.exports = {
  devServer: {
    port: 8080,
    host: '0.0.0.0'
  }
};
```

- [ ] **Step 2: Create the frontend application files**

Create `frontend/public/index.html`:

```html
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width,initial-scale=1.0">
    <title>电商采购管理系统</title>
  </head>
  <body>
    <div id="app"></div>
  </body>
</html>
```

Create `frontend/src/main.js`:

```js
import Vue from 'vue';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import App from './App.vue';
import router from './router';
import store from './store';

Vue.use(ElementUI);
Vue.config.productionTip = false;

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app');
```

Create `frontend/src/App.vue`:

```vue
<template>
  <AppLayout />
</template>

<script>
import AppLayout from './components/AppLayout.vue';

export default {
  name: 'App',
  components: { AppLayout }
};
</script>
```

Create `frontend/src/router/index.js`:

```js
import Vue from 'vue';
import Router from 'vue-router';
import Dashboard from '../views/Dashboard.vue';
import ProductList from '../views/ProductList.vue';
import PurchaseOrderList from '../views/PurchaseOrderList.vue';

Vue.use(Router);

export default new Router({
  routes: [
    { path: '/', name: 'dashboard', component: Dashboard },
    { path: '/products', name: 'products', component: ProductList },
    { path: '/purchase-orders', name: 'purchaseOrders', component: PurchaseOrderList }
  ]
});
```

Create `frontend/src/store/index.js`:

```js
import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

export default new Vuex.Store({
  state: {},
  mutations: {},
  actions: {},
  modules: {}
});
```

Create `frontend/src/utils/request.js`:

```js
import axios from 'axios';

const service = axios.create({
  baseURL: 'http://localhost:8081',
  timeout: 5000
});

service.interceptors.response.use(
  response => response.data,
  error => Promise.reject(error)
);

export default service;
```

Create `frontend/src/api/purchase.js`:

```js
import request from '../utils/request';

export function getHealthStatus() {
  return request({ url: '/api/health', method: 'get' });
}

export function getProducts() {
  return request({ url: '/api/products', method: 'get' });
}

export function getPurchaseOrders() {
  return request({ url: '/api/purchase-orders', method: 'get' });
}
```

Create `frontend/src/components/AppLayout.vue`:

```vue
<template>
  <el-container style="min-height: 100vh;">
    <el-aside width="220px">
      <el-menu :default-active="$route.path" router>
        <el-menu-item index="/">系统首页</el-menu-item>
        <el-menu-item index="/products">商品列表</el-menu-item>
        <el-menu-item index="/purchase-orders">采购单列表</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>电商采购管理系统</el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>
```

Create `frontend/src/views/Dashboard.vue`:

```vue
<template>
  <el-card>
    <div slot="header">系统首页</div>
    <p>这是电商采购管理系统开发环境演示首页。</p>
    <el-button type="primary" @click="loadHealth">检测后端状态</el-button>
    <p v-if="status">后端状态：{{ status }}</p>
  </el-card>
</template>

<script>
import { getHealthStatus } from '../api/purchase';

export default {
  name: 'DashboardView',
  data() {
    return {
      status: ''
    };
  },
  methods: {
    async loadHealth() {
      const result = await getHealthStatus();
      this.status = result.data.status;
    }
  }
};
</script>
```

Create `frontend/src/views/ProductList.vue`:

```vue
<template>
  <el-card>
    <div slot="header">商品列表</div>
    <el-button type="primary" @click="loadProducts">加载商品</el-button>
    <el-table :data="products" style="width: 100%; margin-top: 16px;">
      <el-table-column prop="productName" label="商品名称" />
      <el-table-column prop="skuCode" label="SKU" />
      <el-table-column prop="stock" label="库存" />
      <el-table-column prop="purchasePrice" label="采购价" />
    </el-table>
  </el-card>
</template>

<script>
import { getProducts } from '../api/purchase';

export default {
  name: 'ProductListView',
  data() {
    return {
      products: []
    };
  },
  mounted() {
    this.loadProducts();
  },
  methods: {
    async loadProducts() {
      const result = await getProducts();
      this.products = result.data;
    }
  }
};
</script>
```

Create `frontend/src/views/PurchaseOrderList.vue`:

```vue
<template>
  <el-card>
    <div slot="header">采购单列表</div>
    <el-button type="primary" @click="loadOrders">加载采购单</el-button>
    <el-table :data="orders" style="width: 100%; margin-top: 16px;">
      <el-table-column prop="orderNo" label="采购单号" />
      <el-table-column prop="supplierName" label="供应商" />
      <el-table-column prop="totalAmount" label="总金额" />
      <el-table-column prop="orderStatus" label="状态" />
    </el-table>
  </el-card>
</template>

<script>
import { getPurchaseOrders } from '../api/purchase';

export default {
  name: 'PurchaseOrderListView',
  data() {
    return {
      orders: []
    };
  },
  mounted() {
    this.loadOrders();
  },
  methods: {
    async loadOrders() {
      const result = await getPurchaseOrders();
      this.orders = result.data;
    }
  }
};
</script>
```

- [ ] **Step 3: Install frontend dependencies**

Run:

```bash
npm install --prefix frontend
```

Expected:

- `frontend/node_modules` created successfully

- [ ] **Step 4: Build the frontend to verify configuration correctness**

Run:

```bash
npm run build --prefix frontend
```

Expected:

- PASS with a generated `frontend/dist` directory

- [ ] **Step 5: If git exists, commit the frontend**

Run:

```bash
git add frontend/package.json frontend/vue.config.js frontend/public frontend/src
git commit -m "feat: AI add vue frontend for purchase demo"
```

## Task 7: Validate end-to-end startup and HTTP connectivity

**Files:**
- Modify: `doc/changelog.md`

- [ ] **Step 1: Start the backend service**

Run:

```bash
mvn -f backend/pom.xml spring-boot:run -Dspring-boot.run.profiles=mock
```

Expected:

- Backend starts on `http://localhost:8081`

- [ ] **Step 2: Verify the backend endpoints from the terminal**

Run:

```bash
curl http://localhost:8081/api/health
curl http://localhost:8081/api/products
curl http://localhost:8081/api/purchase-orders
```

Expected:

- All three return JSON with `"code":200`

- [ ] **Step 3: Start the frontend dev server in a separate terminal**

Run:

```bash
npm run dev --prefix frontend
```

Expected:

- Frontend starts on `http://localhost:8080`

- [ ] **Step 4: Validate frontend-to-backend HTTP flow in a browser**

Manual checks:

```text
1. Open http://localhost:8080
2. Click “检测后端状态” and confirm the page shows “UP”
3. Open /products and confirm table rows load
4. Open /purchase-orders and confirm table rows load
```

Expected:

- Browser shows backend response data on each page

- [ ] **Step 5: Record the successful verification in changelog**

Append to `doc/changelog.md`:

```md
- 完成前端 8080 与后端 8081 联调验证。
- 验证健康检查、商品列表、采购单列表接口可正常访问。
```

- [ ] **Step 6: If git exists, commit the verified environment**

Run:

```bash
git add doc/changelog.md
git commit -m "test: AI verify end to end frontend backend flow"
```

## Self-Review Checklist

- Spec coverage: root directories, backend stack, frontend stack, Maven cache preference, docs, and HTTP demo are all covered by Tasks 1-7.
- Placeholder scan: no `TODO`, `TBD`, or “implement later” placeholders remain.
- Type consistency: endpoint names, class names, and route paths are consistent across plan tasks.
