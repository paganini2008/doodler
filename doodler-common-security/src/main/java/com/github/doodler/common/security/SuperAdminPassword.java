package com.github.doodler.common.security;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Description: SuperAdminPassword
 * @Author: Fred Feng
 * @Date: 12/12/2022
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class SuperAdminPassword {

    private final SecurityClientProperties securityClientProperties;
    private final PasswordEncoder passwordEncoder;
    private String encodedSaPassword;

    public boolean matches(String password) {
        return passwordEncoder.matches(password, encodedSaPassword);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void setSaPassword() {
        String rawPassword = securityClientProperties.getSaPassword();
        if (StringUtils.isBlank(rawPassword)) {
            rawPassword = UUID.randomUUID().toString();
            log.info("Sa Password: {}", rawPassword);
        }
        this.encodedSaPassword = passwordEncoder.encode(rawPassword);
    }
}