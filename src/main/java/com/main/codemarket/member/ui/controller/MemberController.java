package com.main.codemarket.member.ui.controller;

import com.main.codemarket.member.application.service.MemberService;
import com.main.codemarket.member.ui.dto.SignUpDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * 회원가입 API
     * - JSON 데이터를 받아 MemberService에서 회원가입 처리
     */
    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody SignUpDto signUpDto) {
        memberService.signUp(SignUpDto.createMemberEntity(signUpDto));
        return ResponseEntity.ok("회원가입 성공");
    }

}
