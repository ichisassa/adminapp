package com.example.adminapp.controller.api;

import com.example.adminapp.service.mail.MailSearchService;
import com.example.adminapp.service.mail.MailSendService;
import com.example.adminapp.service.mail.dto.MailSearchResponseDto;
import com.example.adminapp.service.mail.dto.MailSendResponseDto;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * MailController Class
 */
@RestController
public class MailController {

    private final MailSearchService search;
    private final MailSendService   send;

    /**
     * constructor
     */
    public MailController(MailSearchService search, MailSendService send) {
        this.search = search;
        this.send = send;
    }

    /**
     * 検索処理
     * @param params 入力値
     * @return 検索結果
     */
    @GetMapping("/admin/api/mail/list")
    public MailSearchResponseDto search(@RequestParam Map<String, String> params) {
        return search.search(params);
    }

    /**
     * 送信処理
     * @param params 入力値
     * @return 送信結果
     */
    @PostMapping("/admin/api/mail/send")
    public MailSendResponseDto sendMail(@RequestParam Map<String, String> params) {
        return send.send(params);
    }
}
