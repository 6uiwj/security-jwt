package com.springboot.security.user.application;

import com.springboot.security.user.application.command.SignInCommand;
import com.springboot.security.user.application.command.SignUpCommand;
import com.springboot.security.user.presentation.dto.response.SignInResult;
import com.springboot.security.user.presentation.dto.response.SignUpResult;

public interface SignService {
    SignUpResult signUp(SignUpCommand signUpCommand);

    SignInResult signIn(SignInCommand signInCommand) throws RuntimeException;

}
