package com.main.codemarket.member.application.service;

import com.main.codemarket.member.domain.entity.Member;
import com.main.codemarket.member.infra.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * 회원가입 기능
     * - 중복된 email 체크
     */
    @Override
    @Transactional
    public Member signUp(Member member) {
        validateDuplicateEmail(member.getEmail());
        return memberRepository.save(member);
    }

    /**
     * 중복된 email 체크
     */
    private void validateDuplicateEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }
    }
}
