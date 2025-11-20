package com.example.adminapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 管理画面アプリケーション全体を起動するエントリーポイントクラス。
 * Spring Boot の自動設定を読み込み、Web アプリを立ち上げる役割を担う。
 */
@SpringBootApplication
public class AdminApplication {
    /**
     * アプリケーション起動処理。
     * Spring Boot を初期化し、組み込みサーバで Web アプリを稼働させる。
     * @param args 入力パラメータ
     * @return     なし
     */
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
