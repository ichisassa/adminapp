package com.example.adminapp.service.mail.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Captures filter and pagination parameters for the mail log list.
 * Page index is zero-based; size represents items per page.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailLogSearchConditionDto {

    private String toAddress;
    private String ccAddress;
    private String bccAddress;
    private String subjectKeyword;
    private String status;
    private LocalDateTime sentAtFrom;
    private LocalDateTime sentAtTo;
    private Integer page;
    private Integer size;
}
