package com.rohit.springboot.MaverickBank.userManager.requestPayload;

import lombok.Data;

import java.sql.Date;

@Data
public class UpdateUserPayload {
    private String Username;
    private String pincode;
    private String fullname=null;
    private String phonenumber=null;
    private String email=null;
    private String password=null;
    private String address=null;
    private String pan=null;
    private Date dob=null;

}
