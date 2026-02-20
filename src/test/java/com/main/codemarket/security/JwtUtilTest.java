package com.main.codemarket.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {
    @InjectMocks
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        // @Value 값들 명시적으로 세팅
        ReflectionTestUtils.setField(jwtUtil, "secretKeyRaw",
                "mySecretKey1234567890123456789012345678901234567890");
        ReflectionTestUtils.setField(jwtUtil, "expireTime", 1000L);

        // 키 생성을 위해 수동 호출
        jwtUtil.init();
    }

    @Test
    @DisplayName("정상적인 이메일 값으로 토큰 생성에 성공한다")
    void createToken_success() {
        //given
        String email = "test@example.com";

        //when
        String token = jwtUtil.createToken(email);

        //then
        assertNotNull(token);
        assertEquals(email, jwtUtil.getEmail(token));
    }

    @Test
    @DisplayName("유효하지않은 이메일 값으로 토큰 생성 시도하면 예외를 던진다")
    void createToken_failure() {
        //given
        String email = "";

        //when & then
        assertThrows(IllegalArgumentException.class, () -> jwtUtil.createToken(email));
    }

    @Test
    @DisplayName("토큰이 유효하면 토큰 생성시 예외를 던지지 않는다")
    void validateToken_success() {
        //given
        String email = "test@example.com";
        String token = jwtUtil.createToken(email);

        //when & then
        assertDoesNotThrow(() -> jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("토큰이 만료되었으면 만료 예외를 던진다")
    void validateToken_expire() {
        //given
        String email = "test@example.com";
        String token = jwtUtil.createToken(email);
        //토큰 유효기간이 만료되었는지 확인해보기 위해 최대 2초간, 100ms단위로 폴링하며 예외를 던지는지 확인한다
        await().atMost(2, TimeUnit.SECONDS) //최대
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    //when & then
                    assertThrows(IllegalStateException.class, () -> jwtUtil.validateToken(token));
                });
    }

    @Test
    @DisplayName("토큰의 서명/형식이 잘못되었으면 토큰 서명/형식 예외를 던진다")
    void validateToken_form() {
        //given
        String email = "test@example.com";
        SecretKey otherKey = Keys.hmacShaKeyFor("wrong-key-12345678901234567890123456789012".getBytes());
        String token = Jwts.builder()
                .claim("email", email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000L))
                .signWith(otherKey)
                .compact();
        String malformToken = "eyJhbGciOiJIUzI1NiJ9.invalid.payload.signature";

        //when & then
        assertThrows(IllegalArgumentException.class, () -> jwtUtil.validateToken(token));
        assertThrows(IllegalArgumentException.class, () -> jwtUtil.validateToken(malformToken));
    }

    @Test
    @DisplayName("헤더 값이 유효하면 헤더로부터 토큰 정보를 추출한다")
    void extractToken_success() {
        //given
        String email = "test@example.com";
        String token = jwtUtil.createToken(email);
        String header = "Bearer " + token;

        //when
        String extractedToken = jwtUtil.extractToken(header);

        //then
        assertNotNull(extractedToken); //파싱한 값이 잘 들어가있는지
        assertFalse(extractedToken.contains("Bearer ")); //Bearer 뒤를 파싱한게 맞는지
    }

    @Test
    @DisplayName("헤더 값이 유효하지 않으면 예외를 던진다")
    void extractToken_valid_header() {
        //given
        String email = "test@example.com";
        String token = jwtUtil.createToken(email);

        //when & then
        assertThrows(IllegalArgumentException.class,
                () -> jwtUtil.extractToken("")); //헤더 없는 경우
        assertThrows(IllegalArgumentException.class,
                () -> jwtUtil.extractToken("Bear " + token)); //Bearer 형식이 아닌 경우
        assertThrows(IllegalArgumentException.class,
                () -> jwtUtil.extractToken(token)); //Bearer가 없는 경우
    }
}