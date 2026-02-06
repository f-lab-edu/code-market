package com.main.codemarket.member.infra.repository;

import com.main.codemarket.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByEmail(String email);
    Optional<Member> findById(Long memberId);
}
