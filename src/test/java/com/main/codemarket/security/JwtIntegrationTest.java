package com.main.codemarket.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class JwtIntegrationTest {

    @Test
    @DisplayName("유효한 토큰으로 검증이 필요한 API에 접근 성공")
    void valid_token_access() {
        //given
        //when
        //then
    }

    @Test
    @DisplayName("유효한 토큰으로 검증이 필요한 API에 접근 실패")
    void invalid_token_access() {
        //given
        //when
        //then
    }

    @Test
    @DisplayName("만료된 토큰으로 접근 시 재발급 요청 메세지 리턴")
    void expired_token_access() {
        //given
        //when
        //then
    }
}
