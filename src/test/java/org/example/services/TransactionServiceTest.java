package org.example.services;

import org.example.models.Account;
import org.example.models.Transaction;
import org.example.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SuppressWarnings("BigDecimalMethodWithoutRoundingCalled")
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TransactionServiceTest {


    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionService service;

    @Test
    void successfulTransfer(){
        //Given account A with balance 1000.0 and account B with balance 500.0
        Account a = new Account("A", BigDecimal.valueOf(1000.0));
        Account b = new Account("B",BigDecimal.valueOf(500.0));

        List<Account> accounts = accountRepository.saveAll(List.of(a,b));


        //When Transfer
        Transaction newTransaction = service.transfer(accounts.getFirst().getId(),accounts.getLast().getId(),BigDecimal.valueOf(200.00));

        //Then

            //New balance should be 800 and 700 ((Setting scale since BigDecimal turns 800.00 to 800.0 and detects that as an inequality, Probably because default scale = 1 but I set it to 2 in the DB and JPA entities))
        assertEquals(BigDecimal.valueOf(800.00).setScale(2),accountRepository.findById(accounts.getFirst().getId()).get().getBalance());
        assertEquals(BigDecimal.valueOf(700.00).setScale(2),accountRepository.findById(accounts.getLast().getId()).get().getBalance());


            //Transaction should be logged in database as success
        Optional<Transaction> fetchedTransaction = service.getTransactionFromID(newTransaction.getID());
        assertTrue(fetchedTransaction.isPresent());
        assertEquals("SUCCESS",fetchedTransaction.get().getStatus());

            //Ensure the correct AccountIDs and amount are in
        assertEquals(accounts.getFirst().getId(),fetchedTransaction.get().getFromAccount().getId());
        assertEquals(accounts.getLast().getId(),fetchedTransaction.get().getToAccount().getId());
        assertEquals(BigDecimal.valueOf(200.00),fetchedTransaction.get().getAmount());
    }

    @Test
    void insufficientBalance() {
        //Given
        Account a = new Account("A", BigDecimal.valueOf(100.00).setScale(2));
        Account b = new Account("B", BigDecimal.valueOf(500.00).setScale(2));

        List<Account> accounts = accountRepository.saveAll(List.of(a, b));

        //When
        Transaction shouldBeNull = null;
        try {
            shouldBeNull = service.transfer(accounts.getFirst().getId(), accounts.getLast().getId(), BigDecimal.valueOf(500.00));
            fail(); // THE SERVICE.TRANSFER SHOULD THROW IN THIS SCENARIO, ANYTHING ELSE IS UNEXPECTED BEHAVIOR
        } catch (Exception e) {
            assertInstanceOf(IllegalStateException.class, e);
        }

        //then
        assertEquals(a.getBalance(), accountRepository.findById(accounts.getFirst().getId()).get().getBalance());
        assertEquals(b.getBalance(), accountRepository.findById(accounts.getLast().getId()).get().getBalance());
        assertNull(shouldBeNull);
        //My choice has been to not log these kinds of errors that are a result of bad input,
        //So which means that on IllegalStateException or IllegalArgumentException the failurelogger never writes to the DB,
        //User still obviously get the exception Error message.

        //IF we check the balance and see that its ok,  and another instance removes enough to put us under,
        //Then we would end up violating a DB-Constraint on account.balance and that gives us other exceptions where we write the failed log to DB.
    }

    @Test
    void NegativeAmountTransfer(){
        //given
        Account a = new Account("A", BigDecimal.valueOf(100.00).setScale(2));
        Account b = new Account("B", BigDecimal.valueOf(500.00).setScale(2));

        List<Account> accounts = accountRepository.saveAll(List.of(a, b));

        //when

        Transaction shouldBeNull = null;
        try{
            shouldBeNull = service.transfer(accounts.getFirst().getId(),accounts.getLast().getId(),BigDecimal.valueOf(-20.00));
            fail();
        } catch (Exception e) {
            assertInstanceOf(IllegalArgumentException.class,e);
        }
        //then
        assertNull(shouldBeNull);
        //once more,  User errors should be logged or displayed locally,
        //Actual system errors related to system or database exceptions should be logged at the DB
        List<Account> postTransferAccounts = accountRepository.findAllById(List.of(a.getId(),b.getId()));

        assertEquals(a.getBalance(),postTransferAccounts.getFirst().getBalance());
        assertEquals(b.getBalance(),postTransferAccounts.getLast().getBalance());

    }

    @Test
    void shouldNotAcceptSameAccountTransfer(){
        //given
        Account a = new Account("A", BigDecimal.valueOf(100.00).setScale(2));

        Account savedAccount = accountRepository.save(a);

        //when

        Transaction shouldBeNull = null;
        try{
            shouldBeNull = service.transfer(savedAccount.getId(), savedAccount.getId(), BigDecimal.valueOf(20.00));
            fail();
        } catch (Exception e) {
            assertInstanceOf(IllegalArgumentException.class,e);
        }
        //then
        assertNull(shouldBeNull);
        //once more,  User errors should be logged or displayed locally,
        //Actual system errors related to system or database exceptions should be logged at the DB
        assertEquals(a.getBalance(),accountRepository.findById(savedAccount.getId()).get().getBalance());
    }

    @Test
    void onDatabaseErrorWriteLog(){

        //given
        Account a = new Account("A", BigDecimal.valueOf(100.00).setScale(2));
        Account b = new Account("B", BigDecimal.valueOf(500.00).setScale(2));

        List<Account> accounts = accountRepository.saveAll(List.of(a, b));

        //when
        try{
            service.transferWithFailure(
                    accounts.getFirst().getId(),
                    accounts.getLast().getId(),
                    BigDecimal.valueOf(20.20));
            fail();
        } catch (Exception e) {
            assertInstanceOf(RuntimeException.class,e);
        }
        //then

        Transaction lastTransaction = service.getAllTransactions().getLast();
        List<Account> postTransferAccounts = accountRepository.findAllById(List.of(a.getId(),b.getId()));

        assertEquals(a.getBalance(),postTransferAccounts.getFirst().getBalance());
        assertEquals(b.getBalance(),postTransferAccounts.getLast().getBalance());

        System.out.println("BEFORE");
        accounts.forEach(o->System.out.println(o.printInfo()));
        System.out.println("AFTER");
        System.out.println(postTransferAccounts.getFirst().printInfo());
        System.out.println(postTransferAccounts.getLast().printInfo());
        System.out.println("TRANSACTION");
        System.out.println(lastTransaction.printInfo());

        assertEquals(lastTransaction.getFromAccount().getId(),postTransferAccounts.getFirst().getId());
        assertEquals(lastTransaction.getToAccount().getId(),postTransferAccounts.getLast().getId());
        assertEquals("FAILED",lastTransaction.getStatus());




    }






}