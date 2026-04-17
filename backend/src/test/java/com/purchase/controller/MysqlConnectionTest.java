package com.purchase.controller;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MysqlConnectionTest {

    @Test
    void shouldConnectMysqlAndCreateDatabase() throws Exception {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai",
                "root",
                "root");
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE DATABASE IF NOT EXISTS purchase_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");
            assertTrue(true);
        }
    }
}
