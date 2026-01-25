package com.main.codemarket.member.domain.entity;

import jakarta.persistence.*;

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

    public static Member createMember(String email, String password) {
        Member member = new Member();
        member.email = email;
        member.password = password;
        // 도메인 검증 로직
        validateEmail(email);
        return member;
    }

    public static Member createMember(String email, String username, String password) {
        Member member = new Member();
        member.email = email;
        member.username = username;
        member.password = password;
        // 도메인 검증 로직
        validateEmail(email);
        return member;
    }

    private static void validateEmail(String email) {
        // TODO : 예외 처리 방법에 대해서는 정의가 필요
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
}
