package com.main.codemarket.member.application.service;

import com.main.codemarket.member.domain.entity.Member;
import com.main.codemarket.member.infra.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("local")
class MemberServiceImplTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;


    @Test
    @DisplayName("입력값이 유효한 회원 객체는 DB에 정상적으로 저장된다")
    void signUp() {
        //given
        Member member = Member.createMember("test@naver.com", "test_user", "encryptedPassword");

        //when
        Member savedMember = memberService.signUp(member);
        Member foundMember = memberRepository.findById(savedMember.getId()).get();

        //then
        assertEquals(savedMember.getId(), foundMember.getId());
    }


    @Test
    @DisplayName("동일한 이메일로 가입한 데이터가 있다면 회원 가입 시에 예외를 발생시킨다")
    void validateDuplicateEmail() {
        //given
        Member member = Member.createMember("dbsalszz@naver.com", "test_user", "encryptedPassword");
        memberRepository.save(member);

        Member sameEmailMember = Member.createMember("dbsalszz@naver.com", "another_test_user", "another_Password");
        //then
        assertThrows(IllegalArgumentException.class, () -> {
            memberService.signUp(sameEmailMember);
        });

    }


}