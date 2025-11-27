package com.example.adminapp.controller.api;

import com.example.adminapp.service.mail.MailSearchService;
import com.example.adminapp.service.mail.dto.MailSearchResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/**
 * MailController Class
 */
@RestController
public class MailController {

    private final MailSearchService service;

    /**
     * constructor
     */
    public MailController(MailSearchService service) {
        this.service = service;
    }

    /**
     * 検索処理
     * @param params 入力値
     * @return 検索結果
     */
    @GetMapping("/api/admin/mail/logs")
    public MailSearchResponseDto search(@RequestParam Map<String, String> params) {
        return service.search(params);
    }

}
