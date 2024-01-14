package com.github.doodler.common.security;

import static com.github.doodler.common.security.SecurityConstants.LOGIN_KEY;
import static com.github.doodler.common.security.SecurityConstants.TOKEN_KEY;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @Description: PlatformUserDetailsServiceImpl
 * @Author: Fred Feng
 * @Date: 21/10/2020
 * @Version 1.0.0
 */
public class PlatformUserDetailsServiceImpl extends BasicUserDetailsServiceImpl implements PlatformUserDetailsService {

	public PlatformUserDetailsServiceImpl(RedisOperations<String, Object> redisOperation) {
		super(redisOperation);
	}
	
    @Override
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        throw new UnsupportedOperationException("loadUserById");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        throw new UnsupportedOperationException("loadUserByUsername");
    }

    @Override
    public UserDetails loadUserBySocialAccount(String username, String channel) throws UsernameNotFoundException {
        throw new UnsupportedOperationException("loadUserBySocialAccount");
    }

    @Override
    public UserDetails loadUserByUsernameAndPlatform(String username, String platform) throws UsernameNotFoundException {
        String key = String.format(LOGIN_KEY, platform, username);
        if (redisOperations.hasKey(key)) {
            String currentToken = (String) redisOperations.opsForValue().get(key);
           	key = String.format(TOKEN_KEY, platform, currentToken);
            AuthenticationInfo authInfo = (AuthenticationInfo) redisOperations.opsForValue().get(key);
            if (authInfo == null) {
                throw new UsernameNotFoundException("Cannot load user '" + username + "' from platform: " + platform);
            }
            if (authInfo.hasSa()) {
                return SuperAdmin.INSTANCE;
            }
            return new RegularUser(authInfo.getId(), authInfo.getUsername(), SecurityConstants.NA, authInfo.getPlatform(),
                    true,
                    SecurityUtils.getGrantedAuthorities(authInfo.getGrantedAuthorities()));
        }
        throw new UsernameNotFoundException("Cannot load user '" + username + "' from platform: " + platform);
    }
}