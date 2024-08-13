package com.sj.Petory.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtils {
    private static final String TOKEN_TYPE = "token_type";
    @Value("${spring.jwt.expiredAt.ATK}")
    private String expiredAt_ATK;

    @Value("${spring.jwt.expiredAt.RTK}")
    private String expiredAt_RTK;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    public String generateToken(final String email, final String tokenType) {

        Claims claims = Jwts.claims().setSubject(email);
        claims.put(TOKEN_TYPE, tokenType);

        Date now = new Date();
        Date expiredDate = setExpired(tokenType, now);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    private Date setExpired(String tokenType, Date now) {
        if (tokenType.equals("ATK")) {
            return new Date(now.getTime() + Long.parseLong(expiredAt_ATK));
        }

        if (tokenType.equals("RTK")) {
            return new Date(now.getTime() + Long.parseLong(expiredAt_RTK));
        }
        return null;
    }
}
