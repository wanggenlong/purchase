package com.purchase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "密码验证请求")
public class VerifyRequestDTO {

    @Schema(description = "明文密码", example = "123456")
    private String rawPassword;

    @Schema(description = "BCrypt哈希值", example = "$2a$10$...")
    private String hashedPassword;

}