package com.rohit.springboot.MaverickBank.loanManager.requestPayload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.PublicKey;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanRequestPayload {
    public Long accountid;
    public Float amount;
    public Float interest;
    public Integer term;
    public String type;
}
