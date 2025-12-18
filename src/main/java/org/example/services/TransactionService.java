package org.example.services;

import jakarta.transaction.Transactional;
import org.example.models.Account;
import org.example.models.Transaction;
import org.example.repository.AccountRepository;
import org.example.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.math.BigDecimal;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    TransactionFailureLogger logger = new TransactionFailureLogger();

    @Transactional
    public void transfer(Long fromAccountID, Long toAccountID, BigDecimal amount){


        try {

            if(amount.compareTo(BigDecimal.valueOf(0.0))<1){
                throw new IllegalArgumentException("Can only accept non-zero positive values");
            }

            Account toAccount = accountRepository.findById(toAccountID)
                    .orElseThrow(()-> new IllegalArgumentException("To account not found"));

            Account fromAccount = accountRepository.findById(fromAccountID)
                    .orElseThrow(()-> new IllegalArgumentException("From account not found"));

            if(amount.compareTo(fromAccount.getBalance())<1){
                throw new IllegalStateException("Insufficient Balance");
            }

            Transaction newTransaction = new Transaction(fromAccount,toAccount,amount);

            accountRepository.updateBalanceOnAccountWithID(toAccountID,toAccount.getBalance().add(amount));
            accountRepository.updateBalanceOnAccountWithID(fromAccountID,fromAccount.getBalance().subtract(amount));
            transactionRepository.save(newTransaction);
        }
        catch (Exception E){
            logger.onTransferFailure();
        }

    }



}
