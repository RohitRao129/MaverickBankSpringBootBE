package com.rohit.springboot.MaverickBank.loanManager;

import com.rohit.springboot.MaverickBank.entities.Account;
import com.rohit.springboot.MaverickBank.entities.ApprovedLoan;
import com.rohit.springboot.MaverickBank.entities.LoanRequest;
import com.rohit.springboot.MaverickBank.entities.User;
import com.rohit.springboot.MaverickBank.loanManager.requestPayload.AproveLoanRequestPayload;
import com.rohit.springboot.MaverickBank.loanManager.requestPayload.GetLoanRequestPayload;
import com.rohit.springboot.MaverickBank.loanManager.requestPayload.LoanRequestPayload;
import com.rohit.springboot.MaverickBank.repository.AccountRepository;
import com.rohit.springboot.MaverickBank.repository.ApprovedLoanRepository;
import com.rohit.springboot.MaverickBank.repository.LoanRequestsRepository;
import com.rohit.springboot.MaverickBank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/loan")
public class LoanController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    ApprovedLoanRepository approvedLoanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoanRequestsRepository loanRequestsRepository;

    @PostMapping("/request")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<?> requestLoan(@RequestBody LoanRequestPayload request){
        //System.out.println(request);
        try{
            LoanRequest loanRequest =new LoanRequest();

            loanRequest.setAmount(request.getAmount());
            loanRequest.setType(request.getType());
            loanRequest.setTerm(request.getTerm());
            loanRequest.setInterest(request.getInterest());
            loanRequest.setRequesterAccount(accountRepository.findById(request.getAccountid()).get());

            loanRequestsRepository.save(loanRequest);

            return new ResponseEntity<>("Loan requested", HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("some error occured",HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/approve")
    @PreAuthorize("hasAnyAuthority('ADMIN','EMPLOYEE')")
    public ResponseEntity<?> approveLoan(@RequestBody AproveLoanRequestPayload request){
        try{
            LoanRequest loanRequest =loanRequestsRepository.findById(request.getRequestid()).get();

            ApprovedLoan loan =new ApprovedLoan();
            loan.setAmount(loanRequest.getAmount());
            loan.setType(loanRequest.getType());
            loan.setTerm(loanRequest.getTerm());
            loan.setInterest(loanRequest.getInterest());
            loan.setBorrowerAccount(accountRepository.findById(loanRequest.getRequesterAccount().getId()).get());
            loan.setAmountleft(loan.getAmount());
            loan.setStartdate(new Date());
            loan.setBorrower(loan.getBorrower());

            Account account = loanRequest.getRequesterAccount();
            account.getLoanRequests().remove(loanRequest);
            account.setBalance(account.getBalance()+loanRequest.getAmount());
            accountRepository.save(account);
            loanRequest.setRequesterAccount(null);
            loanRequestsRepository.delete(loanRequest);
            approvedLoanRepository.save(loan);

            return new ResponseEntity<>("Loan Aproved", HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("some error occured",HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/getLoanRequest")
    @PreAuthorize("hasAnyAuthority('ADMIN','EMPLOYEE')")
    @ResponseBody
    public ResponseEntity<?> getLoanRequests(@RequestBody GetLoanRequestPayload req){

        if(req.getLoanrequestid()!=null){
            return new ResponseEntity<>(loanRequestsRepository.findById(req.getLoanrequestid()).get(),HttpStatus.OK);
        }

        List<LoanRequest> requests = loanRequestsRepository.findAll();
        return new ResponseEntity<>(requests ,HttpStatus.OK);
    }


    @PutMapping("/deleteLoanRequest")
    @ResponseBody
    public ResponseEntity<?> deleteLoanRequests(@RequestBody GetLoanRequestPayload req){
        System.out.println(req.getLoanrequestid());
        LoanRequest Loanreq =loanRequestsRepository.findById(req.getLoanrequestid()).get();

        Account account = Loanreq.getRequesterAccount();
        account.getLoanRequests().remove(Loanreq);
        accountRepository.save(account);
        Loanreq.setRequesterAccount(null);
        loanRequestsRepository.delete(Loanreq);


        return new ResponseEntity<>("Success" ,HttpStatus.OK);
    }

    @PostMapping("/getUserLoan")
    @ResponseBody
    public ResponseEntity<?> getUserLoan(@RequestBody GetLoanRequestPayload req){

        User user = userRepository.findByEmailOrPhonenumberOrPan(req.getUsername(),req.getUsername(),req.getUsername());

        if(user==null)return new ResponseEntity<>("user not found",HttpStatus.BAD_REQUEST);

        List<Account> accounts = user.getAccounts();
        List<Account> approvedAccounts = new ArrayList<>();
        for(int i= accounts.size()-1;i>=0;i--) {
            if (accounts.get(i).isApproved()) {
                approvedAccounts.add(accounts.get(i));
            }
        }

        if(req.getApproved()){
            List<ApprovedLoan> approvedLoan =new ArrayList<>();
            for(int i=0;i<approvedAccounts.size();i++){
                approvedLoan.addAll(approvedAccounts.get(i).getApprovedLoans());
            }
            return new ResponseEntity<>(approvedLoan,HttpStatus.OK);
        }


        List<LoanRequest> unapprovedLoan =new ArrayList<>();
        for(int i=0;i<approvedAccounts.size();i++){
            unapprovedLoan.addAll( loanRequestsRepository.findAllByRequestorId(approvedAccounts.get(i).getId()));
        }
        return new ResponseEntity<>(unapprovedLoan ,HttpStatus.OK);
    }

    @PostMapping("/getAccountLoan")
    @ResponseBody
    public ResponseEntity<?> getAccountLoan(@RequestBody GetLoanRequestPayload req){

        Account account =accountRepository.findById(req.getAccountId()).get();

        if(req.getApproved()){
            return new ResponseEntity<>(account.getApprovedLoans(),HttpStatus.OK);
        }
        return new ResponseEntity<>(account.getLoanRequests() ,HttpStatus.OK);
    }


    @PostMapping("/getLoan")
    @ResponseBody
    public ResponseEntity<?> getLoan(@RequestBody GetLoanRequestPayload req){

        if(req.getApproved())
        {return new ResponseEntity<>(approvedLoanRepository.findById(req.getApprovedLoanId()).get(),HttpStatus.OK) ;}

        return new ResponseEntity<>(loanRequestsRepository.findById(req.getLoanrequestid()).get(),HttpStatus.OK) ;
    }





}
