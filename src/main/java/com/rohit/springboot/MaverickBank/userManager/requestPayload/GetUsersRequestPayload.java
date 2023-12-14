package com.rohit.springboot.MaverickBank.userManager.requestPayload;

import lombok.Data;

@Data
public class GetUsersRequestPayload {
    private Integer Count=-1;
    private Long Id=null;
    private String username=null;
    private String Ifsc=null;
    private  String role=null;
}
