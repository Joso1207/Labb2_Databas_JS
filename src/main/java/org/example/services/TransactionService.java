package org.example.services;

import org.example.models.Account;
import org.example.models.Transaction;
import org.example.repository.AccountRepository;
import org.example.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    TransactionFailureLogger logger = new TransactionFailureLogger();

    public Optional<Transaction> getTransactionFromID(Long id){
        return transactionRepository.findById(id);
    }
    public List<Transaction> getAllTransactions(){
        return transactionRepository.findAll();
    }

    @Transactional
    public Transaction transfer(Long fromAccountID, Long toAccountID, BigDecimal amount){

        Transaction newTransaction = null;

        try {

            if(Objects.equals(fromAccountID, toAccountID)){
                throw new IllegalArgumentException("Cannot create transfers from account to itself");
            }

            if(amount.compareTo(BigDecimal.valueOf(0.0))<1){
                throw new IllegalArgumentException("Can only accept non-zero positive values");
            }

            Account toAccount = accountRepository.findById(toAccountID)
                    .orElseThrow(()-> new IllegalArgumentException("To account not found"));
            
            Account fromAccount = accountRepository.findById(fromAccountID)
                    .orElseThrow(()-> new IllegalArgumentException("From account not found"));
            
            
            if(amount.compareTo(fromAccount.getBalance()) > 0){
                throw new IllegalStateException("Insufficient Balance");
            }

            newTransaction = new Transaction(fromAccount,toAccount,amount);

            accountRepository.updateBalanceOnAccountWithID(toAccountID,toAccount.getBalance().add(amount));
            accountRepository.updateBalanceOnAccountWithID(fromAccountID,fromAccount.getBalance().subtract(amount));
            return transactionRepository.save(newTransaction);
        }
        catch(IllegalArgumentException | IllegalStateException e){
            //These exceptions occur before we actually attempt to insert into the database,
            // So keeping the error clientside only makes sense, We don't want to log faulty inputs made by every user as a remote thing
            throw e;
        } catch (Exception E){
            //Any other exceptions typically come as a result of system or database errors,
            //At which point we have likely already told the database to save a transaction
            // and thus progressed the ID-Sequence, We should save this to keep the transaction ids as a continuous sequence
            logger.onTransferFailure(newTransaction,E.getMessage());
            throw E;
        }

    }

    @Transactional
    public Transaction transferWithFailure(Long fromAccountID, Long toAccountID, BigDecimal amount) throws Exception{

        Transaction newTransaction = null;

        try {

            if(Objects.equals(fromAccountID, toAccountID)){
                throw new IllegalArgumentException("Cannot create transfers from account to itself");
            }

            if(amount.compareTo(BigDecimal.valueOf(0.0))<1){
                throw new IllegalArgumentException("Can only accept non-zero positive values");
            }

            Account toAccount = accountRepository.findById(toAccountID)
                    .orElseThrow(()-> new IllegalArgumentException("To account not found"));

            Account fromAccount = accountRepository.findById(fromAccountID)
                    .orElseThrow(()-> new IllegalArgumentException("From account not found"));


            if(amount.compareTo(fromAccount.getBalance()) > 0){
                throw new IllegalStateException("Insufficient Balance");
            }

            newTransaction = new Transaction(fromAccount,toAccount,amount);

            accountRepository.updateBalanceOnAccountWithID(toAccountID,toAccount.getBalance().add(amount));
            accountRepository.updateBalanceOnAccountWithID(fromAccountID,fromAccount.getBalance().subtract(amount));
            throw new RuntimeException("Something is wrong");
            //return transactionRepository.save(newTransaction);
        }
        catch(IllegalArgumentException | IllegalStateException e){
            //These exceptions occur before we actually attempt to insert into the database,
            // So keeping the error clientside only makes sense, We don't want to log faulty inputs made by every user as a remote thing
            throw e;
        } catch (Exception E){
            //Any other exceptions typically come as a result of system or database errors,
            //At which point we have likely already told the database to save a transaction
            // and thus progressed the ID-Sequence, We should save this to keep the transaction ids as a continuous sequence
            logger.onTransferFailure(newTransaction,E.getMessage());
            throw E;
        }

    }



}
