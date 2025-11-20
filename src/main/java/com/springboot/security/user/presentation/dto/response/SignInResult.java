package com.springboot.security.user.presentation.dto.response;

public record SignInResult(
    SignUpResult signUpResult,
    String token
) {

}
