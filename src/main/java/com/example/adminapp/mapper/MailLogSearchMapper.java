package com.example.adminapp.mapper;

import com.example.adminapp.service.mail.dto.MailLogSearchConditionDto;
import com.example.adminapp.service.mail.dto.MailLogSummaryDto;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * メールログの検索用クエリをまとめた MyBatis マッパー。
 */
@Mapper
public interface MailLogSearchMapper {

    /**
     * 条件に一致するメール送信ログの総件数を取得する。
     *
     * @param condition フィルタ条件
     * @return 総件数
     */
    long countByCondition(@Param("condition") MailLogSearchConditionDto condition);

    /**
     * 条件とページング指定に一致するメール送信ログのサマリ一覧を取得する。
     *
     * @param condition 検索条件
     * @param offset    取得開始位置
     * @param limit     取得件数
     * @return 該当するサマリ一覧
     */
    List<MailLogSummaryDto> findByCondition(
            @Param("condition") MailLogSearchConditionDto condition,
            @Param("offset") int offset,
            @Param("limit") int limit);
}
