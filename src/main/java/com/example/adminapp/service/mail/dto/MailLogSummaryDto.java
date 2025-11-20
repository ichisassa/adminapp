package com.example.adminapp.service.mail.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * メール送信ログ一覧の 1 行分に相当するサマリ DTO。
 * 画面表示に必要な要約情報のみを保持する。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailLogSummaryDto {

    private Long id;
    private String toAddress;
    private String ccAddress;
    private String bccAddress;
    private String subject;
    private String status;
    private Boolean isHtml;
    private String errorMessage;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
}
