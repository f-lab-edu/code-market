package com.main.codemarket.member.ui.controller;

import com.main.codemarket.member.application.service.MemberService;
import com.main.codemarket.member.ui.dto.SignUpDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@Tag(name = "Member", description = "회원 관련 API")
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    public MemberController(MemberService memberService, PasswordEncoder passwordEncoder) {
        this.memberService = memberService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 회원가입 API
     * - JSON 데이터를 받아 MemberService에서 회원가입 처리
     */
    @Operation(summary = "회원 가입", description = "새로운 회원을 생성한다.")
    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignUpDto signUpDto) {
        String encodedPassword = passwordEncoder.encode(signUpDto.getPassword());
        memberService.signUp(SignUpDto.createMemberEntity(signUpDto, encodedPassword));
        return ResponseEntity.ok("회원가입 성공");
    }
}
