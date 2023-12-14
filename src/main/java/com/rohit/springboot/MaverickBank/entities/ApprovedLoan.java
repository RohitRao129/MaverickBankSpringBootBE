package com.rohit.springboot.MaverickBank.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "approved_loans")
public class ApprovedLoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Temporal(TemporalType.DATE)
    @CreatedDate
    private Date startdate;
    
    @Column(nullable = false)
    private Long borrower;


    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "borrowerAccount",nullable = false)
    @JsonIgnore
    private Account borrowerAccount;


    @Column(nullable = false)
    private float amount;

    @Column(nullable = false)
    private Float amountleft;


    @Column(nullable = false)
    private Integer term;

    @Column(nullable = false)
    private float interest;

    @Column(nullable = false)
    private String type;
}
