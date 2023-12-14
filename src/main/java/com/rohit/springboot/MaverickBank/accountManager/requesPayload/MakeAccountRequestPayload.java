package com.rohit.springboot.MaverickBank.accountManager.requesPayload;

import lombok.Data;

@Data
public class MakeAccountRequestPayload {
    private String username;
    private String ifsc;
}
