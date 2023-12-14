package com.rohit.springboot.MaverickBank.authentication.requestPayload;

import lombok.Data;

import java.sql.Date;

@Data
public class SignupRequestPayload {
    private String fullname;
    private String phonenumber;
    private String email;
    private String password;
    private String address;
    private String pan;
    private Date dob;
    private  String pincode;
}
