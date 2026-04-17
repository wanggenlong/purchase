package com.purchase.controller;

import com.purchase.common.Result;
import com.purchase.dto.LoginRequestDTO;
import com.purchase.dto.TokenRequestDTO;
import com.purchase.entity.User;
import com.purchase.enums.ResultCodeEnum;
import com.purchase.service.UserService;
import com.purchase.vo.LoginResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "认证管理", description = "用户登录、获取用户信息接口")
@Slf4j
public class LoginController {

    @Resource
    private UserService userService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "根据用户名密码登录系统，返回JWT Token")
    public Result<LoginResponseVO> login(
            @RequestBody LoginRequestDTO request) {
        String username = request.getUsername();
        String password = request.getPassword();

        log.info("Login attempt for username: {}", username);

        User user = userService.getByUsername(username);
        if (user == null) {
            log.warn("User not found: {}", username);
            return Result.error(ResultCodeEnum.USER_NOT_FOUND);
        }

        log.info("User found - id: {}, username: {}, status: {}, password hash: {}",
                user.getId(), user.getUsername(), user.getStatus(), user.getPassword());

        if (user.getStatus() != 1) {
            log.warn("User disabled: {}", username);
            return Result.error(ResultCodeEnum.USER_DISABLED);
        }

        boolean matches = passwordEncoder.matches(password, user.getPassword());
        log.info("Password match result: {}", matches);

        if (!matches) {
            return Result.error(ResultCodeEnum.PASSWORD_ERROR);
        }

        String token = com.purchase.config.JwtUtil.generateToken(username);

        LoginResponseVO response = new LoginResponseVO();
        response.setToken(token);
        response.setUsername(username);
        response.setRealName(user.getRealName());

        return Result.success(response);
    }

    @PostMapping("/info")
    @Operation(summary = "获取用户信息", description = "根据Token获取当前登录用户信息")
    public Result<User> getUserInfo(
            @RequestBody TokenRequestDTO body) {
        try {
            String token = body.getToken();
            String username = com.purchase.config.JwtUtil.getUsernameFromToken(token);
            User user = userService.getByUsername(username);
            if (user == null) {
                return Result.error(ResultCodeEnum.USER_NOT_FOUND);
            }
            user.setPassword(null);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(ResultCodeEnum.DEFAULT_EXCEPTION);
        }
    }

}