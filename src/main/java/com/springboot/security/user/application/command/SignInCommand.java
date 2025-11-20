package com.springboot.security.user.application.command;

public record SignInCommand(
    String id,
    String password
) {

}
