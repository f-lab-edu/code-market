package com.main.codemarket.member.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.codemarket.member.domain.entity.Member;
import com.main.codemarket.member.ui.controller.MemberController;
import com.main.codemarket.member.ui.dto.SignUpDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("정상적인 회원 가입 입력값이 전달되면 회원가입 메서드를 호출한다")
    void signUp_success() throws Exception {
        //given
        SignUpDto signUpDto = new SignUpDto("test@example.com", "test_username", "test_password");

        //when & then
        mockMvc.perform(post("/member/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDto)))
                .andExpect(status().is2xxSuccessful());

        //실제 서비스 호출하였는지 테스트
        verify(memberService).signUp(any(Member.class));
    }


    @Test
    @DisplayName("이메일 입력값의 형식이 잘못되었을 때 유효성 검사를 통과하지 못한다")
    void validateEmail() throws Exception {
        //given
        SignUpDto signUpDto = new SignUpDto("test",
                "test_username", "test_password");

        //when & then
        mockMvc.perform(post("/member/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비밀번호 입력값의 형식이 잘못되었을 때 유효성 검사를 통과하지 못한다")
    void validatePassword() throws Exception {
        //given
        SignUpDto signUpDto = new SignUpDto("test@example.com", "test_username",
                "short");

        //when & then
        mockMvc.perform(post("/member/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("필수 입력값이 없다면 유효성 검사를 통과하지 못한다")
    void test() throws Exception {
        //given
        SignUpDto signUpDtoWithoutEmail = new SignUpDto("",
                "test_username", "test_password");

        SignUpDto signUpDtoWithoutUsername = new SignUpDto("test@example.com",
                "", "test_password");

        SignUpDto signUpDtoWithoutPassword = new SignUpDto("test@example.com",
                "test_username", "");

        //when & then
        mockMvc.perform(post("/member/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDtoWithoutEmail)))
                .andExpect(status().isBadRequest());

        //when & then
        mockMvc.perform(post("/member/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDtoWithoutUsername)))
                .andExpect(status().isBadRequest());

        //when & then
        mockMvc.perform(post("/member/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDtoWithoutPassword)))
                .andExpect(status().isBadRequest());

    }
}