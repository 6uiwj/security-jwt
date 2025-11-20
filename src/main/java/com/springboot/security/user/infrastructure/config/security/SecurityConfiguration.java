package com.springboot.security.user.infrastructure.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//스프링 시큐리티 설정
@EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfiguration {

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public SecurityConfiguration(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    /**
     * HttpSecurity 기능
     *  - 리소스 접근 권한 설정
     *  - 인증 실패 시 발생하는 예외 처리
     *  - 인증 로직 커스터마이징
     *  - csrf, cors 등의 스프링 시큐리티 설정
     */
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            /**
             * RestAPI 에서 CSRF 보안이 필요없는 이유
             * - CSRF 공격은 쿠키 + 세션 기반 인증에서만 의미 있음
             * - REST API는 JWT 등 헤더 인증 + Stateless → 브라우저가 자동으로 인증 정보 전송 안 함
             * - 따라서 대부분 REST API에서는 CSRF 보안 필요 없음
             * - 단, JWT를 쿠키에 넣어 사용하면 CSRF 공격 가능 → 주의 필요
             */
            .csrf(AbstractHttpConfigurer::disable) //RestAPI에서는 CSRF 보안이 필요 없기 때문에 비활성 (활성화시 csrf 토큰 필요)
            //세션 stateless로 설정
            .sessionManagement(httpSecuritySessionManagementConfigurer ->
                httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            //UI 사용하는 것을 기본값으로 가진 시큐리티 설정 비활성화
            .httpBasic(AbstractHttpConfigurer::disable)
            //권한 체크 / 경로별 권한 설정
            .authorizeHttpRequests(authorize ->
                authorize
                    .requestMatchers("/sign-api/sign-up", "/swagger", "/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**", "/v3/api-docs/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/product/**").permitAll()
                    .requestMatchers("**exception**").permitAll()
                    .anyRequest().hasRole("ADMIN"))
            .formLogin(AbstractHttpConfigurer::disable) //폼 로그인 비활성화
            //JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 앞에 추가
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class) //필터 배치할 위치 설정
            .exceptionHandling((exceptionHandling -> //예외 처리에 사용할 핸들러 지정
                exceptionHandling
                    .authenticationEntryPoint(new CustomAuthenticationEntryPoint()) //인증이 필요한 요청인데, 사용자가 로그인하지 않았거나 인증되지 않은 상태에서 접근했을 때
                    .accessDeniedHandler(new CustomAccessDeniedHandler()))); //인증은 됐지만, 요청한 리소스에 권한이 없는 경우
        return httpSecurity.build();
    }
}
