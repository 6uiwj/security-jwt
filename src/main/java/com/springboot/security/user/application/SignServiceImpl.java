package com.springboot.security.user.application;

import com.springboot.security.common.CommonResponse;
import com.springboot.security.user.application.command.SignInCommand;
import com.springboot.security.user.application.command.SignUpCommand;
import com.springboot.security.user.domain.entity.User;
import com.springboot.security.user.domain.repository.UserRepository;
import com.springboot.security.user.infrastructure.config.security.JwtTokenProvider;
import com.springboot.security.user.presentation.dto.response.SignInResult;
import com.springboot.security.user.presentation.dto.response.SignUpResult;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SignServiceImpl implements SignService{

    private final Logger LOGGER = LoggerFactory.getLogger(SignServiceImpl.class);

    public UserRepository userRepository;
    public JwtTokenProvider jwtTokenProvider;
    public PasswordEncoder passwordEncoder; //SecurityConfiguration에 정의

    @Autowired
    public SignServiceImpl(UserRepository userRepository, JwtTokenProvider jwtTokenProvider,
        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 회원가입
     */
    @Override
    public SignUpResult signUp(SignUpCommand signUpCommand) {

        String id = signUpCommand.id();
        String name = signUpCommand.name();
        String password = signUpCommand.password();
        String role = signUpCommand.role();

        LOGGER.info("[getSignUpResult] 회원 가입 정보 전달");
        User user;

        //권한이 Admin이면 "ROLE_ADMIN"으로 유저 정보 저장
        if(role.equalsIgnoreCase("admin")){ //두 문자열이 대소문자 구분 없이 같은지 비교할 때 사용
            user = User.builder()
                .uid(id)
                .name(name)
                .password(passwordEncoder.encode(password)) //비밀번호 인코딩해서 저장
                .roles(Collections.singletonList("ROLE_ADMIN"))
                .build();
        } else { //나머지는 "ROLE_USER"로 저장
            user = User.builder()
                .uid(id)
                .name(name)
                .password(passwordEncoder.encode(password))
                .roles(Collections.singletonList("ROLE_USER"))
                .build();
        }

        User savedUser = userRepository.save(user);

        SignUpResult signUpResult;

        LOGGER.info("[getSignUpResult] userEntity 값이 들어왔는지 확인 후 결과값 주입");

        if( !savedUser.getName().isEmpty()) {
            LOGGER.info("[getSignUpResul] 정상 처리 완료");
            signUpResult = createSuccessResult();
        } else {
            LOGGER.info("[getSignUpResult] 실패 처리 완료");
            signUpResult = createFailResult();
        }
        return signUpResult;
    }

    @Override
    public SignInResult signIn(SignInCommand signInCommand) throws RuntimeException {

        String id = signInCommand.id();
        String password = signInCommand.password();

        LOGGER.info("[getSignInResult] signDataHandler 로 회원 정보 요청");
        User user = userRepository.getByUid(id); //존재하는 id 인지 조회
        LOGGER.info("[getSignInResult] Id : {}", id);

        LOGGER.info("[getSignInResult] 패스워드 비교 수행");
        if (!passwordEncoder.matches(password, user.getPassword())) { //비밀번호 비교
            throw new RuntimeException();
        }

        LOGGER.info("[getSignInResult] 패스워드 일치");


        LOGGER.info("[getSignInResult] SignInResultDto 객체 생성");
        SignInResult signInResult = new SignInResult(
            createSuccessResult(),
            jwtTokenProvider.createToken(String.valueOf(user.getUid()), user.getRoles()));

        LOGGER.info("[getSignInResult] SignInResultDto 객체에 값 주입");
        return signInResult;
    }

    private SignUpResult createSuccessResult() {
        return new SignUpResult(
            true,
            CommonResponse.SUCCESS.getCode(),
            CommonResponse.SUCCESS.getMsg()
        );
    }

    private SignUpResult createFailResult() {
        return new SignUpResult(
            false,
            CommonResponse.FAIL.getCode(),
            CommonResponse.FAIL.getMsg()
        );
    }
}
