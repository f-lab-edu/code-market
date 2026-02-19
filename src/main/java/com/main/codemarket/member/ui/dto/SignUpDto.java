package com.main.codemarket.member.ui.dto;

import com.main.codemarket.member.domain.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SignUpDto {
    @NotNull(message = "이메일은 필수 입력값입니다")
    @Email(message = "잘못된 이메일 형식입니다")
    @Size(max = 255, message = "이메일 길이는 255자 이하여야 합니다")
    private String email;

    @NotNull(message = "사용자 이름은 필수 입력값입니다")
    @Size(max = 50, message = "사용자 이름은 50자 이하여야 합니다")
    private String username;

    @NotNull(message = "비밀번호는 필수 입력값입니다")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 ~ 20자까지 허용됩니다")
    private String password;

    public SignUpDto() {
    }

    public SignUpDto(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public static Member createMemberEntity(SignUpDto signUpDto, String encodedPassword) {
        String email = signUpDto.getEmail();
        String username = signUpDto.getUsername();
        return Member.createMember(email, username, encodedPassword);
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
