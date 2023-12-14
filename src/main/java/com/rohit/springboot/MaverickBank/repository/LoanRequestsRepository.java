package com.rohit.springboot.MaverickBank.repository;

import com.rohit.springboot.MaverickBank.entities.LoanRequest;
import com.rohit.springboot.MaverickBank.entities.LoanRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface LoanRequestsRepository extends JpaRepository<LoanRequest,Long> {

    @Query(value = "Select * from maverickbank.loan_requests where requester_account=?1",nativeQuery = true)
    Collection<? extends LoanRequest> findAllByRequestorId(Long id);
}
