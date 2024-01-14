package com.github.doodler.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.github.doodler.common.Constants;
import com.github.doodler.common.utils.DecryptionUtils;

/**
 * @Description: TextFieldValidator
 * @Author: Fred Feng
 * @Date: 29/03/2020
 * @Version 1.0.0
 */
@Slf4j
public class TextFieldValidator implements ConstraintValidator<TextField, String> {

    private int min;
    private int max;
    private String regex;
    private boolean nullable;
    private boolean encrpted;
    private String securityKey;

    @Override
    public void initialize(TextField anno) {
        this.min = anno.min();
        this.max = anno.max();
        this.regex = anno.regex();
        this.nullable = anno.nullable();
        this.encrpted = anno.encrypted();
        this.securityKey =
                StringUtils.isNotBlank(anno.securityKey()) ? anno.securityKey() : Constants.DEFAULT_SERVER_SECURITY_KEY;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)) {
            return nullable;
        }
        if (encrpted) {
            try {
                value = DecryptionUtils.decryptText(value, securityKey);
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage(), e);
                }
                return false;
            }
        }
        if (value.length() < min) {
            return false;
        }
        if (max > 0 && value.length() > max) {
            return false;
        }
        if (StringUtils.isNotBlank(regex)) {
            return value.matches(regex);
        }
        return true;
    }
}