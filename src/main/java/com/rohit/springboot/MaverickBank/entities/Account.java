package com.rohit.springboot.MaverickBank.entities;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false)
    private boolean approved =false;


    @Column(nullable = false,updatable = false)
    @Temporal(TemporalType.DATE)
    @CreatedDate
    private Date creationdate;

    @Column()
    @Temporal(TemporalType.DATE)
    private Date closedate;

    @Column(nullable = false)
    private String ifsc;

    @Column(nullable = false)
    private String branch;

    @Column(nullable = false,columnDefinition = "float default 0")
    private Float balance;

    @Column(nullable = false,length = 4)
    @JsonIgnore
    private String pin;

    @ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinTable(
            name = "accounts_users",
            joinColumns = {@JoinColumn(name = "account_id",referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name ="user_id",referencedColumnName = "ID")})
    private List<User> accountOwners =new ArrayList<>();

    //mapped by is the name of variable your made in other table you're mapping.
    @OneToMany(mappedBy = "transactionOwner",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Transaction> transactionsOwner;


    @OneToMany(mappedBy = "transactionReceiver",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Transaction> transactionsReceiver;



    @OneToMany(mappedBy = "requesterAccount",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<LoanRequest> loanRequests;


    @OneToMany(mappedBy = "borrowerAccount",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ApprovedLoan> approvedLoans;



}
