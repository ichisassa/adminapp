package com.example.adminapp.service.mail;

import com.example.adminapp.service.mail.dto.MailLogSearchConditionDto;
import com.example.adminapp.service.mail.dto.MailLogSearchResponseDto;

/**
 * メール送信ログ検索サービスのインターフェース。
 */
public interface MailLogSearchService {

    /**
     * 指定された条件にもとづきメール送信ログを検索し、ページング情報付きの結果を返す。
     *
     * @param condition 送信日時やステータスなどの検索条件
     * @return ページング済みの検索結果 DTO
     */
    MailLogSearchResponseDto search(MailLogSearchConditionDto condition);
}
