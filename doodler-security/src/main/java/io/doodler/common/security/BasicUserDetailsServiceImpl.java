package io.doodler.common.security;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import lombok.RequiredArgsConstructor;

/**
 * @Description: BasicUserDetailsServiceImpl
 * @Author: Fred Feng
 * @Date: 26/11/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class BasicUserDetailsServiceImpl implements BasicUserDetailsService {

    private static final String userKey = "security:users";
    protected final RedisOperations<String, Object> redisOperations;

    @Override
    public void addBasicUser(String username, String password, String platform, String... roles) {
    	if(!redisOperations.opsForHash().hasKey(userKey, username)) {
    		redisOperations.opsForHash().put(userKey, username, new BasicCredentials(username, password, platform, roles));
    	}
    }

    @Override
    public void removeBasicUser(String username) {
    	redisOperations.opsForHash().delete(userKey, username);
    }

    @Override
    public void cleanBasicUsers() {
    	redisOperations.delete(userKey);
    }

    @Override
    public UserDetails loadBasicUserByUsername(String username) throws UsernameNotFoundException {
        BasicCredentials credentials = (BasicCredentials) redisOperations.opsForHash().get(userKey, username);
        if (credentials == null) {
            throw new UsernameNotFoundException(username);
        }
        return new BasicUser(credentials.getUsername(), credentials.getPassword(), credentials.getPlatform(),
                credentials.getRoles());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        throw new UnsupportedOperationException("loadUserByUsername");
    }
}