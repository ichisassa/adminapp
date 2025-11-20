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

/**
 * メール送信ログの検索 API を提供する REST コントローラ。
 * クエリパラメータから検索条件を組み立て、サービスへ受け渡す。
 */
@RestController
public class MailLogApiController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    private final MailLogSearchService mailLogSearchService;

    /** 検索サービスを受け取り API コントローラを初期化する。 */
    public MailLogApiController(MailLogSearchService mailLogSearchService) {
        this.mailLogSearchService = mailLogSearchService;
    }

    /**
     * クエリパラメータを検索条件に変換し、メールログ検索 API を実行して結果を返す。
     *
     * @param sentAtFrom 送信日時（From）
     * @param sentAtTo 送信日時（To）
     * @param status ステータス
     * @param toAddress 宛先アドレス
     * @param subjectKeyword 件名キーワード
     * @param page 表示したいページ番号（1 始まり）
     * @param size 1 ページの表示件数
     * @return 検索結果 DTO
     */
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

    /**
     * 1 始まりのページ番号をサービス層向けの 0 始まりに整形する。
     *
     * @param page 1 始まりのページ番号
     * @return 0 始まりのページ番号
     */
    private Integer convertToZeroBased(Integer page) {
        if (page == null || page < 1) {
            return 0;
        }
        return page - 1;
    }

    /**
     * ページサイズを検証し、未指定や 1 未満の場合はデフォルト値を返す。
     *
     * @param size 要求されたページサイズ
     * @return 有効なページサイズ
     */
    private Integer resolveSize(Integer size) {
        if (size == null || size < 1) {
            return 20;
        }
        return size;
    }

    /**
     * 文字列で受け取った日時を LocalDateTime に変換する。エラー時は 400 を返す。
     *
     * @param value 文字列の日時
     * @return LocalDateTime もしくは null
     */
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
