package com.example.adminapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AdminApplication Class
 */
@SpringBootApplication
public class AdminApplication {
    /**
     * 起動処理
     * @param args 入力値
     * @return
     */
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
