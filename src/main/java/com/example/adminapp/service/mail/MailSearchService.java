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

    private static final int DEFAULT_PAGE = 0;  // 頁初期値
    private static final int DEFAULT_SIZE = 20; // 件数初期値

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
    public MailSearchResponseDto search(Map<String, String> params) {

        MailSearchConditionDto condition = buildCondition(params);
        Integer page = condition.getPage();
        Integer size = condition.getSize();

        long totalsize = mapper.countByCondition(condition);
        int totalpage  = getTotalPage(totalsize, size);

        if (totalpage > 0 && page >= totalpage) {
            page = totalpage - 1;
            condition.setPage(page);
        }

        List<MailSendListDto> items = totalsize == 0
                ? Collections.emptyList()
                : mapper.findByCondition(condition, page * size, size);

        return buildResponse(items, totalsize, size, totalpage, page);
    }

    /**
     * 検索条件生成処理
     * @param params 入力値
     * @return 検索条件
     */
    private MailSearchConditionDto buildCondition(Map<String, String> params) {
        MailSearchConditionDto dto = new MailSearchConditionDto();
        dto.setSentAtFrom(parseDateTime(params, "sentAtFrom"));
        dto.setSentAtTo(parseDateTime(params, "sentAtTo"));
        dto.setStatus(getString(params, "status"));
        dto.setToAddress(getString(params, "toAddress"));
        dto.setSubjectKeyword(resolveSubjectKeyword(params));
        dto.setPage(getPage(params));
        dto.setSize(getSize(params));
        return dto;
    }

    /**
     * 検索結果生成処理
     * @param items      SQL実行結果
     * @param totalsize  総件数
     * @param size       件数
     * @param totalpage  総頁数
     * @param page       頁数
     * @return 検索結果
     */
    private MailSearchResponseDto buildResponse(
        List<MailSendListDto> items,
        long totalsize,
        int  size,
        int  totalpage,
        int  page)
    {
        MailSearchResponseDto rtn = new MailSearchResponseDto();
        boolean hasPrevious = page > 0;
        boolean hasNext = page + 1 < totalpage;
        rtn.setItems(items);
        rtn.setTotalCount(totalsize);
        rtn.setSize(size);
        rtn.setTotalPages(totalpage);
        rtn.setPage(page);
        rtn.setHasNext(hasNext);
        rtn.setHasPrevious(hasPrevious);
        return rtn;
    }

    /**
     * 総頁数変換処理
     * @param totalCount 総件数
     * @param size       件数
     * @return 総頁数
     */
    private int getTotalPage(long totalsize, int size) {
        if (totalsize == 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalsize / size);
    }

    /**
     * 頁数変換処理
     * @param params 入力値
     * @return 頁数
     */
    private Integer getPage(Map<String, String> params) {
        Integer page = parseInteger(params, "page");
        if (page == null) {
            return DEFAULT_PAGE;
        }
        return Math.max(page - 1, DEFAULT_PAGE);
    }

    /**
     * 件数変換処理
     * @param params 入力値
     * @return 頁数
     */
    private Integer getSize(Map<String, String> params) {
        Integer size = parseInteger(params, "size");
        if (size == null) {
            return DEFAULT_SIZE;
        }
        return Math.max(size, DEFAULT_SIZE);
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
            String value = getString(params, key);
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
            String value = getString(params, key);
            if (value != null) {
                rtn = LocalDateTime.parse(value, DATE_TIME_FORMATTER);
            }
        } catch (Exception e) {
        }
        return rtn;
    }

    /**
     * 件名変換処理
     * @param params 入力値
     * @return 件名
     */
    private String resolveSubjectKeyword(Map<String, String> params) {
        String keyword = getString(params, "subjectKeyword");
        if (keyword != null) {
            return keyword;
        }
        return getString(params, "subject");
    }

    /**
     * 入力値取得処理
     * @param params 入力値
     * @param key    Key値
     * @return 入力値
     */
    private String getString(Map<String, String> params, String key) {
        // 入力値        → 変換値
        // "abc"	     → "abc"
        // " abc "	     → "abc"
        // "" （空文字） → null
        // " "（空白）	 → null
        // Null	         → null
        // Key無し  	 → null
        return Optional.ofNullable(params.get(key))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .orElse(null);
    }
}
