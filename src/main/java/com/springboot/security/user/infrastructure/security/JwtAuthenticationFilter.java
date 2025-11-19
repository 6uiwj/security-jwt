package com.springboot.security.user.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JwtAuthenticationFilter는 모든 HTTP 요청마다(한 번씩) 들어온 요청에서 JWT를 꺼내고(추출),
 * 토큰이 유효하면 Authentication을 만들어 SecurityContextHolder에 넣어주는 역할을 함
 * 그 뒤 요청 처리는 다음 필터(혹은 컨트롤러)로 이어짐
 */
//JWT 토큰 인증
    //OncePerRequestFilter : 같은 요청에 대해 한 번만 실행된다(포워딩/인클루드 시 중복 호출 방지)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //각 필터의 작업을 담당하는 메서드
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //servletRequest에서 토큰 추출
        String token = jwtTokenProvider.resolveToken(request);
        LOGGER.info("[doFilterInternal] token 값 추출 완료. token : {}", token);

        //토큰 유효성 검사
        LOGGER.info("[doFilterInternal] token 값 유효성 체크 시작");
        if(token != null && jwtTokenProvider.validateToken(token)) {
            //유효한 토큰이면 Authentication 객체를 생성하여 토크에서 추출한 사용자 정보를 담아 SecurityContextHolder에 추가
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            LOGGER.info("[doFilterInternal] token 값 유효성 체크 완료");
        }
        //인증 설정이 끝난 뒤 요청을 다음 필터/서블릿으로 전달. 매우 중요(반드시 호출되어야 함).
        filterChain.doFilter(request, response);

    }
}
