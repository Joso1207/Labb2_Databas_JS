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


import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.List;
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

        System.out.println(insertedAccount.printInfo());

    }

    @Test
    void FindByID(){
        //Sequence still at nextid = 2  due to earlier test
        Account newAccount = new Account("Big A",BigDecimal.valueOf(600.20));
        Account savedAcc = repo.save(newAccount);
        Optional<Account> fetchedAccount = repo.findById(savedAcc.getId());

        assertTrue(fetchedAccount.isPresent());
        assertEquals(fetchedAccount.get().getOwner_name(), newAccount.getOwner_name());
        System.out.println(fetchedAccount.get().printInfo());

    }

    @Test
    void updateAccount(){

        Optional<Account> accountToUpdate = repo.findById(1L);
        assertTrue(accountToUpdate.isPresent());

        accountToUpdate.get().setOwner_name("Updated");
        accountToUpdate.get().setBalance(BigDecimal.valueOf(200000.0));

        Account updatedAcc = repo.save(accountToUpdate.get());
        assertEquals(entityManager.find(Account.class,updatedAcc.getId()), accountToUpdate.get());

    }

    @Test
    void updateBalance() {

        Optional<Account> fetchedAccount = repo.findById(1L);
        assertTrue(fetchedAccount.isPresent());
        BigDecimal expectedBalance = fetchedAccount.get().getBalance();

        repo.updateBalanceOnAccountWithID(1L, expectedBalance.add(BigDecimal.valueOf(2000)));
        Account updatedAccount = entityManager.find(Account.class, 1L);

        assertEquals(expectedBalance, updatedAccount.getBalance());

    }



}