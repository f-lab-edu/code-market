package com.main.codemarket.member.application.service;

import com.main.codemarket.member.domain.entity.Member;
import com.main.codemarket.member.infra.repository.MemberRepository;
import com.main.codemarket.member.ui.dto.SignUpDto;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * 회원가입 기능
     * - 중복된 membername 체크
     * - 비밀번호 암호화 후 저장
     */
    @Transactional
    public void signUp(SignUpDto signUpDto) {
        validateDuplicateEmail(signUpDto.getEmail());
        Member member = createMemberEntity(signUpDto);
        memberRepository.save(member);
    }

    /**
     * 중복된 email 체크
     */
    private void validateDuplicateEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
//            log.warn("중복된 아이디 입니다: {}" , membername);
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }
    }

    /**
     * Member 엔티티 생성 (비밀번호 암호화 적용)
     */
    private Member createMemberEntity(SignUpDto signUpDto) {
        return Member.createMember(
                signUpDto.getEmail(),
                signUpDto.getUsername(),
                bCryptPasswordEncoder.encode(signUpDto.getPassword())); // 비밀번호 암호화)
    }
}
