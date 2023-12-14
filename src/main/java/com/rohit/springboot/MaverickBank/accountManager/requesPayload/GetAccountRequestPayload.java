package com.rohit.springboot.MaverickBank.accountManager.requesPayload;

import lombok.Data;

@Data
public class GetAccountRequestPayload {
    private String username;
    private Integer count =10;
    private Long account_id=null;
    private  Boolean approved=null;
}
