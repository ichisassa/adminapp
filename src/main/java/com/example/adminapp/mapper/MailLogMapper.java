package com.example.adminapp.mapper;

import com.example.adminapp.domain.MailLog;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * メール送信ログの CRUD 操作を提供する MyBatis マッパー。
 */
@Mapper
public interface MailLogMapper {

    /**
     * メールログをページングして全件取得する。
     *
     * @param offset 取得開始位置
     * @param limit 取得件数
     * @return メールログ一覧
     */
    List<MailLog> findAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 主キーでメールログを検索する。
     *
     * @param id メールログ ID
     * @return 見つかったメールログ、存在しない場合は null
     */
    MailLog findById(@Param("id") Long id);

    /**
     * メールログを新規登録する。
     *
     * @param mailLog 登録対象
     * @return 追加件数
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MailLog mailLog);

    /**
     * メールログを更新する。
     *
     * @param mailLog 更新内容
     * @return 更新件数
     */
    int update(MailLog mailLog);

    /**
     * メールログを削除する。
     *
     * @param id 削除する ID
     * @return 削除件数
     */
    int delete(@Param("id") Long id);
}
