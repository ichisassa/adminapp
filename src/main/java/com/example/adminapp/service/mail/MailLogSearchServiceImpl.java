package com.example.adminapp.service.mail;

import com.example.adminapp.mapper.MailLogSearchMapper;
import com.example.adminapp.service.mail.dto.MailLogSearchConditionDto;
import com.example.adminapp.service.mail.dto.MailLogSearchResponseDto;
import com.example.adminapp.service.mail.dto.MailLogSummaryDto;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * メール送信ログ検索機能の実装クラス。
 * MyBatis マッパーを用いて条件検索とページング処理を行う。
 */
@Service
public class MailLogSearchServiceImpl implements MailLogSearchService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    private final MailLogSearchMapper mailLogSearchMapper;

    /** 検索用マッパーを注入しサービスを初期化するコンストラクタ。 */
    public MailLogSearchServiceImpl(MailLogSearchMapper mailLogSearchMapper) {
        this.mailLogSearchMapper = mailLogSearchMapper;
    }

    /**
     * メール送信ログを条件付きで検索し、ページング情報付きの結果を返す。
     * 無効なページ／サイズを補正し、該当件数が 0 件の場合は空リストを返却する。
     * @param condition 検索条件 DTO（null 可）
     * @return          ページング済みの検索結果
     */
    @Override
    public MailLogSearchResponseDto search(MailLogSearchConditionDto condition) {
        MailLogSearchConditionDto effectiveCondition =
                condition != null ? condition : new MailLogSearchConditionDto();

        int page = effectiveCondition.getPage() != null ? effectiveCondition.getPage() : DEFAULT_PAGE;
        if (page < 0) {
            page = DEFAULT_PAGE;
        }

        int size = effectiveCondition.getSize() != null ? effectiveCondition.getSize() : DEFAULT_SIZE;
        if (size <= 0) {
            size = DEFAULT_SIZE;
        }

        effectiveCondition.setPage(page);
        effectiveCondition.setSize(size);

        int offset = page * size;
        long totalCount = mailLogSearchMapper.countByCondition(effectiveCondition);
        List<MailLogSummaryDto> items = totalCount == 0
                ? Collections.emptyList()
                : mailLogSearchMapper.findByCondition(effectiveCondition, offset, size);

        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) totalCount / size);
        boolean hasPrevious = page > 0;
        boolean hasNext = page + 1 < totalPages;

        return new MailLogSearchResponseDto(items, totalCount, page, size, totalPages, hasNext, hasPrevious);
    }
}
