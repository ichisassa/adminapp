package com.example.adminapp.service.mail.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

/**
 * MailSendListDto Class
 */
public class MailSendListDto {
    private Long id;                  // ID
    private String toAddress;         // 宛先アドレス
    private String ccAddress;         // CCアドレス
    private String bccAddress;        // BCCアドレス
    private String subject;           // 件名
    private String status;            // 送信ステータス
    private Boolean isHtml;           // HTMLメールフラグ
    private String errorMessage;      // エラーメッセージ
    private LocalDateTime sentAt;     // 送信日時
    private LocalDateTime createdAt;  // 作成日時
}
