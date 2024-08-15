package com.sj.Petory.security;

import com.sj.Petory.domain.member.dto.MemberAdapter;
import com.sj.Petory.domain.member.service.UserDetailsServiceImpl;
import com.sj.Petory.exception.MemberException;
import com.sj.Petory.exception.type.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtils {
    private static final String TOKEN_TYPE = "token_type";

    private final UserDetailsServiceImpl userDetailsService;

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

    public String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            return null;
        }

        return header.substring("Bearer ".length());
    }

    public boolean validateToken(String token) {

        Date exp = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();

        if (exp.before(new Date())) {//날짜 개념 학습
            throw new MemberException(ErrorCode.TOKEN_EXPIRED);
        }
        return true;
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(
                parseEmail(token));

        return new UsernamePasswordAuthenticationToken(
                userDetails
                , ""
                , userDetails.getAuthorities()
        );
    }
    private String parseEmail(String token) {
        return parseClaims(token).getSubject();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token).getBody();
    }
}
