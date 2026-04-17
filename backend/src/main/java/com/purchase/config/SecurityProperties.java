package com.purchase.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Security permit-all patterns configuration
 */
@Data
@ConfigurationProperties(prefix = "purchase.security")
public class SecurityProperties {

    private List<String> permitAll;

}
