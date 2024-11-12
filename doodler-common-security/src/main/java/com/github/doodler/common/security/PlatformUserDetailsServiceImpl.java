package com.github.doodler.common.security;

import static com.github.doodler.common.security.SecurityConstants.LOGIN_KEY;
import static com.github.doodler.common.security.SecurityConstants.TOKEN_KEY;
import org.apache.commons.collections4.MapUtils;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @Description: PlatformUserDetailsServiceImpl
 * @Author: Fred Feng
 * @Date: 21/12/2022
 * @Version 1.0.0
 */
public class PlatformUserDetailsServiceImpl extends BasicUserDetailsServiceImpl
        implements PlatformUserDetailsService {

    public PlatformUserDetailsServiceImpl(RedisOperations<String, Object> redisOperation) {
        super(redisOperation);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadUserByIdentity(username);
    }

    @Override
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        throw new UnsupportedOperationException("loadUserById");
    }

    @Override
    public UserDetails loadUserByIdentity(String identity) throws UsernameNotFoundException {
        throw new UnsupportedOperationException("loadUserByIdentity");
    }

    @Override
    public UserDetails loadUserByIdentityAndRegistrationId(String identity, String registrationId)
            throws UsernameNotFoundException {
        throw new UnsupportedOperationException("loadUserByRegistrationId");
    }

    @Override
    public UserDetails loadUserByIdentityAndPlatform(String indentity, String platform)
            throws UsernameNotFoundException {
        String key = String.format(LOGIN_KEY, platform, indentity);
        if (redisOperations.hasKey(key)) {
            String currentToken = (String) redisOperations.opsForValue().get(key);
            key = String.format(TOKEN_KEY, platform, currentToken);
            AuthenticationInfo authInfo =
                    (AuthenticationInfo) redisOperations.opsForValue().get(key);
            if (authInfo == null) {
                throw new UsernameNotFoundException(
                        "Cannot load user '" + indentity + "' from platform: " + platform);
            }
            if (authInfo.hasSa()) {
                return SuperAdmin.INSTANCE;
            }
            RegularUser regularUser = new RegularUser(authInfo.getId(), authInfo.getUsername(),
                    SecurityConstants.NA, authInfo.getEmail(), authInfo.getPlatform(), true,
                    SecurityUtils.getGrantedAuthorities(authInfo.getGrantedAuthorities()));
            if (MapUtils.isNotEmpty(authInfo.getAdditionalInformation())) {
                regularUser.getAdditionalInformation().putAll(authInfo.getAdditionalInformation());
            }
            return regularUser;
        }
        throw new UsernameNotFoundException(
                "Cannot load user '" + indentity + "' from platform: " + platform);
    }
}
