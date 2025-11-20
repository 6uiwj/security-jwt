package com.springboot.security.user.presentation.dto.request;

public record SignInRequest(
    String id,
    String password
) {

}
