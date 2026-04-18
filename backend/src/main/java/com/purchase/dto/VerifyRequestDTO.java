package com.purchase.dto;

import lombok.Data;

@Data
public class VerifyRequestDTO {

    /**
     * description = "明文密码", example = "123456"
     */
    private String rawPassword;

    /**
     * description = "BCrypt哈希值", example = "$2a$10$..."
     */
    private String hashedPassword;

}