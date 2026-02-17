package com.main.codemarket.security;

import com.main.codemarket.member.infra.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;
    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return memberRepository.findByEmail(email)
                .map(member -> new CustomUserDetail(member.getEmail(), member.getPassword()))
                .orElseThrow(() -> new UsernameNotFoundException(email + "계정 정보를 찾을 수 없습니다"));
    }
}
