package com.main.codemarket.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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
    @DisplayName("유효하지않은 이메일 값으로 토큰 생성 전에 예외를 던진다")
    void createToken_failure() {
        //given
        String email = "";

        //when & then
        assertThrows(IllegalArgumentException.class, () -> jwtUtil.createToken(email));
    }

    @Test
    @DisplayName("토큰이 유효하면 예외를 던지지 않는다")
    void validateToken_success() {
        //given
        String email = "test@example.com";
        String token = jwtUtil.createToken(email);

        //when & then
        assertDoesNotThrow(() -> jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("토큰이 만료되었으면 만료 예외를 던진다")
    void validateToken_expire() throws InterruptedException {
        //given
        String email = "test@example.com";
        String token = jwtUtil.createToken(email);
        Thread.sleep(2000L); //토큰 유효기간을 1초로 했기에 스레드를 2초간 잠들게 해 만료시킨다

        //when & then
        assertThrows(IllegalStateException.class, () -> jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("토큰의 서명/형식이 잘못되었으면 토큰 서명/형식 예외를 던진다")
    void validateToken_form() {
        //given
        String email = "test@example.com";
        String token = jwtUtil.createToken(email);

        //when & then
        assertThrows(IllegalStateException.class, () -> jwtUtil.validateToken(token));
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