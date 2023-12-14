package com.rohit.springboot.MaverickBank.userManager.requestPayload;

import lombok.Data;

@Data
public class UpdateRoleRequestPayload {
    private String username;
    private String Role;
}
