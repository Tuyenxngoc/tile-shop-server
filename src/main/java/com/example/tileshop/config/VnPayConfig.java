package com.example.tileshop.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vnpay")
@Getter
@Setter
public class VnPayConfig {
    private String tmnCode;
    private String hashSecret;
    private String payUrl;
    private String returnUrl;
    private String version;
    private String command;
    private String orderType;
}
