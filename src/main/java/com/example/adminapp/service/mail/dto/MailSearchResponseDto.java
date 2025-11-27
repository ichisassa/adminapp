package com.example.adminapp.service.mail.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

/**
 * MailSearchResponseDto Class
 */
public class MailSearchResponseDto {
    private List<MailSendListDto> items; // 検索結果
    private long totalSize;              // 総件数
    private int  totalPages;             // 総頁数
    private int page;                    // 頁数
    private int size;                    // 件数
    private boolean hasNext;             // 次頁有無
    private boolean hasPrevious;         // 前頁有無
}
