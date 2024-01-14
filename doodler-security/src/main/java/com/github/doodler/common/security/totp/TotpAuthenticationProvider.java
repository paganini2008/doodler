package com.github.doodler.common.security.totp;

import com.github.doodler.common.BizException;
import com.github.doodler.common.security.ErrorCodes;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Description: TotpAuthenticationProvider
 * @Author: Fred Feng
 * @Date: 04/10/2020
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class TotpAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        TotpAuthenticationToken authToken = (TotpAuthenticationToken) authentication;
        String email = (String) authToken.getPrincipal();
        String password = (String) authToken.getCredentials();
        String code = authToken.getCode();

        TotpRegularUser user = (TotpRegularUser) userDetailsService.loadUserByUsername(email);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BizException(ErrorCodes.BAD_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        }
        String rightCode = TotpUtils.getTotpCode(user.getSecurityKey());
        if (!rightCode.equals(code)) {
            throw new BizException(ErrorCodes.TOTP_CODE_MISMATCHED, HttpStatus.UNAUTHORIZED);
        }
        return new TotpAuthenticationToken(user, user.getPassword(), code, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return TotpAuthenticationToken.class.isAssignableFrom(authentication);
    }
}