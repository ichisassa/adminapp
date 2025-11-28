package com.example.adminapp.service.mail.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * MailSendResponseDto Class
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MailSendResponseDto {

    private boolean success;
    private String message;
    private Map<String, String> fieldErrors;
    private List<String> globalErrors;

    public static MailSendResponseDto success(String message) {
        MailSendResponseDto response = new MailSendResponseDto();
        response.setSuccess(true);
        response.setMessage(message);
        return response;
    }

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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public List<String> getGlobalErrors() {
        return globalErrors;
    }

    public void setGlobalErrors(List<String> globalErrors) {
        this.globalErrors = globalErrors;
    }
}
