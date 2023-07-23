package io.doodler.security;

import lombok.RequiredArgsConstructor;

import static io.doodler.security.SecurityConstants.LOGIN_KEY;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @Description: PlatformUserDetailsServiceImpl
 * @Author: Fred Feng
 * @Date: 21/12/2022
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class PlatformUserDetailsServiceImpl implements PlatformUserDetailsService {

    private final RedisOperations<String, Object> redisOperations;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        throw new UnsupportedOperationException("loadUserByUsername");
    }
    
    @Override
    public UserDetails loadUserBySocialAccount(String username, String channel) throws UsernameNotFoundException{
    	throw new UnsupportedOperationException("loadUserBySocialAccount");
    }

    @Override
    public UserDetails loadUserByUsernameAndPlatform(String username, String platform) throws UsernameNotFoundException {
        String key = String.format(LOGIN_KEY, platform, username);
        if (redisOperations.hasKey(key)) {
            String currentToken = (String) redisOperations.opsForValue().get(key);
            AuthenticationInfo authInfo = (AuthenticationInfo) redisOperations.opsForValue().get(currentToken);
            if (authInfo == null) {
                throw new UsernameNotFoundException("Cannot load user '" + username + "' from platform: " + platform);
            }
            if (authInfo.hasSa()) {
            	return SuperAdmin.INSTANCE;
            }
            return new RegularUser(authInfo.getId(), authInfo.getUsername(), SecurityConstants.NA, authInfo.getPlatform(), true,
                    SecurityUtils.getGrantedAuthorities(authInfo.getGrantedAuthorities()));
        }
        throw new UsernameNotFoundException("Cannot load user '" + username + "' from platform: " + platform);
    }
}