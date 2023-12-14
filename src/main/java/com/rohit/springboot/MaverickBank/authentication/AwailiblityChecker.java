package com.rohit.springboot.MaverickBank.authentication;

import com.rohit.springboot.MaverickBank.authentication.requestPayload.CheckRequestPayload;
import com.rohit.springboot.MaverickBank.entities.User;
import com.rohit.springboot.MaverickBank.repository.UserRepository;
import com.rohit.springboot.MaverickBank.security.Jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/check")
public class AwailiblityChecker {

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/username")
    @ResponseBody
    public String chkUsername(@RequestBody CheckRequestPayload request){
        User user = userRepository.findByEmailOrPhonenumberOrPan(request.getData(),request.getData(),request.getData());
        if(user!=null){
            return "inUse";
        }

        return "notUsed";
    }

    @PostMapping("/jwt")
    @ResponseBody
    public String chkJwt(@RequestBody CheckRequestPayload request){



        if(jwtUtils.validateJwtToken(request.getData()) && userRepository.findByEmailOrPhonenumberOrPan(jwtUtils.getUserNameFromJwtToken(request.getData()),jwtUtils.getUserNameFromJwtToken(request.getData()),jwtUtils.getUserNameFromJwtToken(request.getData()))!=null)
        return "valid";
        else return  "invalid";
    }

}
