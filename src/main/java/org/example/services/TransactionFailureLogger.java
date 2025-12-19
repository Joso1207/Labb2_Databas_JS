package org.example.services;

import org.example.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransactionFailureLogger {

    @Autowired
    TransactionRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onTransferFailure(Long fromAccountID, Long toAccountID, BigDecimal amount,String err_msg){



    }
}
