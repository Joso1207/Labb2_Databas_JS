package org.example.repository;

import org.aspectj.lang.annotation.Before;
import org.example.models.Account;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.AutoConfigureDataJpa;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.BeforeTestExecution;


import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class AccountRepositoryTest {

    @Autowired
    AccountRepository repo;

    @Autowired
    TestEntityManager entityManager;


    @Test
    void LoadContext(){

    }

    @Test
    void saveToRepo(){
        Account newAccount = new Account("Big G",BigDecimal.valueOf(2020.20));
        Account insertedAccount = repo.save(newAccount);
        assertEquals(entityManager.find(Account.class, insertedAccount.getId()), newAccount);

        System.out.println(insertedAccount);

    }

    @Test
    void FindByID(){

    }

    @Test
    void updateAccount(){

    }

    @Test
    void updateBalance(){

    }



}