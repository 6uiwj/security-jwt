package com.springboot.security.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
    @NotBlank
    String id,

    @NotBlank
    String password,

    @NotBlank
    String name,
    String role ) {

}
