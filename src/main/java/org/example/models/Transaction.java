package org.example.models;

import jakarta.persistence.*;


import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    @Column(nullable = false,precision = 10,scale = 2, columnDefinition = "DECIMAL(10,2) CHECK(amount>0)")
    private BigDecimal amount;
    @Column(nullable = false,columnDefinition = "VARCHAR(10) CHECK(status='SUCCESS' OR status='FAILED') DEFAULT 'SUCCESS'")
    private String status = "SUCCESS";
    @Column(name = "error_msg")
    private String error_message;

    @Column(nullable = false,columnDefinition = "DATE CHECK(created_at<=now()) DEFAULT now() ")
    private Date created_at = Date.valueOf(LocalDate.now());

    @ManyToOne
    private Account toAccount;
    @ManyToOne
    private Account fromAccount;

    public Transaction(){
    }

    public Transaction(Account fromAccount,Account toAccount,BigDecimal amount){
        this.amount = amount;
        this.toAccount = toAccount;
        this.fromAccount = fromAccount;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }


    public Account getToAccount() {
        return toAccount;
    }

    public void setToAccount(Account toAccount) {
        this.toAccount = toAccount;
    }

    public Account getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(Account fromAccount) {
        this.fromAccount = fromAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public String printInfo(){
        return "ID#"+ID+"Amount:"+amount +" FROM:"+fromAccount.getId()+" TO " + toAccount.getId();
    }

}
