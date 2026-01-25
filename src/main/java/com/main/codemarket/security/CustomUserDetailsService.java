package com.main.codemarket.security;


import com.main.codemarket.member.domain.entity.Member;
import com.main.codemarket.member.infra.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * username을 이용해 사용자 정보를 조회
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Member> memberOptional = memberRepository.findByEmail(email);

        // 사용자가 존재하지 않을 경우 예외 throw
        Member member = memberOptional.orElseThrow(() -> {
//            log.warn("사용자를 찾을 수 없습니다: username={}", username);
            return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email);
        });

        return new CustomUserDetails(member);
    }
}
