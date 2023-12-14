package com.rohit.springboot.MaverickBank.authentication;

import com.rohit.springboot.MaverickBank.authentication.requestPayload.LoginRequestPayload;
import com.rohit.springboot.MaverickBank.authentication.requestPayload.SignupRequestPayload;
import com.rohit.springboot.MaverickBank.authentication.responsePayload.LoginResponsePayload;
import com.rohit.springboot.MaverickBank.entities.User;
import com.rohit.springboot.MaverickBank.repository.UserRepository;
import com.rohit.springboot.MaverickBank.security.Jwt.JwtUtils;
import com.rohit.springboot.MaverickBank.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.Array;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/signUp")
    @Async
    public ResponseEntity<?> addUser(@RequestBody SignupRequestPayload signupRequestPayload){
        //System.out.println(signupRequestPayload);

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();


        User existinguser = userRepository.findByEmail(signupRequestPayload.getEmail());

        Boolean alreadyRegistered =false;
        HashMap<String,Boolean > map  = new HashMap<>();
        if(existinguser!=null){
            alreadyRegistered =true;
            map.put("email", true );
        }
        existinguser = userRepository.findByPhonenumber(signupRequestPayload.getPhonenumber());
        if(existinguser!=null){
            alreadyRegistered =true;
            map.put("phone", true );
        }
        existinguser = userRepository.findByPan(signupRequestPayload.getPan());
        if(existinguser!=null){
            alreadyRegistered =true;
            map.put("pan", true );
        }

        if(alreadyRegistered){
            return  new ResponseEntity<>(map,HttpStatus.OK);
        }

        String encodedPassword = bCryptPasswordEncoder.encode(signupRequestPayload.getPassword());

        User user = new User();
        user.setFullname(signupRequestPayload.getFullname());
        user.setEmail(signupRequestPayload.getEmail());
        user.setPhonenumber(signupRequestPayload.getPhonenumber());
        user.setPassword(encodedPassword);
        user.setPan(signupRequestPayload.getPan());
        user.setPincode(signupRequestPayload.getPincode());


        user.setDob(signupRequestPayload.getDob());
        user.setAddress(signupRequestPayload.getAddress());
        user.setRole("USER");
        userRepository.save(user);

        return new ResponseEntity<>("Success", HttpStatus.OK);

    }


    @ResponseBody
    @PostMapping("/signIn")
    @Async
    public ResponseEntity<?> Login(@RequestBody LoginRequestPayload loginRequestPayload){
        User user = userRepository.findByEmailOrPhonenumberOrPan(loginRequestPayload.getUsername(),loginRequestPayload.getUsername(),loginRequestPayload.getUsername());
        if(user==null){
            return new ResponseEntity<>(1, HttpStatus.BAD_REQUEST);
        }
        else{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestPayload.getUsername(), loginRequestPayload.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);



            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .toList().get(0);

            return ResponseEntity.ok(jwt);
        }


    }





}
