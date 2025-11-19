package com.example.adminapp.mapper;

import com.example.adminapp.domain.MailLog;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MailLogMapper {

    List<MailLog> findAll(@Param("offset") int offset, @Param("limit") int limit);

    MailLog findById(@Param("id") Long id);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MailLog mailLog);

    int update(MailLog mailLog);

    int delete(@Param("id") Long id);
}

