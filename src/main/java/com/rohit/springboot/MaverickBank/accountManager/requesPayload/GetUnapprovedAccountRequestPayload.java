package com.rohit.springboot.MaverickBank.accountManager.requesPayload;

import lombok.Data;

@Data
public class GetUnapprovedAccountRequestPayload {
    private String ifsc;
    private  Integer count =10;
}
