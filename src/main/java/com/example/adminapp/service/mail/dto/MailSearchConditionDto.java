package com.example.adminapp.service.mail.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

/**
 * MailSearchConditionDto Class
 */
public class MailSearchConditionDto {
    private String toAddress;         // 宛先アドレス
    private String ccAddress;         // CCアドレス
    private String bccAddress;        // BCCアドレス
    private String subjectKeyword;    // 件名
    private String status;            // 送信ステータス
    private LocalDateTime sentAtFrom; // 送信日時(開始)
    private LocalDateTime sentAtTo;   // 送信日時(終了)
    private Integer page;             //頁数
    private Integer size;             //件数
}
