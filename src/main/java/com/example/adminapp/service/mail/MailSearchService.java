package com.example.adminapp.service.mail;

import com.example.adminapp.mapper.MailSearchMapper;
import com.example.adminapp.service.mail.dto.MailSearchConditionDto;
import com.example.adminapp.service.mail.dto.MailSearchResponseDto;
import com.example.adminapp.service.mail.dto.MailSendListDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * MailSearchService Class
 */
@Service
public class MailSearchService {

    // 日時形式
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    // Max頁、Max件数
    private static final int DEFAULT_PAGE_INDEX = 0;
    private static final int DEFAULT_PAGE_SIZE = 20;

    // MyBatis(Mapper)
    private final MailSearchMapper mapper;

    /**
     * constructor
     */
    public MailSearchService(MailSearchMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 検索処理
     * @param params 入力値
     * @return 検索結果
     */
    public MailSearchResponseDto search(Map<String, String> rawParams) {
        MailSearchConditionDto condition = buildCondition(rawParams);
        Paging paging = normalizePaging(condition.getPage(), condition.getSize());
        condition.setPage(paging.page());
        condition.setSize(paging.size());

        long totalCount = mapper.countByCondition(condition);
        List<MailSendListDto> items = totalCount == 0
                ? Collections.emptyList()
                : mapper.findByCondition(condition, paging.offset(), paging.size());

        return buildResponse(items, totalCount, paging);
    }

    /**
     * 検索条件生成処理
     * @param params 入力値
     * @return 検索条件
     */
    private MailSearchConditionDto buildCondition(Map<String, String> params) {
        MailSearchConditionDto condition = new MailSearchConditionDto();
        condition.setSentAtFrom(parseDateTime(params, "sentAtFrom"));
        condition.setSentAtTo(parseDateTime(params, "sentAtTo"));
        condition.setStatus(extract(params, "status"));
        condition.setToAddress(extract(params, "toAddress"));
        condition.setSubjectKeyword(resolveSubjectKeyword(params));
        condition.setPage(parsePage(params));
        condition.setSize(parseInteger(params, "size"));
        return condition;
    }

    /**
     * 頁、件数正規化処理
     * @param page 頁数
     * @param size 件数
     * @return 簡易クラス
     */
    private Paging normalizePaging(Integer page, Integer size) {

        // 入力値	    → 頁数
        // null	        → DEFAULT_PAGE_INDEX
        //  0           → 0
        // 10           → 10
        // -1       	→ DEFAULT_PAGE_INDEX
        int normalizedPage = Optional.ofNullable(page).filter(p -> p >= 0).orElse(DEFAULT_PAGE_INDEX);

        // 入力値	    → 件数
        // null	        → DEFAULT_PAGE_INDEX
        // 0           	→ DEFAULT_PAGE_INDEX
        // 10           → 10
        // -1           → DEFAULT_PAGE_INDEX
        int normalizedSize = Optional.ofNullable(size).filter(s -> s != null && s > 0).orElse(DEFAULT_PAGE_SIZE);

        return new Paging(normalizedPage, normalizedSize);
    }

    /**
     * 検索結果生成処理
     * @param items      Record
     * @param totalCount 総件数
     * @param paging     簡易クラス(頁、件数)
     * @return 検索結果
     */
    private MailSearchResponseDto buildResponse(List<MailSendListDto> items, long totalCount, Paging paging) {
        int totalPages = paging.size() == 0 ? 0 : (int) Math.ceil((double) totalCount / paging.size());
        boolean hasPrevious = paging.page() > 0;
        boolean hasNext = paging.page() + 1 < totalPages;
        return new MailSearchResponseDto(
                items,
                totalCount,
                paging.page(),
                paging.size(),
                totalPages,
                hasNext,
                hasPrevious
        );
    }

    /**
     * 件名変換処理
     * @param params 入力値
     * @return 件名
     */
    private String resolveSubjectKeyword(Map<String, String> params) {
        // 件名 と 件名(KeyWord) のどちらか入力された方を優先
        String keyword = extract(params, "subjectKeyword");
        if (keyword != null) {
            return keyword;
        }
        return extract(params, "subject");
    }

    /**
     * 頁数変換処理
     * @param params 入力値
     * @return 頁数
     */
    private Integer parsePage(Map<String, String> params) {
        // 外から受け取った頁番号（1 始まり）を
        // 内部用の 0 始まりに変換し、不正な値を 0 に補正する処理
        Integer raw = parseInteger(params, "page");
        if (raw == null) {
            return null;
        }
        int zeroBased = raw - 1;
        return Math.max(zeroBased, DEFAULT_PAGE_INDEX);
    }

    /**
     * 数値変換処理
     * @param params 入力値
     * @param key    Key値
     * @return 数値
     */
    private Integer parseInteger(Map<String, String> params, String key) {
        Integer rtn = null;
        try {
            String value = extract(params, key);
            if (value != null) {
                rtn = Integer.valueOf(value);
            }
        } catch (Exception e) {
        }
        return rtn;
    }

    /**
     * 日時変換処理
     * @param params 入力値
     * @param key    Key値
     * @return 日時
     */
    private LocalDateTime parseDateTime(Map<String, String> params, String key) {
        LocalDateTime rtn = null;
        try {
            String value = extract(params, key);
            if (value != null) {
                rtn = LocalDateTime.parse(value, DATE_TIME_FORMATTER);
            }
        } catch (Exception e) {
        }
        return rtn;
    }

    /**
     * 入力値変換処理
     * @param params 入力値
     * @param key    Key値
     * @return 文字列 or Null
     */
    private String extract(Map<String, String> params, String key) {
        // 入力値        → 変換値
        // "abc"	     → "abc"
        // " abc "	     → "abc"
        // "" （空文字） → null
        // " "（空白）	 → null
        // Null	         → null
        // Key無し  	 → null
        return Optional.ofNullable(params.get(key))
                .map(String::trim)
                .filter(v -> !v.isEmpty())
                .orElse(null);
    }

    /**
     * 簡易クラス生成処理
     * @param page 頁数
     * @param size 件数
     * @return 簡易クラス
     */
    private record Paging(int page, int size) {
        // 頁番号（page）と件数（size）を1つの値オブジェクトに集約し、
        // 頁検索するときの offset を計算して返却する
        int offset() {
            return page * size;
        }
    }
}
