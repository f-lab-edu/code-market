package com.main.codemarket.security.filter;

import com.main.codemarket.security.CustomUserDetailsService;
import com.main.codemarket.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import org.slf4j.Logger;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * 요청 헤더 유효성 검사, 로그인은 다음 필터이므로 현재 필터 스킵
     * 헤더에서 토큰 추출 후 토큰 유효성 검사
     * 해당 요청이 인증이 되지않았다면 인증 정보 저장
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //요청이 로그인인지 검사
        String path = request.getRequestURI();
        String method = request.getMethod();
        if ("/member/login".equals(path) && "POST".equals(method)) {
            //다음 필터인 로그인 진행
            filterChain.doFilter(request, response);
            return;
        }

        //요청이 유효하다면 요청에서 토큰 정보를 추출 후 토큰 검증
        //검증이 유효하다면 시큐리티 컨텍스트에 저장
        try {
            // 헤더 유효성 검증 후 토큰 추출
            String token = this.extractToken(request);
            //토큰 유효성 검증
            jwtUtil.validateToken(token);

            String email = jwtUtil.getSubject(token);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

            //지금 요청에서 이미 인증이 되어있는지 확인
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(userDetails, null));
            }
        } catch (IllegalArgumentException e) {
            //헤더에서 토큰 추출 혹은 토큰의 서명이나 형식이 오류인 경우
            logger.error(e.getMessage(), e.getStackTrace());
        } catch (IllegalStateException e) {
            //만료의 경우는 재발급이라 가정 (이후 클라이언트 재요청)
            logger.error(e.getMessage(), e.getStackTrace());
        } catch (Exception e) {
            logger.error("JWT 인증 필터 검증 오류 발생", e.getStackTrace());
        }

        //다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    public String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("요청 헤더의 형식이 잘못되었습니다");
        }

        String[] headers = header.split(" ");
        if (headers.length < 1) {
            throw new IllegalArgumentException("토큰의 형식이 잘못되었습니다");
        }
        return headers[1];
    }
}

