package com.rohit.springboot.MaverickBank.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "loan_requests")
public class LoanRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "requesterAccount",nullable = false)
    private Account requesterAccount;


    @Column(nullable = false)
    private float amount;

    @Column(nullable = false)
    private Integer term;

    @Column(nullable = false)
    private float interest;

    @Column(nullable = false)
    private String type;
}
