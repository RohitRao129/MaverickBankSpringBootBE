package com.rohit.springboot.MaverickBank.loanManager.requestPayload;

import lombok.Data;

@Data
public class GetLoanRequestPayload {
    private Long approvedLoanId;
    private Long loanrequestid;
    private Long accountId;
    private String username;
    private Boolean approved=false;
}
