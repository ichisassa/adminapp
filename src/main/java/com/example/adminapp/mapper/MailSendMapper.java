package com.example.adminapp.mapper;

import com.example.adminapp.domain.MailLog;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MailSendMapper {

    List<MailLog> findAll(
            @Param("limit") int limit,
            @Param("offset") int offset);

    MailLog findById(@Param("id") Long id);

    int insert(MailLog mailLog);

    int update(MailLog mailLog);

    int delete(@Param("id") Long id);
}
