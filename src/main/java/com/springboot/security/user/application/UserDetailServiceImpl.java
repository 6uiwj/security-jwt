package com.springboot.security.user.application;

import com.springboot.security.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailServiceImpl implements UserDetailsService { //UserDetailsService : 시큐리티한테 사용자 정보를 로드하는 클래스

    private final Logger LOGGER = LoggerFactory.getLogger(UserDetailServiceImpl.class);
    private final UserRepository userRepository;


    //UserDetails에 조회한 유저의 정보(username, password, Authorities)를 담아 인증에 사용
    @Override
    public UserDetails loadUserByUsername(String username)  { //로그인할 때 자동으로 호출되는 메서드
        LOGGER.info("[loadUserByUsername] loadUserByUsername 수행. username = {}", username);
        return userRepository.getByUid(username); //userDetails의 구현체로 만든 Usr 객체 반환
    }
}
