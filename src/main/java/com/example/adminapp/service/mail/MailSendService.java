package com.example.adminapp.service.mail;

import com.example.adminapp.service.mail.dto.MailSendResponseDto;
import com.example.adminapp.validation.FormValidator;
import com.example.adminapp.validation.mail.MailSendField;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * MailSendService Class
 */
@Service
public class MailSendService {

    private final FormValidator validator;

    /**
     * constructor
     */
    public MailSendService(FormValidator validator) {
        this.validator = validator;
    }

    /**
     * 送信処理
     * @param params 入力値
     * @return 送信結果
     */
    public MailSendResponseDto send(Map<String, String> params) {
        Map<String, String> errors = validator.validate(MailSendField.class, params);
        if (!errors.isEmpty()) {
            // NG
            return MailSendResponseDto.failure("入力内容にエラーがあります", errors, null);
        }
        //OK
        return MailSendResponseDto.success(getSuccessMessage());
    }

    /**
     * Message取得処理(OK)
     * @param params 入力値
     * @return 送信結果
     */
    private String getSuccessMessage() {
        return "メール送信処理を受け付けました（ダミー）";
    }
}
