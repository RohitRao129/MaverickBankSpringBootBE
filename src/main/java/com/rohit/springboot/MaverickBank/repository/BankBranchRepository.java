package com.rohit.springboot.MaverickBank.repository;

import com.rohit.springboot.MaverickBank.entities.BankBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BankBranchRepository extends JpaRepository<BankBranch,Long> {
    List<BankBranch> findAllByCityOrIfsc(String query, String query1);

    BankBranch findByIfsc(String ifsc);

    BankBranch findByPincode(String pincode);

    List<BankBranch> findAllByCityOrIfscOrPincode(String city, String ifsc, String pincode);

    @Query(value = "select * from maverickbank.bank_branch Limit ?1 ;",nativeQuery = true)
    List<BankBranch> findAllByLim(Integer count);
}
