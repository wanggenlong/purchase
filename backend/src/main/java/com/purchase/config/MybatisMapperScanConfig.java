package com.purchase.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("dev")
@Configuration
@MapperScan("com.purchase.mapper")
public class MybatisMapperScanConfig {
}
