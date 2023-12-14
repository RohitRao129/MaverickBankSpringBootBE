package com.rohit.springboot.MaverickBank.authentication.requestPayload;

import lombok.Data;

@Data
public class LoginRequestPayload {
    private String username;
    private String password;
}
