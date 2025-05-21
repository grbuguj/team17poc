package com.team17.poc.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
