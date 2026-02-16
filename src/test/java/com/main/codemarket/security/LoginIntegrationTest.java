package com.main.codemarket.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.codemarket.member.domain.entity.Member;
import com.main.codemarket.member.infra.repository.MemberRepository;
import com.main.codemarket.security.dto.LoginRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    private LoginRequestDto loginRequestDto;

    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void set_up() {
        loginRequestDto = new LoginRequestDto("test@example.com", "test_password");
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    @DisplayName("로그인 요청을 올바른 입력값으로 요청하면 클라이언트는 새로운 토큰 값을 리턴받는다")
    void login_success() throws Exception {
        //given
        String encryptedPassword = passwordEncoder.encode("test_password");
        member = Member.createMember("test@example.com", "test_user", encryptedPassword);
        memberRepository.save(member);

        //when
        ResultActions resultActions = mockMvc.perform(post("/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)));

        //then
        resultActions.andExpect(status().is2xxSuccessful());

        //헤더 형식이 맞다면 토큰이 발급된 것으로 간주  
        MvcResult mvcResult = resultActions.andReturn();
        String authorization = mvcResult.getResponse().getHeader("Authorization");
        assertNotNull(authorization);
        assertTrue(authorization.startsWith("Bearer "));
    }

    @Test
    @DisplayName("로그인 요청을 올바르지 않은 입력값으로 요청하면 클라이언트는 401을 리턴받는다")
    void login_fail() throws Exception {
        //given
        member = Member.createMember("test_fail@example.com", "test_user", "test_password");
        memberRepository.save(member);

        //when & then
        mockMvc.perform(post("/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto))
        ).andExpect(status().isUnauthorized());
    }
}
