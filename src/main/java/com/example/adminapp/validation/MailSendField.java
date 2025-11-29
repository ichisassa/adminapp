package com.example.adminapp.validation;

import com.example.adminapp.validation.annotation.EmailList;
import com.example.adminapp.validation.annotation.MaxLength;
import com.example.adminapp.validation.annotation.Required;

/**
 * MailSendField Enum
 */
public enum MailSendField implements FormField {

    @Required(message = "宛先(To)は必須です")
    @MaxLength(value = 1000, message = "宛先(To)は1000文字以内で入力してください")
    @EmailList(message = "宛先(To)の形式が不正です")
    TO("toAddress"),

    @Required(message = "返信先は必須です")
    @MaxLength(value = 1000, message = "返信先は1000文字以内で入力してください")
    @EmailList(message = "返信先の形式が不正です")
    REPLYTO("replyTo"),

    @MaxLength(value = 1000, message = "CC は1000文字以内で入力してください")
    @EmailList(message = "CC の形式が不正です")
    CC("ccAddress"),

    @MaxLength(value = 1000, message = "BCC は1000文字以内で入力してください")
    @EmailList(message = "BCC の形式が不正です")
    BCC("bccAddress"),

    @Required(message = "件名は必須です")
    @MaxLength(value = 255, message = "件名は255文字以内で入力してください")
    SUBJECT("subject"),

    @Required(message = "本文は必須です")
    @MaxLength(value = 4000, message = "本文は4000文字以内で入力してください")
    BODY("body"),

    IS_HTML("isHtml");

    private final String paramName;

    MailSendField(String paramName) {
        this.paramName = paramName;
    }

    @Override
    public String paramName() {
        return paramName;
    }
}
