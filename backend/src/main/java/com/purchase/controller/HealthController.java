package com.purchase.controller;

import com.purchase.common.Result;
import com.purchase.service.HealthService;
import com.purchase.vo.HealthStatusVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/health")
@Tag(name = "健康检查", description = "系统健康状态检查接口")
public class HealthController {

    @Resource
    private HealthService healthService;

    @PostMapping(value = "/check")
    @Operation(summary = "健康检查", description = "检查系统各组件健康状态")
    public Result<HealthStatusVO> check() {
        return Result.success(healthService.getStatus());
    }
}
