package com.github.doodler.common.oauth2;

import org.apache.commons.lang3.RandomStringUtils;
import com.github.doodler.common.id.IdGenerator;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: DefaultClientCredentials
 * @Author: Fred Feng
 * @Date: 07/11/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class DefaultClientCredentials implements ClientCredentials {

    private final IdGenerator idGenerator;

    @Override
    public String getClientId() {
        return String.valueOf(idGenerator.getNextId());
    }

    @Override
    public String getClientSecret() {
        return RandomStringUtils.random(128, true, true);
    }

}
