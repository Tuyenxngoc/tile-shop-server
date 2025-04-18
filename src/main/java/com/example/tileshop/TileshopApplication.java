package com.example.tileshop;

import com.example.tileshop.config.CloudinaryConfig;
import com.example.tileshop.config.MailConfig;
import com.example.tileshop.config.VnPayConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties({MailConfig.class, CloudinaryConfig.class, VnPayConfig.class})
public class TileshopApplication {

    public static void main(String[] args) {
        Environment env = SpringApplication.run(TileshopApplication.class, args).getEnvironment();
        String appName = env.getProperty("spring.application.name");
        if (appName != null) {
            appName = appName.toUpperCase();
        }
        String port = env.getProperty("server.port");
        log.info("-------------------------START {} Application------------------------------", appName);
        log.info("   Application         : {}", appName);
        log.info("   Url swagger-ui      : http://localhost:{}/swagger-ui.html", port);
        log.info("-------------------------START SUCCESS {} Application----------------------", appName);
    }

}
