package com.purchase.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.profiles.active=dev")
@AutoConfigureMockMvc
class MysqlDataIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void initData() throws Exception {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/purchase_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai",
                "root",
                "root");
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS t_product (id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, product_name VARCHAR(128) NOT NULL, sku_code VARCHAR(64) NOT NULL, category_id BIGINT DEFAULT NULL, stock INT NOT NULL DEFAULT 0, purchase_price DECIMAL(10,2) NOT NULL DEFAULT 0.00, create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, is_delete TINYINT NOT NULL DEFAULT 0)");
            statement.execute("CREATE TABLE IF NOT EXISTS t_purchase_order (id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, order_no VARCHAR(64) NOT NULL, supplier_name VARCHAR(128) NOT NULL, total_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00, order_status VARCHAR(32) NOT NULL, create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, is_delete TINYINT NOT NULL DEFAULT 0)");
            statement.execute("DELETE FROM t_product");
            statement.execute("DELETE FROM t_purchase_order");
            statement.execute("INSERT INTO t_product(product_name, sku_code, stock, purchase_price, is_delete) VALUES ('MySQL商品A', 'MYSQL-A001', 66, 88.50, 0)");
            statement.execute("INSERT INTO t_purchase_order(order_no, supplier_name, total_amount, order_status, is_delete) VALUES ('MYSQL-PO-001', 'MySQL供应商', 1234.50, 'CREATED', 0)");
        }
    }

    @Test
    @WithMockUser(username = "admin")
    void shouldReturnMysqlProductData() throws Exception {
        mockMvc.perform(post("/products/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].productName").value("MySQL商品A"));
    }

    @Test
    @WithMockUser(username = "admin")
    void shouldReturnMysqlPurchaseOrderData() throws Exception {
        mockMvc.perform(post("/purchase-orders/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].orderNo").value("MYSQL-PO-001"));
    }
}
