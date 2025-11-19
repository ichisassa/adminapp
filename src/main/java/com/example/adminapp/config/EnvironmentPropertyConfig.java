package com.example.adminapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(
        value = "classpath:environment.properties",
        encoding = "UTF-8"
)
public class EnvironmentPropertyConfig {
    // Configuration class for loading environment.properties with UTF-8 encoding.
}
