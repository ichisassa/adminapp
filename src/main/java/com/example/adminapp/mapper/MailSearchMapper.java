package com.example.adminapp.mapper;

import com.example.adminapp.service.mail.dto.MailSearchConditionDto;
import com.example.adminapp.service.mail.dto.MailSendListDto;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * MailSearchMapper Interface
 */
@Mapper
public interface MailSearchMapper {

    /**
     * SQL実行処理(件数)
     * @param condition 検索条件
     * @param offset    取得開始位置
     * @param limit     取得件数
     * @return 検索結果
     */
    long countByCondition(@Param("condition") MailSearchConditionDto condition);

    /**
     * SQL実行処理(検索)
     * @param condition 検索条件
     * @param offset    取得開始位置
     * @param limit     取得件数
     * @return 検索結果
     */
    List<MailSendListDto> findByCondition(
            @Param("condition") MailSearchConditionDto condition,
            @Param("offset") int offset,
            @Param("limit") int limit);
}
