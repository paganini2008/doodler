package com.github.doodler.common.webmvc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import com.github.doodler.common.BizException;
import com.github.doodler.common.SimpleErrorCode;
import com.github.doodler.common.context.ConditionalOnApplication;
import com.github.doodler.common.utils.DecryptionUtils;

/**
 * @Description: PasswordDecryptor
 * @Author: Fred Feng
 * @Date: 07/02/2023
 * @Version 1.0.0
 */
@Profile({"dev", "test", "prod"})
@ConditionalOnApplication(applicationNames = {"crypto-upms-service", "crypto-user-service"})
@Component
public class PasswordDecryptor {

    private static final SimpleErrorCode PASSWORD_DECRYPTION_FAILURE = new SimpleErrorCode("PASSWORD_DECRYPTION_FAILURE",
            1001100,
            "Password can't be decrypted");

    @Value("${server.securityKey}")
    private String securityKey;

    public String decryptPassword(String password) {
        try {
            return DecryptionUtils.decryptText(password, securityKey);
        } catch (Exception e) {
            throw new BizException(PASSWORD_DECRYPTION_FAILURE, HttpStatus.BAD_REQUEST);
        }
    }
}