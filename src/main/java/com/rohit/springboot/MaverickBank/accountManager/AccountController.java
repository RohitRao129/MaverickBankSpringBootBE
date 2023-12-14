package com.rohit.springboot.MaverickBank.accountManager;

import com.rohit.springboot.MaverickBank.accountManager.requesPayload.AccountIdPayload;
import com.rohit.springboot.MaverickBank.accountManager.requesPayload.GetAccountRequestPayload;
import com.rohit.springboot.MaverickBank.accountManager.requesPayload.GetUnapprovedAccountRequestPayload;
import com.rohit.springboot.MaverickBank.accountManager.requesPayload.MakeAccountRequestPayload;
import com.rohit.springboot.MaverickBank.emailService.EmailDetails;
import com.rohit.springboot.MaverickBank.emailService.EmailService;
import com.rohit.springboot.MaverickBank.entities.Account;
import com.rohit.springboot.MaverickBank.entities.BankBranch;
import com.rohit.springboot.MaverickBank.entities.User;
import com.rohit.springboot.MaverickBank.repository.AccountRepository;
import com.rohit.springboot.MaverickBank.repository.BankBranchRepository;
import com.rohit.springboot.MaverickBank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private BankBranchRepository bankBranchRepository;

    @Autowired
    private EmailService emailService;


    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/getAll")
    @ResponseBody
    public  List<Account> getallaccounts(){
        return accountRepository.findAll();
    }


    public String setpin(){
        String pincontainer ="0123456789";
        StringBuilder sb = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            int index = (int)(pincontainer.length() * Math.random());
            sb.append(pincontainer.charAt(index));
        }

        return  sb.toString();
    }

    @PostMapping("/create")
    public  ResponseEntity<?> createAccount(@RequestBody MakeAccountRequestPayload request){
        User owner = userRepository.findByEmailOrPhonenumberOrPan(request.getUsername(),request.getUsername(), request.getUsername());
        BankBranch branch = bankBranchRepository.findByIfsc(request.getIfsc());

        if(owner==null){
            return new ResponseEntity<>("User Not Found" ,HttpStatus.NOT_FOUND);
        }
        if(branch==null){
            return new ResponseEntity<>("Branch Not Found" ,HttpStatus.NOT_FOUND);
        }

        List<User> owners =new ArrayList<>();owners.add(owner);

        Account accountrequest = new Account();
        accountrequest.setAccountOwners(owners);
        accountrequest.setBalance(0f);
        accountrequest.setCreationdate(new Date());
        accountrequest.setPin(setpin());
        accountrequest.setIfsc(branch.getIfsc());
        accountrequest.setBranch(branch.getCity());
        accountrequest.setApproved(false);
        accountRepository.save(accountrequest);

        return new ResponseEntity<>( accountrequest.getPin(),HttpStatus.OK);
    }


    @Query("Delete from accounts_users as t where t.account_id = idaccount ")
    private void deleteaccount(@Param("idaccount")long accountid){}

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN','EMPLOYEE')")
    public ResponseEntity<?> deleteAccount(@RequestBody AccountIdPayload req){
        Account account = accountRepository.findById(Long.parseLong(req.getAccount_id())).get();

        if(account==null){
            return  new ResponseEntity<>("Account not found!",HttpStatus.NOT_FOUND);
        }

        accountRepository.delete(account);

        return  new ResponseEntity<>("Account Deleted!",HttpStatus.OK);
    }

    @PutMapping("/close")
    @PreAuthorize("hasAnyAuthority('ADMIN','EMPLOYEE')")
    public ResponseEntity<?> closeAccount(@RequestBody AccountIdPayload req){
        Account account = accountRepository.findById(Long.parseLong(req.getAccount_id())).get();

        if(account==null){
            return  new ResponseEntity<>("Account not found!",HttpStatus.NOT_FOUND);
        }

        if(account.isApproved()==false){
            return  new ResponseEntity<>("cannot closeaccount before aproval!",HttpStatus.NOT_FOUND);
        }
        account.setClosedate(new Date());
        accountRepository.save(account);

        return  new ResponseEntity<>("Account Closed!",HttpStatus.OK);
    }


    //get by account of a user by his username
    @PostMapping("/get")
    @ResponseBody
    public  List<Account> getAccountOfUser(@RequestBody GetAccountRequestPayload request){
        if(request.getAccount_id()!=null ){
            List<Account> accounts =new ArrayList<>();
           Optional<Account> account = accountRepository.findById(request.getAccount_id());
            if(!account.isPresent()){
                return null;
            }
            accounts.add(account.get());
            return accounts;
        }

        User user =userRepository.findByEmailOrPhonenumberOrPan(request.getUsername(),request.getUsername(),request.getUsername());
        if(user==null){
            return null;
        }

        List<Account> accounts = user.getAccounts();
        if(request.getApproved()==null){
            return accounts;
        }
        List<Account> finalAccounts = new ArrayList<>();
        if(request.getApproved()){
            for(int i= accounts.size()-1;i>=0;i--){
                if(accounts.get(i).isApproved()){
                    finalAccounts.add(accounts.get(i));
                }
            }
        }else{

            for(int i= accounts.size()-1;i>=0;i--){
                if(!accounts.get(i).isApproved()){
                    finalAccounts.add(accounts.get(i));
                }
            }
        }


        return finalAccounts;
    }

    //approve account by ADMIN OR EMPLOYEE
    @PutMapping("/approve")
    @PreAuthorize("hasAnyAuthority('ADMIN','EMPLOYEE')")
    public ResponseEntity<String> approveAccount(@RequestBody AccountIdPayload request){


        Account account =accountRepository.findById(Long.parseLong(request.getAccount_id())).get();
        if(account==null){
            return  new ResponseEntity<>("account not found!",HttpStatus.NOT_FOUND);
        }

        if(account.isApproved()){
            return  new ResponseEntity<>("account Already Approved!",HttpStatus.METHOD_NOT_ALLOWED);
        }
        account.setApproved(true);

        EmailDetails details =new EmailDetails();
        details.setSubject("Account Approved!");
        details.setMsgBody("Dear customer! your bank account is Approved with account number : "+ account.getId()+"/n The SECURITY PIN is : "+account.getPin());

        try{
            String emailstatus = emailService.sendSimpleMail(details);
            System.out.println(emailstatus);
        }catch (Error e){
            System.out.println(e);
            return null;
        }




        accountRepository.save(account);

        return new ResponseEntity<>("Account Creation Approved nad pin is :" + account.getPin() , HttpStatus.OK);

    }

    @PostMapping("/getApproveRequests")
    @PreAuthorize("hasAnyAuthority('ADMIN','EMPLOYEE')")
    @ResponseBody
    public List<Account> GetAllApproveRequsets(@RequestBody GetUnapprovedAccountRequestPayload request){
        if(request.getIfsc()==null || Objects.equals(request.getIfsc(), "") ){
            return accountRepository.findAllByApproved(false,request.getCount());
        }

        return accountRepository.findAllByIfscAndApproved(request.getIfsc(),false,request.getCount());

    }





}
