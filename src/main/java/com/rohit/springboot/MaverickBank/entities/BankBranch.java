package com.rohit.springboot.MaverickBank.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;


@Getter
@Setter
@Entity
@Table(name = "bank_branch")
public class BankBranch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String city;

    @Column(unique = true)
    private String ifsc;

    @Column(nullable = false)
    private String pincode;

    @Column(nullable = false,columnDefinition = "Double default 0")
    private Double balance;

}
