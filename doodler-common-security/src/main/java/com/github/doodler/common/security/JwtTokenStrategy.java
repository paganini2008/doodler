package com.github.doodler.common.security;

import static com.github.doodler.common.security.SecurityConstants.CLAIM_ADDITIONAL_INFORMATION;
import static com.github.doodler.common.security.SecurityConstants.CLAIM_AUTHORITIES;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import com.github.doodler.common.BizException;
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
        String self = (userDetails.getId() != null ? userDetails.getId() : "<None>") + "|"
                + user.getUsername() + "|" + user.getEmail() + "|" + user.getPlatform();
        return jwtProperties.getPrefix() + Jwts.builder()
                .setId(UUID.nameUUIDFromBytes(self.getBytes()).toString()).setSubject(self)
                .setAudience(user.getPlatform()).setIssuedAt(new Date())
                .setIssuer(jwtProperties.getIssuer())
                .setExpiration(Date.from(Instant.now().plus(
                        Math.max(expiration, jwtProperties.getExpiration()), ChronoUnit.SECONDS)))
                .claim(CLAIM_AUTHORITIES, new ArrayList<>(userDetails.getAuthorities()))
                .claim(CLAIM_ADDITIONAL_INFORMATION, userDetails.getAdditionalInformation())
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecretKey()).compact();
    }

    @Override
    @SuppressWarnings("unchecked")
    public IdentifiableUserDetails decode(String token) {
        String prefix = jwtProperties.getPrefix();
        if (StringUtils.isNotBlank(prefix)) {
            token = token.substring(prefix.length());
        }
        try {
            Claims claims = Jwts.parser().setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token).getBody();
            String id = claims.getId();
            String subject = claims.getSubject();
            if (!id.equals(UUID.nameUUIDFromBytes(subject.getBytes()).toString())) {
                throw new BizException(ErrorCodes.JWT_TOKEN_INVALID, HttpStatus.UNAUTHORIZED);
            }
            String[] args = subject.split("|", 4);
            Long userId = Long.valueOf(args[0]);
            String username = args[1];
            String email = args[2];
            Collection<? extends GrantedAuthority> authorities =
                    (Collection<? extends GrantedAuthority>) claims.get(CLAIM_AUTHORITIES);
            Map<String, Object> additionalInformation =
                    (Map<String, Object>) claims.get(CLAIM_ADDITIONAL_INFORMATION);
            RegularUser regularUser = new RegularUser(userId, username, email, SecurityConstants.NA,
                    claims.getAudience(), true, authorities);
            regularUser.getAdditionalInformation().putAll(additionalInformation);
            return regularUser;
        } catch (RuntimeException e) {
            throw new BizException(ErrorCodes.JWT_TOKEN_INVALID, HttpStatus.UNAUTHORIZED,
                    e.getMessage());
        }
    }

    @Override
    public boolean validate(String token) {
        String prefix = jwtProperties.getPrefix();
        if (StringUtils.isNotBlank(prefix)) {
            token = token.substring(prefix.length());
        }
        try {
            Claims claims = Jwts.parser().setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token).getBody();
            return !claims.getExpiration().before(new Date());
        } catch (RuntimeException e) {
            throw new BizException(ErrorCodes.JWT_TOKEN_INVALID, HttpStatus.UNAUTHORIZED,
                    e.getMessage());
        }
    }
}
