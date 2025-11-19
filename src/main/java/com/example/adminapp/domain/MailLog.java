package com.example.adminapp.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailLog {
    private Long id;
    private String toAddress;
    private String ccAddress;
    private String bccAddress;
    private String subject;
    private String body;
    private Boolean isHtml;
    private String status;
    private String errorMessage;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer version;
}
