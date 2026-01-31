package com.main.codemarket.member.application.service;

import com.main.codemarket.member.domain.entity.Member;
import com.main.codemarket.member.infra.repository.MemberRepository;
import com.main.codemarket.member.ui.dto.SignUpDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public interface MemberService {
    public Member signUp(Member member);


}
