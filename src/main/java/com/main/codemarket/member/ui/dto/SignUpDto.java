package com.main.codemarket.member.ui.dto;

import com.main.codemarket.member.domain.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignUpDto {
    @NotBlank(message = "이메일은 필수 입력값입니다")
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\\\.[a-zA-Z]{2,6}$",message = "잘못된 이메일 형식입니다")
    private String email;
    @NotBlank(message = "사용자 이름은 필수 입력값입니다")
    private String username;
    @NotBlank(message = "비밀번호는 필수 입력값입니다")
    @Size(min=8, max = 20, message = "비밀번호는 8자 ~ 20자까지 허용됩니다")
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
