package com.main.codemarket.security.config;

import com.main.codemarket.security.JWTUtil;
import com.main.codemarket.security.filter.JWTFilter;
import com.main.codemarket.security.filter.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Spring Security의 AuthenticationManager를 빈으로 등록
     * - 로그인 시 사용자의 인증(Authentication)을 담당
     */
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 비밀번호 암호화를 위한 BCryptPasswordEncoder 빈 등록
     * - 회원가입 시 비밀번호를 안전하게 암호화하여 저장
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * CORS 설정을 위한 Bean 등록
     * - 프론트엔드(React 등)에서 API 요청 시 CORS 문제 해결
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); // 허용할 도메인
            configuration.setAllowedMethods(Collections.singletonList("*")); // 모든 HTTP 메서드 허용
            configuration.setAllowCredentials(true); // 인증 정보 포함 허용
            configuration.setAllowedHeaders(Collections.singletonList("*")); // 모든 헤더 허용
            configuration.setExposedHeaders(Collections.singletonList("Authorization")); // Authorization 헤더 노출
            configuration.setMaxAge(3600L); // 1시간 동안 캐싱
            return configuration;
        };
    }

    /**
     * Spring Security 필터 체인 설정
     * - JWT 인증을 기반으로 한 보안 설정 적용
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 적용
                .csrf(csrf -> csrf.disable())  // JWT 사용 시 CSRF 보호 비활성화
                .formLogin(form -> form.disable()) // 기본 로그인 폼 비활성화 (JWT 사용)
                .httpBasic(httpBasic -> httpBasic.disable()) // HTTP Basic 인증 비활성화

                // 엔드포인트별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/member/sign-up").permitAll() // 로그인, 회원가입, 홈은 누구나 접근 가능
                        .anyRequest().authenticated()) // 그 외의 요청은 인증된 사용자만 접근 가능

                // JWT 필터 추가 (기존 UsernamePasswordAuthenticationFilter 이전에 실행)
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)

                // 로그인 필터 추가 (JWTFilter 실행 후 JWT 발급 처리)
                .addFilterAfter(new LoginFilter(authenticationManager(), jwtUtil), JWTFilter.class)

                // 세션을 사용하지 않음 (JWT 기반 인증이므로 STATELESS 모드 설정)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();

    }
}
