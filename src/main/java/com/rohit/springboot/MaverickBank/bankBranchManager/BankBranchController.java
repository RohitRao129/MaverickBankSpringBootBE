package com.rohit.springboot.MaverickBank.bankBranchManager;


import com.rohit.springboot.MaverickBank.bankBranchManager.BankBranchDto;
import com.rohit.springboot.MaverickBank.entities.Account;
import com.rohit.springboot.MaverickBank.entities.BankBranch;
import com.rohit.springboot.MaverickBank.repository.AccountRepository;
import com.rohit.springboot.MaverickBank.repository.BankBranchRepository;
import org.hibernate.query.spi.Limit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/branch")
public class BankBranchController {
    @Autowired
    private BankBranchRepository bankBranchRepository;

    @Autowired
    private AccountRepository accountRepository;
    @PostMapping("/get")
    public List<BankBranch> getbankbranch(@RequestBody BankBranchDto req){

        if(req.getCity()== null && req.getIfsc()==null && req.getPincode()==null){
            if(req.getCount()==-1){
                return  bankBranchRepository.findAll();
            }
            else {
                return  bankBranchRepository.findAllByLim(req.getCount());
            }
        }
        if(req.getCity()!=null){req.setCity(req.getCity().toUpperCase());}
        if(req.getIfsc()!=null){req.setIfsc(req.getIfsc().toUpperCase());}
        return bankBranchRepository.findAllByCityOrIfscOrPincode( req.getCity(), req.getIfsc(), req.getPincode());
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ADMIN','EMPLOYEE')")
    public ResponseEntity<String> addBankBranch(@RequestBody BankBranchDto bankBranchDto){

            if(bankBranchRepository.findByPincode(bankBranchDto.getPincode())!=null){
                return  new ResponseEntity<>("Another branch present there",HttpStatus.OK);
            }

            BankBranch newBranch =new BankBranch();
            newBranch.setCity(bankBranchDto.getCity().toUpperCase());
            newBranch.setPincode(bankBranchDto.getPincode());
            newBranch.setBalance(0d);
            BankBranch createdBranch = bankBranchRepository.save(newBranch);
            createdBranch.setIfsc("BKID"+bankBranchDto.getCity().substring(0,2).toUpperCase()+ createdBranch.getId());
            bankBranchRepository.save(createdBranch);

            return new ResponseEntity<>("Bank Added", HttpStatus.OK);

    }


    @PostMapping("/remove")
    @PreAuthorize("hasAnyAuthority('ADMIN','EMPLOYEE')")
    public ResponseEntity<String> removeBankBranch(@RequestBody BankBranchDto bankBranchDto){
        BankBranch branch = bankBranchRepository.findByIfsc(bankBranchDto.getIfsc());
        if(branch==null){
            return  new ResponseEntity<>("no branch Found!",HttpStatus.OK);
        }

        BankBranch newBranch = bankBranchRepository.findByIfsc(bankBranchDto.getTransferifsc());
        if(newBranch==null){
            return  new ResponseEntity<>("Please transfer accounts to other branch!",HttpStatus.OK);
        }

        List<Account> accounts = accountRepository.findAllByIfsc(bankBranchDto.getIfsc());
        for(int i=0;i<accounts.size();i++){
            accounts.get(i).setIfsc(bankBranchDto.getTransferifsc());
            accountRepository.save(accounts.get(i));
        }

        bankBranchRepository.delete(branch);

        return new ResponseEntity<>("Branch removed successfully",HttpStatus.OK);
    }
}
