package io.doodler.security;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * @Description: SocialAccountAuthenticationToken
 * @Author: Fred Feng
 * @Date: 21/02/2023
 * @Version 1.0.0
 */
public abstract class SocialAccountAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 1L;

    protected SocialAccountAuthenticationToken(String channel, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.channel = channel;
    }

    private final String channel;

    public String getChannel() {
        return channel;
    }
}