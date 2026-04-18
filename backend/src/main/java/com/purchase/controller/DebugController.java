package com.purchase.controller;

import com.purchase.dto.HashRequestDTO;
import com.purchase.dto.VerifyRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/debug")
@Tag(name = "调试工具", description = "密码哈希生成与验证工具")
public class DebugController {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @PostMapping("/hash")
    @Operation(summary = "生成密码哈希", description = "将明文密码生成为BCrypt哈希值")
    public String generateHash(@RequestBody HashRequestDTO request) {
        return encoder.encode(request.getPassword());
    }

    @PostMapping("/verify")
    @Operation(summary = "验证密码", description = "验证明文密码与BCrypt哈希值是否匹配")
    public boolean verifyPassword(@RequestBody VerifyRequestDTO request) {
        return encoder.matches(request.getRawPassword(), request.getHashedPassword());
    }

}