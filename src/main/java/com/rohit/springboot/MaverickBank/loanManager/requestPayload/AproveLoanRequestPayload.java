package com.rohit.springboot.MaverickBank.loanManager.requestPayload;

import lombok.Data;

@Data
public class AproveLoanRequestPayload {
    private String approver;
    private Long requestid;
}
