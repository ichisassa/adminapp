package com.example.adminapp.service.mail.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@Setter

/**
 * MailSendResponseDto Class
 */
public class MailSendResponseDto {

    // 処理結果(True:OK、False：NG)
    private boolean success;
    // Message
    private String message;
    // Error Message(各入力項目)
    private Map<String, String> fieldErrors;
    // Error Message(全体)
    private List<String> globalErrors;

    /**
     * 処理結果(正常)
     * @param message Message
     * @return 処理結果
     */
    public static MailSendResponseDto success(String message) {
        MailSendResponseDto response = new MailSendResponseDto();
        response.setSuccess(true);
        response.setMessage(message);
        return response;
    }

    /**
     * 処理結果(異常)
     * @param message Message
     * @return 処理結果
     */
    public static MailSendResponseDto failure(
            String message,
            Map<String, String> fieldErrors,
            List<String> globalErrors) {
        MailSendResponseDto response = new MailSendResponseDto();
        response.setSuccess(false);
        response.setMessage(message);
        if (fieldErrors != null && !fieldErrors.isEmpty()) {
            response.setFieldErrors(fieldErrors);
        }
        if (globalErrors != null && !globalErrors.isEmpty()) {
            List<String> sanitized = globalErrors.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(error -> !error.isBlank())
                    .toList();
            if (!sanitized.isEmpty()) {
                response.setGlobalErrors(sanitized);
            }
        }
        return response;
    }
}
