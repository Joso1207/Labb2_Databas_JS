package org.example.services;

import org.example.models.Account;
import org.example.models.Transaction;
import org.example.repository.AccountRepository;
import org.example.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionFailureLogger {

    @Autowired
    TransactionRepository transactionRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onTransferFailure(Transaction failedTransaction, String err_msg){

        failedTransaction.setStatus("FAILED");
        failedTransaction.setError_message(err_msg);
        transactionRepository.save(failedTransaction);

    }
}
