package com.example.adminapp.service.mail.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Paginated result of a mail log search.
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
