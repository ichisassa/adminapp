package com.example.adminapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * environment.properties を UTF-8 で読み込む設定クラス。
 * 外部設定値を Spring の Environment に登録する責務を持つ。
 */
@Configuration
@PropertySource(
        value = "classpath:environment.properties",
        encoding = "UTF-8"
)
public class EnvironmentPropertyConfig {
}
