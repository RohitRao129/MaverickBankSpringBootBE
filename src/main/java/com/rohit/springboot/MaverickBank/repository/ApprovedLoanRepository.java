package com.rohit.springboot.MaverickBank.repository;

import com.rohit.springboot.MaverickBank.entities.ApprovedLoan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovedLoanRepository extends JpaRepository<ApprovedLoan,Long> {
}
