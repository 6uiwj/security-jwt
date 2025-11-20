package com.springboot.security.user.presentation;

import com.springboot.security.user.application.SignService;
import com.springboot.security.user.application.command.SignInCommand;
import com.springboot.security.user.application.command.SignUpCommand;
import com.springboot.security.user.presentation.dto.request.SignInRequest;
import com.springboot.security.user.presentation.dto.request.SignUpRequest;
import com.springboot.security.user.presentation.dto.response.SignInResult;
import com.springboot.security.user.presentation.dto.response.SignUpResult;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sign-api")
public class SignController {

    private final Logger LOGGER = LoggerFactory.getLogger(SignController.class);
    private final SignService signService;

    public SignController(SignService signService) {
        this.signService = signService;
    }

    @PostMapping(value = "/sign-in")
    public SignInResult signIn(@RequestBody SignInRequest signInRequest) throws Exception {
        LOGGER.info("[signIn] 로그인을 시도하고 있씁니다. id : {}. pw : **** ", signInRequest.id());

        //Command 객체 생성
        SignInCommand signInCommand = new SignInCommand(
            signInRequest.id(),
            signInRequest.password()
        );

        //service 호출
        SignInResult signInResult = signService.signIn(signInCommand);

        if(signInResult.signUpResult().code() == 0) {
            LOGGER.info("[signIn] 정상적으로 로그인되었습니다. id : {}, token : {}", signInRequest.id(), signInResult.token());
        }
        return signInResult;
    }

    @PostMapping(value = "/sign-up")
    public SignUpResult signUp(@RequestBody SignUpRequest signUpRequest) {
        LOGGER.info("[signUp] 회원가입을 수행합니다. id : {}, password : ****, name : {}, role : {}", signUpRequest.id(), signUpRequest.name(), signUpRequest.role());
        SignUpCommand signUpCommand = new SignUpCommand(
            signUpRequest.id(),
            signUpRequest.password(),
            signUpRequest.name(),
            signUpRequest.role()
        );

        SignUpResult signUpResult = signService.signUp(signUpCommand);

        LOGGER.info("[signUp] 회원가입을 완료했습니다. id : {}", signUpRequest.id());
        return signUpResult;
    }

    @GetMapping(value = "/exception")
    public void exceptionTest() throws RuntimeException {
        throw new RuntimeException("접근이 금지되었습니다.");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> ExceptionHandler(RuntimeException e) {
        HttpHeaders responseHeaders = new HttpHeaders();
        //responseHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        LOGGER.error("ExceptionHandler 호출, {}. {}", e.getCause(), e.getMessage());

        Map<String , String> map = new HashMap<>();
        map.put("error type", httpStatus.getReasonPhrase());
        map.put("code", "400");
        map.put("message", "에러 발생");
        return new ResponseEntity<>(map, responseHeaders, httpStatus);
    }
}
