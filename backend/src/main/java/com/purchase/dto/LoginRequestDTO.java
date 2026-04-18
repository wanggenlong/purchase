package com.purchase.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {

    /**
     * description = "BCrypt哈希值", example = "$2a$10$..."
     */
    private String username;

    /**
     * description = "BCrypt哈希值", example = "$2a$10$..."
     */
    private String password;

}