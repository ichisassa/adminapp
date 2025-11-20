package com.example.adminapp.mapper;

import com.example.adminapp.service.mail.dto.MailLogSearchConditionDto;
import com.example.adminapp.service.mail.dto.MailLogSummaryDto;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MailLogSearchMapper {

    long countByCondition(@Param("condition") MailLogSearchConditionDto condition);

    List<MailLogSummaryDto> findByCondition(
            @Param("condition") MailLogSearchConditionDto condition,
            @Param("offset") int offset,
            @Param("limit") int limit);
}
