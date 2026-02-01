package com.main.codemarket.member.domain.entity;

import com.main.codemarket.member.domain.PasswordEncoder;
import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
public class Member implements PasswordEncoder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    private String email;
    private String username;
    private String password;

    public Member() {
    }

    public static Member createMember(String email, String password) {
        // 도메인 검증 로직
        validateEmail(email);
        Member member = new Member();
        member.email = email;
        member.password = password;
        return member;
    }

    public static Member createMember(String email, String username, String password) {
        // 도메인 검증 로직
        validateEmail(email);
        Member member = new Member();
        member.email = email;
        member.username = username;
        //비밀번호 암호화
        member.password = password;
        return member;
    }

    private static void validateEmail(String email) {
        if (!email.contains("@")) throw new IllegalArgumentException("잘못된 이메일");
    }


    public Long getMemberId() {
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

    @Override
    public String encode(String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(password);
    }
}
