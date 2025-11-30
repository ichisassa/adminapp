package com.example.adminapp.mapper;

import com.example.adminapp.domain.MailLog;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper

/**
 * MailSendMapper Interface
 */
public interface MailSendMapper {

    /**
     * SQL実行処理(検索)
     * @param id ID
     * @return 検索結果
     */
    List<MailLog> findAll(
            @Param("limit") int limit,
            @Param("offset") int offset);

    /**
     * SQL実行処理(検索)
     * @param id ID
     * @return 検索結果
     */
    MailLog findById(@Param("id") Long id);

    /**
     * SQL実行処理(登録)
     * @param id ID
     * @return 処理結果
     */
    int insert(MailLog mailLog);

    /**
     * SQL実行処理(更新)
     * @param id ID
     * @return 処理結果
     */
    int update(MailLog mailLog);

    /**
     * SQL実行処理(削除)
     * @param id ID
     * @return 処理結果
     */
    int delete(@Param("id") Long id);
}
