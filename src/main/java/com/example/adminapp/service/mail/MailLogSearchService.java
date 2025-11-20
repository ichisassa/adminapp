package com.example.adminapp.service.mail;

import com.example.adminapp.service.mail.dto.MailLogSearchConditionDto;
import com.example.adminapp.service.mail.dto.MailLogSearchResponseDto;

/**
 * Declares operations for searching mail logs.
 */
public interface MailLogSearchService {

    MailLogSearchResponseDto search(MailLogSearchConditionDto condition);
}
