package com.main.codemarket.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequestDto {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 20, message = "비밀번호는 8자 ~ 20자까지 허용됩니다")
    private String password;

    public LoginRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
