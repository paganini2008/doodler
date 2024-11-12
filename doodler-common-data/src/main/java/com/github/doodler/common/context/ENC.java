package com.github.doodler.common.context;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import lombok.experimental.UtilityClass;

/**
 * @Description: ENC
 * @Author: Fred Feng
 * @Date: 03/02/2023
 * @Version 1.0.0
 */
@UtilityClass
public class ENC {

    public static final String DEFAULT_ALGORITHM = "PBEWithMD5AndDES";
    private static final String DEFAULT_PASSWORD_SALT = "5h0E5GZ3DhAJTSQOFQhlxEZEgJYFjdQz";

    private EnvironmentStringPBEConfig getConfig(String salt) {
        EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
        config.setAlgorithm(DEFAULT_ALGORITHM);
        if (StringUtils.isBlank(salt)) {
            salt = DEFAULT_PASSWORD_SALT;
        }
        config.setPassword(salt);
        return config;
    }

    public String encrypt(String plainText) {
        return encrypt(plainText, null);
    }

    public String encrypt(String plainText, String salt) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        EnvironmentStringPBEConfig config = getConfig(salt);
        encryptor.setConfig(config);
        return encryptor.encrypt(plainText);
    }

    public String decrypt(String cipherText) {
        return decrypt(cipherText, null);
    }

    public String decrypt(String cipherText, String salt) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        EnvironmentStringPBEConfig config = getConfig(salt);
        encryptor.setConfig(config);
        return encryptor.decrypt(cipherText);
    }

}
