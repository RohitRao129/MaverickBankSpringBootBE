package com.rohit.springboot.MaverickBank.repository;

import com.rohit.springboot.MaverickBank.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    @Query(value="Select * from maverickbank.transactions where owner_account =?1",nativeQuery = true)
    List<Transaction> findAllByTransactoinOwner(Long owner,Integer count);
}
