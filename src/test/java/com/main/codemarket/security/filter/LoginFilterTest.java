package com.main.codemarket.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.codemarket.security.JwtUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoginFilterTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;
    private LoginFilter loginFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        loginFilter = new LoginFilter(authenticationManager, jwtUtil, new ObjectMapper());
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        request.setMethod("POST");
        request.setContentType("application/json");
    }

    @Test
    @DisplayName("인증에 필요한 응답 값을 파싱하고, 인증을 진행한다")
    void attemptAuthentication_success() {
        //given
        String jsonBody = "{\"email\":\"test@test.com\",\"password\":\"123456789\"}";
        request.setContent(jsonBody.getBytes(StandardCharsets.UTF_8));

        //when
        loginFilter.attemptAuthentication(request, response);

        //실제 인증 과정 호출하였는지 테스트
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("로그인 인증에 필요한 값이 파싱이 되지 않는다면 예외를 던진다")
    void loginRequest_format_invalid() {
        //given
        String jsonBody = "{\"mail\":\"test@test.com\",\"password\":\"123456789\"}";
        request.setContent(jsonBody.getBytes(StandardCharsets.UTF_8));

        //when & then
        Assertions.assertThrows(InternalAuthenticationServiceException.class,
                () -> loginFilter.attemptAuthentication(request, response));

    }

    @Test
    @DisplayName("로그인 인증에 필요한 값이 유효하지 않다면 예외를 던진다")
    void loginRequest_value_invalid() {
        //given
        String jsonBodyWithEmptyEmail = "{\"email\":\"\",\"password\":\"123456789\"}";
        request.setContent(jsonBodyWithEmptyEmail.getBytes(StandardCharsets.UTF_8));

        //when & then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> loginFilter.attemptAuthentication(request, response));

        //given
        String jsonBodyWithEmptyPassword = "{\"email\":\"test@test.com\",\"password\":\"\"}";
        request.setContent(jsonBodyWithEmptyPassword.getBytes(StandardCharsets.UTF_8));

        //when & then
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> loginFilter.attemptAuthentication(request, response));
    }
}