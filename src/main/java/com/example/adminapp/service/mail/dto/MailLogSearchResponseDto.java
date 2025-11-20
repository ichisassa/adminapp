package com.example.adminapp.service.mail.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * メール送信ログ検索結果を保持するレスポンス DTO。
 * ページング情報や次ページ有無を含む。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailLogSearchResponseDto {

    private List<MailLogSummaryDto> items;
    private long totalCount;
    private int page;
    private int size;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
}
