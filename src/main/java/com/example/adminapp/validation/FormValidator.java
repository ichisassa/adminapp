package com.example.adminapp.validation;

import com.example.adminapp.validation.annotation.EmailList;
import com.example.adminapp.validation.annotation.MaxLength;
import com.example.adminapp.validation.annotation.Required;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * FormValidator Class
 */
@Component
public class FormValidator {

    // Email 正規表現
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * validation処理
     * @param fieldEnumClass 入力項目 enum
     * @param params 入力値
     * @return Error Message Map
     * @throws IllegalStateException
     */
    public <E extends Enum<E> & FormField> Map<String, String> validate(Class<E> fieldEnumClass, Map<String, String> params) {
        
        Map<String, String> rtn = new LinkedHashMap<>();
        E[] fields = fieldEnumClass.getEnumConstants();
        if (fields == null) {
            return rtn;
        }

        for (E field : fields) {

            Field enumField  = resolveField(fieldEnumClass, field);
            String paramName = field.paramName();
            String value     = normalize(params.get(paramName));

            Required required = enumField.getAnnotation(Required.class);
            if (required != null) {
                validateRequired(paramName, value, required.message(), rtn);
            }

            MaxLength maxLength = enumField.getAnnotation(MaxLength.class);
            if (maxLength != null) {
                validateLength(paramName, value, maxLength.value(), maxLength.message(), rtn);
            }

            EmailList emailList = enumField.getAnnotation(EmailList.class);
            if (emailList != null) {
                validateEmails(paramName, value, emailList.message(), rtn);
            }

        }

        return rtn;
    }

    /**
     * Field定数取得処理
     * @param <E>        enum型
     * @param enumClass  enum Class
     * @param constant   定数
     * @return enum定数
     * @throws IllegalStateException
     */
    private <E extends Enum<E> & FormField> Field resolveField(Class<E> enumClass, E constant) {
        // 例) Class<E> enumClass : com.example.adminapp.validation.mail.MailSendField
        // 例) E constant         : MailSendField.TO("toAddress")、MailSendField.CC("ccAddress")
        try {
            return enumClass.getField(((Enum<?>) constant).name());
        } catch (NoSuchFieldException ex) {
            throw new IllegalStateException("Enum field not found: " + constant.name(), ex);
        }
    }

    /**
     * Validation処理(必須項目)
     * @param fieldName 項目名
     * @param value     入力値
     * @param message   Error Message
     * @param errors    Error Message Map(参照値)
     */
    private void validateRequired(String fieldName, String value, String message, Map<String, String> errors) {
        if (value.isEmpty() && !errors.containsKey(fieldName)) {
            errors.put(fieldName, message);
        }
    }

    /**
     * Validation処理(文字列長)
     * @param fieldName 項目名
     * @param value     入力値
     * @param maxLength Max Length
     * @param message   Error Message
     * @param errors    Error Message Map(参照値)
     */
    private void validateLength(String fieldName, String value, int maxLength, String message, Map<String, String> errors) {
        if (!value.isEmpty() && value.length() > maxLength && 
            !errors.containsKey(fieldName)) {
            errors.put(fieldName, message);
        }
    }

    /**
     * Validation処理(Email)
     * @param fieldName 項目名
     * @param value     入力値
     * @param message   Error Message
     * @param errors    Error Message Map(参照値)
     */
    private void validateEmails(String fieldName, String value, String message, Map<String, String> errors) {

        if (value.isEmpty() || errors.containsKey(fieldName)) {
            return;
        }

        String[] candidates = value.split(",");
        for (String candidate : candidates) {
            String email = candidate.trim();
            if (email.isEmpty() || !EMAIL_PATTERN.matcher(email).matches()) {
                errors.put(fieldName, message);
                return;
            }
        }
    }

    /**
     * 正規化処理
     * @param params 入力値
     * @return 入力値
     */
    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
