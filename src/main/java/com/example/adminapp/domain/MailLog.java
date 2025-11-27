package com.example.adminapp.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * MailLog Class
 */
public class MailLog {                  // メール送信ログ(mail_log)
    private Long id;                    // ID
    private String toAddress;           // 宛先アドレス
    private String ccAddress;           // CCアドレス
    private String bccAddress;          // BCCアドレス
    private String subject;             // 件名
    private String body;                // 本文
    private Boolean isHtml;             // HTMLメールフラグ
    private String status;              // 送信ステータス
    private String errorMessage;        // エラーメッセージ
    private LocalDateTime sentAt;       // 送信日時
    private LocalDateTime createdAt;    // 作成日時
    private LocalDateTime updatedAt;    // 更新日時
    private Integer version;            // バージョン
}
