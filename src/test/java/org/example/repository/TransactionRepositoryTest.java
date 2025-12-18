package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.models.Account;
import org.example.models.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TransactionRepositoryTest {


    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    EntityManager entityManager;


    @Test
    void LoadContext(){

    }

    @Test
    void shouldSaveTransactionLog(){

        Account acc1 = new Account("Account a", BigDecimal.valueOf(2.50));
        Account acc2 = new Account("Account b",BigDecimal.valueOf(3.20));
        accountRepository.saveAll(List.of(acc1,acc2));

        List<Account> accounts = accountRepository.findAllById(List.of(1L,2L));
        Transaction newTransaction = new Transaction(BigDecimal.valueOf(200.0));


        newTransaction.setFromAccount(accounts.get(0));
        newTransaction.setToAccount(accounts.get(1));

        Transaction savedTransaction = transactionRepository.save(newTransaction);
        assertEquals(entityManager.find(Transaction.class,savedTransaction.getID()),newTransaction);

    }

    @Test
    void shouldFindTransactionByIDAndIdentifyAccounts(){


        List<Account> accounts = accountRepository.findAllById(List.of(1L,2L));
        Transaction newTransaction = new Transaction(BigDecimal.valueOf(200.0));

        newTransaction.setFromAccount(accounts.get(0));
        newTransaction.setToAccount(accounts.get(1));

        System.out.println(newTransaction.printInfo());

        Transaction savedTransaction = transactionRepository.save(newTransaction);

        Optional<Transaction> fetchedTransaction = transactionRepository.findById(savedTransaction.getID());

        assertTrue(fetchedTransaction.isPresent());
        System.out.println(fetchedTransaction.get().printInfo());
        assertEquals(entityManager.find(Transaction.class,savedTransaction.getID()),newTransaction);






    }



}