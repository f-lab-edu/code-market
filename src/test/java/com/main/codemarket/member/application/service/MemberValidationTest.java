package com.main.codemarket.member.application.service;

import com.main.codemarket.member.domain.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MemberValidationTest {

    @Test
    @DisplayName("이메일 입력값의 형식이 잘못되었을 때 유효성 검사를 통과하지 못한다")
    void validateEmail() {
        //given
        String email = "dbsalszz";
        String username = "test_user";
        String password = "encryptedPassword";

        //then
        assertThrows(IllegalArgumentException.class, () -> {
            Member.createMember(email, username, password);
        });
    }

    @Test
    @DisplayName("비밀번호 입력값의 형식이 잘못되었을 때 유효성 검사를 통과하지 못한다")
    void validatePassword() {
        //given
        String email = "dbsalszz@naver.com";
        String username = "test_user";
        String password = "1234";

        //then
        assertThrows(IllegalArgumentException.class, () -> {
            Member.createMember(email, username, password);
        });
    }
}
