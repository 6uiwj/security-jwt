package com.springboot.security.user.presentation.dto.response;

public record SignUpResult(
    boolean success,
    int code,
    String msg  ) {

}
