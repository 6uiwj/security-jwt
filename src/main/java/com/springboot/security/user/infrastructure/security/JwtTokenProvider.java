package com.springboot.security.user.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * 1. init : secretKey를 Base64형식으로 인코딩
 * 2. createToken : 토큰 생성 - 헤더+페이로드+서명 만들기
 * 3. getUsername : 토큰 파싱 - 회원 정보(sub) 반환
 * 4. getAuthentication : JWT 토큰 파싱  - JWT 정보로 스프링 시큐리티 인증 객체(Authentication) 생성
 * 5. resolveToken
 * 6. validateToken
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider { //UserDetails 정보를 가져와 JWT 토큰 생성


    private final org.slf4j.Logger LOGGER =  LoggerFactory.getLogger(JwtTokenProvider.class);
    private final UserDetailsService userDetailsService;

    @Value("${springboot.jwt.secret}")
    private String secretKey = "secretKey"; //yml파일에서 secretkey 가져오기

    private Key key;
    private final long tokenValidMillisecond = 1000L * 60 * 60; //토큰 만료 시간 (1시간)


    /**
     *  secretKey를 Base64형식으로 인코딩
     */
    @PostConstruct //Spring이 Bean을 모두 생성하고 의존성 주입이 끝난 직후에 실행되는 메서드
    protected void init() {
        LOGGER.info("[init] JwtTokenProvider 내 secretKey 초기화 시작");
        //키를 아스키코드로 인코딩
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
        LOGGER.info("[init] JwtTokenProvider 내 secretKey 초기화 완료");
    }

    /**
     * JWT 토큰 생성
     *  1. claimas 생성
     *  2. key 암호화
     *  3. 토큰에 데이터 담기
     * @param userUid : subject
     * @param roles : 권한
     * @return
     */
    public String createToken(String userUid, List<String> roles) {
        LOGGER.info("[createToken] 토큰 생성 시작");
        Claims claims = Jwts.claims().setSubject(userUid); //sub속성 추가
        claims.put("roles", roles); //roles 추가
        /**
         * {
         *   "sub": "user123",
         *   "roles": ["ROLE_USER", "ROLE_ADMIN"]
         * }
         */

        Date now = new Date(); //발급 시각과 만료 시각 설정위해 Date 객체 생성


        //JJWT 0.11.x 이후 새로운 버전
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        //HMAC-SHA 비밀키 알고리즘에서 사용할 적정 길이의 key 객체로 변환
        this.key = Keys.hmacShaKeyFor(keyBytes);

        String token = Jwts.builder() //위에서 만든 claims와 meta data 담기
            .setClaims(claims)
            .setIssuedAt(now) //발급 시각
            .setExpiration(new Date(now.getTime() + tokenValidMillisecond)) //만료 시각
            //.signWith(SignatureAlgorithm.HS256, secretKey) //deprecated
            .signWith(key, SignatureAlgorithm.HS256) //헤더에 alg추가, signature 추가 - JJWT 0.11.x 이후 방식
            .compact();

            //참고: signWith 호출 시 자동으로 typ: JWT로 들어감
            // signWith(key, SignatureAlgorithm.HS256) -> Header : { "typ": "JWT", alg: "HS256" } 설정 + 서명

            LOGGER.info("[createToken] 토큰 생성 완료");
        /**
         * 최종 결과
         * {
         *   "sub": "user123",
         *   "roles": ["ROLE_USER"],
         *   "iat": 1729892000,
         *   "exp": 1729895600
         * }
         */
            return token;
    }

    //JWT 검증 후 JWT를 Spring Security 인증 객체로 바꿔주는 역할
    //Authentication 현재 인증된 사용자 객체 ( -> principal(사용자 식별), credential(비밀번호-토큰 기반인증으로 필요없음), authorities(권한)가 존재)
    /**
     * 최종 반환되는 Authentication 객체
     * UsernamePasswordAuthenticationToken
     *  ├─ principal : UserDetails(user123)
     *  ├─ credentials : ""
     *  └─ authorities : [ROLE_USER, ROLE_ADMIN]
     */
    public Authentication getAuthentication(String token) {
        LOGGER.info("[getAuthentication] 토큰 인증 정보 조회 시작");
        //"token"에서 username을 추출해 DB에서 사용자정보 조회하여 userDetails에 담음
        //UserDetails: 한 명의 사용자 정보를 제공하는 인터페이스
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUsername(token));
        LOGGER.info("[getAuthentication] 토큰 인증 정보 조회 완료, UserDetails Username : {}", userDetails.getUsername());
        //UsernamePasswordAuthenticationToken : Authentication 인터페이스를 구현한 AbstractAuthenticationToken의 하위 객체, 인증객체를 만드는데 사용
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

    }

    //유저 id 파싱
    public String getUsername(String token) {
        LOGGER.info("[getUsername] 토큰 기반 회원 구별 정보 추출");

        byte[] ketBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(ketBytes);  //문자열 비밀키를 key 객체로 반환
        //토큰 파싱 -> sub 추출
        //setSigningKey :  토큰을 검증할 때 사용할 비밀키를 설정
        //parseClaimsJws(token) :  토큰을 파싱 (Base64 디코딩과 서명 검증) -> 검증 성공시 Jws<Claims> 객체 반환
        //Jws : 서명된 JWT
        String info = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();

        LOGGER.info("[getUsername] 토큰 기반 회원 구별 정보 추출 완료, info : {}", info);
        return info;
    }

    //클라이언트가 보낸 HTTP 요청 헤더에서 JWT 토큰 꺼내오는 역할
    public String resolveToken(HttpServletRequest request) {
        LOGGER.info("[resolveToken] HTTP 헤더에서 Token 값 추출");
        return request.getHeader("X-AUTH-TOKEN");
    }

    public boolean validateToken(String token) {
        LOGGER.info("[validationToken] 토큰 유효 체크 시작");

        try{
            byte[] ketBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            this.key = Keys.hmacShaKeyFor(ketBytes);
            //parseClaimsJws(token) : 실제 토큰 파싱 및 서명 검증
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token); //토큰 파싱
            //토큰의 만료기간 확인
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e){
            LOGGER.info("[validateToken] 토큰 유효 체크 예외 발생");
            return false;
        }
    }
}
