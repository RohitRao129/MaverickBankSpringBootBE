package com.rohit.springboot.MaverickBank.transactionManager;

import lombok.Data;

@Data
public class TransactionReqDto {
    private Long id;
    private String pin;
    private Float amount;
    private Long owner;
    private Long receiver;
    private String ifsc;
    private String username;
    private Integer count =10;
}
