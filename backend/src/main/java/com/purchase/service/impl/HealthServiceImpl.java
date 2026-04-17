package com.purchase.service.impl;

import com.purchase.config.DataModeProperties;
import com.purchase.service.HealthService;
import com.purchase.vo.HealthStatusVO;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class HealthServiceImpl implements HealthService {

    @Resource
    private DataModeProperties dataModeProperties;

    @Override
    public HealthStatusVO getStatus() {
        return new HealthStatusVO("UP", dataModeProperties.getMode(), "purchase-backend");
    }
}
