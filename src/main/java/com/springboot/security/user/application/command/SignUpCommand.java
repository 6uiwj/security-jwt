package com.springboot.security.user.application.command;

public record SignUpCommand(
    String id,
    String password,
    String name,
    String role
) {

}
