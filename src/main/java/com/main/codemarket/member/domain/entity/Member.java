package com.main.codemarket.member.domain.entity;

import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    private String email;
    private String username;
    private String password;

    public Member() {
    }

    public static Member createMember(String email, String username, String password) {
        // 도메인 검증 로직
        validateEmail(email);
        validatePassword(password);
        Member member = new Member();
        member.email = email;
        member.username = username;
        member.password = encode(password);
        return member;
    }

    private static String encode(String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(password);
    }


    /**
     * 이메일 형식 체크
     */
    private static void validateEmail(String email) {
        if (!email.contains("@")) throw new IllegalArgumentException("잘못된 이메일 형식입니다.");
    }

    /**
     * 최소/최대 길이 (8~20자)
     */
    private static void validatePassword(String password) {
        if (password.length() < 8 || password.length() > 20)
            throw new IllegalArgumentException("비밀번호의 길이는 8 ~ 20자여야합니다.");
    }


    public Long getId() {
        return id;
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
