package com.purchase.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "purchase.data")
@Configuration
public class DataModeProperties {

    private String mode;

}
