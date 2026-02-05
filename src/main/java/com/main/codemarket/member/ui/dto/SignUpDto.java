package com.main.codemarket.member.ui.dto;

import com.main.codemarket.member.domain.entity.Member;

public class SignUpDto {
    private String email;
    private String username;
    private String password;

    public static Member createMemberEntity(SignUpDto signUpDto) {
        String email = signUpDto.getEmail();
        String username = signUpDto.getUsername();
        String password = signUpDto.getPassword();
        return Member.createMember(email, username, password);
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
