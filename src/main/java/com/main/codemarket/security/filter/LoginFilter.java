package com.main.codemarket.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.codemarket.security.CustomUserDetail;
import com.main.codemarket.security.JwtUtil;
import com.main.codemarket.security.dto.LoginRequestDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        setAuthenticationManager(authenticationManager);
        setFilterProcessesUrl("/member/login");
    }

    /**
     * 인증에 필요한 값이 있다면 인증 시도
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 요청으로부터 dto 로 변환
        LoginRequestDto loginRequestDto;
        try {
            loginRequestDto = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);
        } catch (IOException e) {
            throw new InternalAuthenticationServiceException("로그인 인증 시도 정보가 올바르지 않습니다");
        }

        // 인증에 필요한 정보 추출
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();
        if (!StringUtils.hasText(email) || !StringUtils.hasText(password)) {
            throw new IllegalArgumentException("요청 정보에 로그인에 필요한 정보가 없습니다");
        }

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email, password);
        return authenticationManager.authenticate(authRequest);
    }

    /**
     * 인증에 성공 했을 시 응답 헤더에 토큰을 담아 리턴
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) {
        CustomUserDetail customUserDetail = (CustomUserDetail) auth.getPrincipal();
        String email = customUserDetail.getUsername();
        String token = jwtUtil.createToken(email);
        response.addHeader("Authorization", "Bearer " + token);
    }

    /**
     * 인증에 실패할 시 401을 리턴
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
