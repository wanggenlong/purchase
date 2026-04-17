package com.purchase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "密码哈希请求")
public class HashRequestDTO {

    @Schema(description = "明文密码", example = "123456")
    private String password;

}