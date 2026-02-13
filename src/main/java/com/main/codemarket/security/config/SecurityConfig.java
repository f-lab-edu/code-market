package com.main.codemarket.security.config;

import com.main.codemarket.security.CustomUserDetailsService;
import com.main.codemarket.security.JwtUtil;
import com.main.codemarket.security.filter.JwtAuthenticationFilter;
import com.main.codemarket.security.filter.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;

    public SecurityConfig(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService, AuthenticationConfiguration authenticationConfiguration) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    /*
     * 인증을 필요한 요청과 아닌 요청을 구분
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = jwtAuthenticationFilter(jwtUtil, customUserDetailsService);
        LoginFilter loginFilter = loginFilter(authenticationManager(authenticationConfiguration), jwtUtil);
        httpSecurity
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/swagger-ui.html", "/member/login", "/member/sign-up").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    /*
     * 로그인 등의 인증 과정에서 커스텀한 인증 과정을 진행하기 위해 빈 등록
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /*
     * 회원 암호 인코딩 방식 지정
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        return new JwtAuthenticationFilter(jwtUtil, customUserDetailsService);
    }

    @Bean
    public LoginFilter loginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        LoginFilter filter = new LoginFilter(authenticationManager, jwtUtil);
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }
}
