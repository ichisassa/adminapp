package com.example.adminapp.service.mail;

import com.example.adminapp.domain.MailLog;
import com.example.adminapp.mapper.MailSendMapper;
import com.example.adminapp.service.mail.dto.MailSendResponseDto;
import com.example.adminapp.validation.FormValidator;
import com.example.adminapp.validation.MailSendField;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * MailSendService Class
 */
@Service
public class MailSendService {

    private final FormValidator  validator;
    private final MailSendMapper mapper;

    /**
     * constructor
     */
    public MailSendService(FormValidator validator, MailSendMapper mapper) {
        this.validator = validator;
        this.mapper = mapper;
    }

    /**
     * 送信処理
     * @param params 入力値
     * @return 処理結果
     */
    public MailSendResponseDto send(Map<String, String> params) {

        // validation処理
        Map<String, String> errors = validate(params);
        if (errors == null){
            return MailSendResponseDto.failure("チェック処理に失敗しました。",
                   Collections.emptyMap(),
                   List.of("システムエラーが発生しました。"));
        }

        if (!errors.isEmpty()) {
            return MailSendResponseDto.failure("入力内容にエラーがあります。", errors, null);
        }

        // insert処理
        Integer result = insert(params);
        if (result == null){
            return MailSendResponseDto.failure("メール送信ログの登録に失敗しました。",
                   Collections.emptyMap(),
                   List.of("システムエラーが発生しました。"));
        }

        if (result != 1){
            return MailSendResponseDto.failure("メール送信ログの登録に失敗しました。",
                   Collections.emptyMap(),
                   List.of("登録件数異常です。[" + result + "]"));
        }

        return MailSendResponseDto.success("メール送信処理を受け付けました。（ダミー）");
    }

    /**
     * validation処理
     * @param params 入力値
     * @return 処理結果
     */
    public Map<String, String> validate(Map<String, String> params) {
        Map<String, String> rtn = null;
        try{
            rtn = validator.validate(MailSendField.class, params);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            System.out.println(sw.toString());
        }
        return rtn;
    }

    /**
     * insert処理
     * @param params 入力値
     * @return 処理結果
     */
    public Integer insert(Map<String, String> params) {
        Integer rtn = null;
        try{
            LocalDateTime now = LocalDateTime.now();

            MailLog maillog = new MailLog();
            maillog.setToAddress(params.get(MailSendField.TO.paramName()));
            maillog.setCcAddress(params.get(MailSendField.CC.paramName()));
            maillog.setBccAddress(params.get(MailSendField.BCC.paramName()));
            maillog.setSubject(params.get(MailSendField.SUBJECT.paramName()));
            maillog.setBody(params.get(MailSendField.BODY.paramName()));

            String isHtmlParam = params.get(MailSendField.IS_HTML.paramName());
            boolean isHtml = "true".equalsIgnoreCase(isHtmlParam);
            maillog.setIsHtml(isHtml);

            maillog.setStatus("SUCCESS");
            maillog.setErrorMessage(null);
            maillog.setSentAt(now);
            maillog.setCreatedAt(now);
            maillog.setUpdatedAt(now);
            maillog.setVersion(0);

            rtn = mapper.insert(maillog);

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            System.out.println(sw.toString());
        }

        return rtn;
    }
}
