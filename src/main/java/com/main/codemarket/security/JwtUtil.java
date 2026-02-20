package com.main.codemarket.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKeyRaw;

    @Value("${jwt.token-expire-time}")
    private long expireTime;

    private SecretKey secretKey;
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_PASSWORD = "password";

    @PostConstruct
    public void init() {
        // 안전한 키 생성 (256비트 이상)
        secretKey = Keys.hmacShaKeyFor(secretKeyRaw.getBytes(StandardCharsets.UTF_8));
    }

    /*
     * 토큰 생성
     */
    public String createToken(String email) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("이메일은 토큰 생성의 필수 요소입니다");
        }
        return Jwts.builder()
                .claim("email", email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(secretKey)
                .compact();
    }

    /*
     *토큰 유효한지 검사
     */
    public void validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            throw new IllegalStateException("토큰이 만료되었습니다");
        } catch (SignatureException | MalformedJwtException e) {
            throw new IllegalArgumentException("토큰 서명/형식 오류");
        }
    }

    /*
     * 헤더로부터 토큰 추출
     */
    public String extractToken(String header) {
        if (!StringUtils.hasText(header)) {
            throw new IllegalArgumentException("Authorization 헤더가 없습니다");
        }
        String[] headers = header.split(" ");
        if (headers.length != 2 || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("올바른 Bearer 토큰 형식이 아닙니다");
        }
        return headers[1];
    }

    /*
     * 토큰으로부터 이메일 정보 추출
     */
    public String getEmail(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload().get(CLAIM_EMAIL, String.class);
    }
}