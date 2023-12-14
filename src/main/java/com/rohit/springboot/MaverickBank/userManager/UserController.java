package com.rohit.springboot.MaverickBank.userManager;

import com.rohit.springboot.MaverickBank.authentication.responsePayload.LoginResponsePayload;
import com.rohit.springboot.MaverickBank.entities.User;
import com.rohit.springboot.MaverickBank.repository.UserRepository;
import com.rohit.springboot.MaverickBank.security.Jwt.JwtUtils;
import com.rohit.springboot.MaverickBank.security.UserDetailsImpl;
import com.rohit.springboot.MaverickBank.userManager.requestPayload.DeleteRequestPayload;
import com.rohit.springboot.MaverickBank.userManager.requestPayload.GetUsersRequestPayload;
import com.rohit.springboot.MaverickBank.userManager.requestPayload.UpdateRoleRequestPayload;
import com.rohit.springboot.MaverickBank.userManager.requestPayload.UpdateUserPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/get")
    public User getuser(@RequestBody GetUsersRequestPayload request){

        User user =null;
        if(request.getId()!=null)
        {user =userRepository.findById(request.getId()).get();}

        if(request.getUsername()!=null && user==null){
            user =userRepository.findByEmailOrPhonenumberOrPan(request.getUsername(),request.getUsername(),request.getUsername());
        }

        return user;
    }

    @PostMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('ADMIN','EMPLOYEE')")
    @ResponseBody
    public List<User> findAllUsers(@RequestBody GetUsersRequestPayload req){
        if(req.getRole()==null){
            return  userRepository.findAll();
        }
        if (req.getCount() == -1) {
            return userRepository.findAllByRole(req.getRole());
        }


        return userRepository.findAllByRoleNCnt(req.getRole(),req.getCount());
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserPayload updateUserPayload){
            User user = userRepository.findByEmailOrPhonenumberOrPan(updateUserPayload.getUsername(),updateUserPayload.getUsername(),updateUserPayload.getUsername());

            if (user == null) {
                return new ResponseEntity<>("User Not Found",HttpStatus.BAD_REQUEST);
            }

            if(!Objects.equals(user.getEmail(), updateUserPayload.getEmail())){
                if(userRepository.findByEmail(updateUserPayload.getEmail())!=null )
                {return new ResponseEntity<>("email already registered",HttpStatus.OK);}
                user.setEmail(updateUserPayload.getEmail());
            }
            if(!Objects.equals(user.getPhonenumber(), updateUserPayload.getPhonenumber())){
                if(userRepository.findByPhonenumber(updateUserPayload.getPhonenumber())!=null )
                {return new ResponseEntity<>("Phone number already registered",HttpStatus.OK);}

                user.setPhonenumber(updateUserPayload.getPhonenumber());
            }
            if(!Objects.equals(user.getPan(), updateUserPayload.getPan())){
                if(userRepository.findByPan(updateUserPayload.getPan())!=null )
                {return new ResponseEntity<>("Pan already registered",HttpStatus.OK);}
                user.setPan(updateUserPayload.getPan());
            }
            if(updateUserPayload.getFullname()!=null){
                user.setFullname(updateUserPayload.getFullname());
            }
            if(updateUserPayload.getDob()!=null){
                user.setDob(updateUserPayload.getDob());
            }
            if(updateUserPayload.getAddress()!=null){
                user.setAddress(updateUserPayload.getAddress());
            }
        if(updateUserPayload.getPincode()!=null){
            user.setPincode(updateUserPayload.getPincode());
        }

            userRepository.save(user);


            return new ResponseEntity<String>("Updated Successfully", HttpStatus.OK);


    }

    @PutMapping("/updateRole")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<?> updateUserRole(@RequestBody UpdateRoleRequestPayload updateRoleRequestPayload){

        if(!Objects.equals(updateRoleRequestPayload.getRole(), "ADMIN") && !Objects.equals(updateRoleRequestPayload.getRole(), "EMPLOYEE") && !Objects.equals(updateRoleRequestPayload.getRole(), "USER") ){
            return new ResponseEntity<>("Wrong Role value",HttpStatus.BAD_REQUEST);
        }

        User user =userRepository.findByEmailOrPhonenumberOrPanOrId(updateRoleRequestPayload.getUsername(),updateRoleRequestPayload.getUsername(),updateRoleRequestPayload.getUsername(),Long.parseLong(updateRoleRequestPayload.getUsername()));

        user.setRole(updateRoleRequestPayload.getRole());

        userRepository.save(user);

        return new ResponseEntity<>(user.getEmail()+" now has the role :"+updateRoleRequestPayload.getRole(),HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestBody DeleteRequestPayload deleteRequestPayload){
        User user = userRepository.findByEmailOrPhonenumberOrPan(deleteRequestPayload.getUsername(),deleteRequestPayload.getUsername(),deleteRequestPayload.getUsername());

        if(user==null)return  new ResponseEntity<>("No user found!",HttpStatus.BAD_REQUEST);

        if(Objects.equals(user.getRole(), "ADMIN") || Objects.equals(user.getRole(),"EMPLOYEE")){
            return  new ResponseEntity<>("admin or employeecant be delete at this url!",HttpStatus.BAD_REQUEST);
        }

        userRepository.delete(user);

        return  new ResponseEntity<>("User Deleted!",HttpStatus.OK);

    }

    @PostMapping("/delete/adminOrEmployee")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteEmployeeOrAdmin(@RequestBody DeleteRequestPayload deleteRequestPayload){
        User user = userRepository.findByEmailOrPhonenumberOrPan(deleteRequestPayload.getUsername(),deleteRequestPayload.getUsername(),deleteRequestPayload.getUsername());

        if(user==null)return  new ResponseEntity<>("No user found!",HttpStatus.BAD_REQUEST);

        userRepository.delete(user);

        return  new ResponseEntity<>("User Deleted!",HttpStatus.OK);

    }




}
