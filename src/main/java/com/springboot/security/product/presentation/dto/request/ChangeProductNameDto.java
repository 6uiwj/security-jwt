package com.springboot.security.product.presentation.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeProductNameDto {
    private Long number;
    private String name;
}