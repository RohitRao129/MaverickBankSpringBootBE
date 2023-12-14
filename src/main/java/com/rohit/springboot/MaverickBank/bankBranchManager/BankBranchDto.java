package com.rohit.springboot.MaverickBank.bankBranchManager;

import lombok.Data;

@Data
public class BankBranchDto {

    private Integer count=4;
    private String city;
    private String ifsc;
    private String transferifsc;
    private String pincode;
}
