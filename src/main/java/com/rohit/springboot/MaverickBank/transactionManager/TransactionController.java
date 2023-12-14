package com.rohit.springboot.MaverickBank.transactionManager;

import com.rohit.springboot.MaverickBank.entities.Account;
import com.rohit.springboot.MaverickBank.entities.Transaction;
import com.rohit.springboot.MaverickBank.entities.User;
import com.rohit.springboot.MaverickBank.repository.AccountRepository;
import com.rohit.springboot.MaverickBank.repository.BankBranchRepository;
import com.rohit.springboot.MaverickBank.repository.TransactionRepository;
import com.rohit.springboot.MaverickBank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private BankBranchRepository bankBranchRepository;
    @GetMapping("/getAll")
    @ResponseBody
    public  List<Transaction> findAllTransactions(){
        return transactionRepository.findAll();
    }

    @PostMapping("/getAccountTransactions")
    @ResponseBody
    public  List<Transaction> getAccountTransaction(@RequestBody TransactionReqDto req){
        List<Transaction> transactions = transactionRepository.findAllByTransactoinOwner(req.getOwner(),req.getCount());

        return transactions;
    }

    @PostMapping("/getUserTransactions")
    @ResponseBody
    public  List<Transaction> getUserTransaction(@RequestBody TransactionReqDto req){
        User user = userRepository.findByEmailOrPhonenumberOrPan(req.getUsername(),req.getUsername(),req.getUsername());

        List<Account> accounts = user.getAccounts();
        List<Account> finalAccounts = new ArrayList<>();
        for(int i= accounts.size()-1;i>=0;i--){
            if(accounts.get(i).isApproved()){
                finalAccounts.add(accounts.get(i));
            }
        }

        List<Transaction> transactions =new ArrayList<>();
        for(int i= 0 ;i<finalAccounts.size();i++) {
            transactions.addAll(transactionRepository.findAllByTransactoinOwner(finalAccounts.get(i).getId(), 100));
        }

        return transactions;
    }

    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<Transaction> getTransaction(@RequestBody TransactionReqDto req){
        return new ResponseEntity<>((transactionRepository.findById(req.getId()).get()),HttpStatus.OK);
    }

    @PostMapping("/send")
    public ResponseEntity<String> transaction(@RequestBody TransactionReqDto transactionReqDto){

        Transaction transaction =new Transaction();
        transaction.setType('T');
        transaction.setAmount(transactionReqDto.getAmount());

        Account ownerAccount = accountRepository.findById(transactionReqDto.getOwner()).get();
        Account receiverAccount = accountRepository.findById(transactionReqDto.getReceiver()).get();

        if(Objects.equals(transactionReqDto.getReceiver(), transactionReqDto.getOwner())){
            return  new ResponseEntity<>("Cannot send money to same account",HttpStatus.OK);
        }

        if(ownerAccount==null){
            return  new ResponseEntity<>("transaction can`t be associated to any account",HttpStatus.OK);
        }

        if(receiverAccount==null){
            return new ResponseEntity<>("Receiver not Found!", HttpStatus.OK);
        }

        transaction.setOwner(ownerAccount.getId());
        transaction.setReceiver(receiverAccount.getId());
        transaction.setTransactionOwner(ownerAccount);
        transaction.setTransactionReceiver(receiverAccount);

        if(!Objects.equals(transactionReqDto.getPin(), ownerAccount.getPin())){
            return new ResponseEntity<>("Wrong pin", HttpStatus.OK);
        }

        if(ownerAccount.getBalance()< transactionReqDto.getAmount()){
            return new ResponseEntity<>("Insufficient Funds", HttpStatus.OK);
        }
        ownerAccount.setBalance(ownerAccount.getBalance() - transactionReqDto.getAmount());

        receiverAccount.setBalance(receiverAccount.getBalance()+ transactionReqDto.getAmount());

        accountRepository.save(ownerAccount);
        accountRepository.save(receiverAccount);
        transactionRepository.save(transaction);
        return  new ResponseEntity<>("Successfull",HttpStatus.OK);
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasAnyAuthority('ADMIN','EMPLOYEE')")
    public ResponseEntity<?> depositMoney(@RequestBody TransactionReqDto req){
        Transaction transaction =new Transaction();

        transaction.setType('D');
        transaction.setAmount(req.getAmount());
        Account ownerAccount = accountRepository.findById(req.getOwner()).get();
        transaction.setTransactionOwner(ownerAccount);
        ownerAccount.setBalance(ownerAccount.getBalance() + req.getAmount());
        transaction.setOwner(ownerAccount.getId());
        transactionRepository.save(transaction);

        return new ResponseEntity<>("Deposit Successfull",HttpStatus.OK);
    }


    @PostMapping("/withdraw")
    public ResponseEntity<?> withdrawMoney(@RequestBody TransactionReqDto req){
        Account ownerAccount = accountRepository.findById(req.getOwner()).get();
        if(ownerAccount.getBalance()<req.getAmount()){
            return new ResponseEntity<>("Low Balance",HttpStatus.OK);
        }

        Transaction transaction =new Transaction();
        transaction.setType('W');
        transaction.setAmount(req.getAmount());
        transaction.setTransactionOwner(ownerAccount);

        if(!Objects.equals(req.getPin(), ownerAccount.getPin())){
            return new ResponseEntity<>("Wrong pin", HttpStatus.OK);
        }
        ownerAccount.setBalance(ownerAccount.getBalance() - req.getAmount());
        transaction.setOwner(ownerAccount.getId());
        transactionRepository.save(transaction);

        return new ResponseEntity<>("Successfull",HttpStatus.OK);
    }


}
