package com.example.adminapp.controller.api.mail;

import com.example.adminapp.service.mail.MailLogSearchService;
import com.example.adminapp.service.mail.dto.MailLogSearchConditionDto;
import com.example.adminapp.service.mail.dto.MailLogSearchResponseDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class MailLogApiController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    private final MailLogSearchService mailLogSearchService;

    public MailLogApiController(MailLogSearchService mailLogSearchService) {
        this.mailLogSearchService = mailLogSearchService;
    }

    @GetMapping("/api/admin/mail/logs")
    public MailLogSearchResponseDto search(
            @RequestParam(value = "sentAtFrom", required = false) String sentAtFrom,
            @RequestParam(value = "sentAtTo", required = false) String sentAtTo,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "toAddress", required = false) String toAddress,
            @RequestParam(value = "subject", required = false) String subjectKeyword,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {

        MailLogSearchConditionDto condition = new MailLogSearchConditionDto();
        condition.setSentAtFrom(parseDateTime(sentAtFrom));
        condition.setSentAtTo(parseDateTime(sentAtTo));
        condition.setStatus(status);
        condition.setToAddress(toAddress);
        condition.setSubjectKeyword(subjectKeyword);
        condition.setPage(convertToZeroBased(page));
        condition.setSize(resolveSize(size));

        return mailLogSearchService.search(condition);
    }

    private Integer convertToZeroBased(Integer page) {
        if (page == null || page < 1) {
            return 0;
        }
        return page - 1;
    }

    private Integer resolveSize(Integer size) {
        if (size == null || size < 1) {
            return 20;
        }
        return size;
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid date format. Expected yyyy/MM/dd HH:mm",
                    ex);
        }
    }
}
