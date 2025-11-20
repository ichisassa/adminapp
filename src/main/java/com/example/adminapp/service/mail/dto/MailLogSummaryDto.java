package com.example.adminapp.service.mail.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents one row on the mail log search result list.
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
