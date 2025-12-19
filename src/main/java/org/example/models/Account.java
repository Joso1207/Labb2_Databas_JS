package org.example.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Set;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String owner_name;
    @Column(nullable = false,precision = 10,scale = 2,columnDefinition = ("DECIMAL(10,2)"))
    private BigDecimal balance;


    public Account(){

    }

    public Account(String owner_name){
        this.owner_name = owner_name;
    }

    public Account(String owner_name,BigDecimal initialBalance){
        this.owner_name = owner_name; this.balance = initialBalance;

    }


    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String printInfo(){
        return "ID#"+id+" Name:"+owner_name+ " Balance:"+balance;
    }
}

