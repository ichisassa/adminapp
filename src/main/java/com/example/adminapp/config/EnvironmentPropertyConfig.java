package com.example.adminapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * MailController Class
 * environment.properties
 */
@Configuration
@PropertySource(
        value = "classpath:environment.properties",
        encoding = "UTF-8"
)
public class EnvironmentPropertyConfig {
}
