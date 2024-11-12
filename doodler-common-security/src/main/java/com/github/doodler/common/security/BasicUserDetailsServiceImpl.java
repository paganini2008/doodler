package com.github.doodler.common.security;

import org.springframework.beans.BeanUtils;
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

    private static final String KEY_USER_LIST = "security:basic:users";
    protected final RedisOperations<String, Object> redisOperations;

    @Override
    public UserDetails loadBasicUserByUsername(String username) throws UsernameNotFoundException {
        BasicCredentials credentials =
                (BasicCredentials) redisOperations.opsForHash().get(KEY_USER_LIST, username);
        if (credentials == null) {
            throw new UsernameNotFoundException(username);
        }
        return new BasicUser(credentials.getUsername(), credentials.getPassword(),
                credentials.getPlatform(), credentials.getRoles());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        throw new UnsupportedOperationException("loadUserByUsername");
    }

    @Override
    public void createUser(UserDetails user) {
        if (!redisOperations.opsForHash().hasKey(KEY_USER_LIST, user.getUsername())) {
            redisOperations.opsForHash().put(KEY_USER_LIST, user.getUsername(), user);
        }
    }

    @Override
    public void updateUser(UserDetails user) {
        if (!redisOperations.opsForHash().hasKey(KEY_USER_LIST, user.getUsername())) {
            BasicCredentials newUser = new BasicCredentials();
            BeanUtils.copyProperties(user, newUser);
            redisOperations.opsForHash().put(KEY_USER_LIST, user.getUsername(), newUser);
        }
    }

    @Override
    public void deleteUser(String username) {
        redisOperations.opsForHash().delete(KEY_USER_LIST, username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        UserDetails userDetails = SecurityUtils.getCurrentUser();
        updatePassword(userDetails, newPassword);
    }

    @Override
    public boolean userExists(String username) {
        return redisOperations.opsForHash().hasKey(KEY_USER_LIST, username);
    }

    @Override
    public void cleanUsers() {
        redisOperations.delete(KEY_USER_LIST);
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        BasicCredentials userCopy = (BasicCredentials) redisOperations.opsForHash()
                .get(KEY_USER_LIST, user.getUsername());
        if (userCopy != null) {
            userCopy.setPassword(newPassword);
            redisOperations.opsForHash().put(KEY_USER_LIST, user.getUsername(), userCopy);
        }
        return userCopy;
    }
}
