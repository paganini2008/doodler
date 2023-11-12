package io.doodler.common.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import io.doodler.common.BizException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

/**
 * @Description: JwtTokenStrategy
 * @Author: Fred Feng
 * @Date: 03/03/2023
 * @Version 1.0.0
 */
@RequiredArgsConstructor
public class JwtTokenStrategy implements TokenStrategy {

    private final JwtProperties jwtProperties;

    @Override
    public String encode(IdentifiableUserDetails userDetails, long expiration) {
        final IdentifiableUserDetails user = (IdentifiableUserDetails) userDetails;
        return jwtProperties.getPrefix() + Jwts.builder().setId(String.valueOf(user.getId()))
                .setSubject(user.getUsername())
                .setAudience(user.getPlatform())
                .setIssuedAt(new Date())
                .setIssuer(jwtProperties.getIssuer())
                .setExpiration(Date.from(
                        Instant.now().plus(Math.max(expiration, jwtProperties.getExpiration()), ChronoUnit.SECONDS)))
                .addClaims(userDetails.getAttributes())
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecretKey()).compact();
    }

    @Override
    public IdentifiableUserDetails decode(String token) {
        String prefix = jwtProperties.getPrefix();
        if (StringUtils.isNotBlank(prefix)) {
            token = token.substring(prefix.length());
        }
        try {
            Claims claims = Jwts.parser().setSigningKey(jwtProperties.getSecretKey()).parseClaimsJws(token).getBody();
            return new RegularUser(Long.valueOf(claims.getId()), claims.getSubject(), SecurityConstants.NA, claims.getAudience());
        } catch (RuntimeException e) {
            throw new BizException(ErrorCodes.JWT_TOKEN_EXPIRATION, HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public boolean validate(String token) {
        String prefix = jwtProperties.getPrefix();
        if (StringUtils.isNotBlank(prefix)) {
            token = token.substring(prefix.length());
        }
        try {
            Claims claims = Jwts.parser().setSigningKey(jwtProperties.getSecretKey()).parseClaimsJws(token).getBody();
            return !claims.getExpiration().before(new Date());
        } catch (RuntimeException e) {
            throw new BizException(ErrorCodes.JWT_TOKEN_EXPIRATION, HttpStatus.UNAUTHORIZED);
        }
    }
}