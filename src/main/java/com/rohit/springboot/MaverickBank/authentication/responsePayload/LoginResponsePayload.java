package com.rohit.springboot.MaverickBank.authentication.responsePayload;

import lombok.*;

@Data
public class LoginResponsePayload {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String roles;
    private Boolean userNameError;
    private Boolean passwordError;

    public LoginResponsePayload(String accessToken, Long id, String username, String roles,boolean userNameError,boolean passwordError) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.userNameError =userNameError;
        this.passwordError =passwordError;
    }

}


