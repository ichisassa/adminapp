package com.example.adminapp.service.mail;

import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * MailSendService Class
 */
@Service
public class MailSendService {

    /**
     * 送信処理
     * @param params 入力値
     */
    public void send(Map<String, String> params) {
        validate(params);
    }

    /**
     * Message取得処理
     * @return Message
     */
    public String getSuccessMessage() {
        return "メール送信処理を受け付けました（ダミー）";
    }

    /**
     * check処理
     * @param params 入力値
     */
    private void validate(Map<String, String> params) {
    }
}
