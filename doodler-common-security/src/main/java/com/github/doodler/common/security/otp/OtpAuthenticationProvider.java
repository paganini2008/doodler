package com.github.doodler.common.security.otp;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.github.doodler.common.BizException;
import com.github.doodler.common.security.ErrorCodes;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @Description: OtpAuthenticationProvider
 * @Author: Fred Feng
 * @Date: 23/10/2024
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class OtpAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        OtpAuthenticationToken authToken = (OtpAuthenticationToken) authentication;
        String identity = (String) authToken.getPrincipal();
        String password = (String) authToken.getCredentials();
        String code = authToken.getCode();
        UserDetails userDetails = userDetailsService.loadUserByUsername(identity);
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BizException(ErrorCodes.BAD_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        }
        String rightCode = OtpUtils.getTotpCode(((OtpRegularUser) userDetails).getSecurityKey());
        if (!rightCode.equals(code)) {
            throw new BizException(ErrorCodes.TOTP_CODE_MISMATCHED, HttpStatus.UNAUTHORIZED);
        }
        return new OtpAuthenticationToken(userDetails, userDetails.getPassword(), code,
                userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OtpAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
