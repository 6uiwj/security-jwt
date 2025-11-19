package com.springboot.security.user.infrastructure.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.security.user.presentation.dto.response.EntryPointErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;


//인증이 실패한 상황을 처리
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();
            LOGGER.info("[commence] 인증 실패로 response.sendError 발생");

            EntryPointErrorResponse entryPointErrorResponse = new EntryPointErrorResponse();
            entryPointErrorResponse.setMsg("인증이 실패핬습니다.");

            response.setStatus(401);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(entryPointErrorResponse)); //dto의 메시지를 -> json으로 변환
    }
}
