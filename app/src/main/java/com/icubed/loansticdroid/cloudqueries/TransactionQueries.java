package com.icubed.loansticdroid.cloudqueries;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.localdatabase.TransactionTable;

import java.util.Map;

public class TransactionQueries {

    private FirebaseFirestore firebaseFirestore;
    
    public TransactionQueries(){
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    /****************Create a new payment**************/
    public Task<DocumentReference> createSavingsTransactions(TransactionTable transactionTable){
        return firebaseFirestore.collection("Savings_Transactions").add(transactionTable);
    }

    /****************Create a new payment**************/
    public Task<DocumentReference> createSavingsTransactions(Map<String, Object> transactionMap){
        return firebaseFirestore.collection("Savings_Transactions").add(transactionMap);
    }

    /********************Retrieve Single Savings_TransactionsQueries Details**************/
    public Task<DocumentSnapshot> retieveSingleSavingsTransactions(String transactionId){
        return firebaseFirestore.collection("Savings_Transactions")
                .document(transactionId)
                .get();
    }

    /***************Retrieve All Savings_Transactionss for a loan Details******************/
    public Task<QuerySnapshot> retrieveAllSavingsTransactionsForSavings(String savingsId){
        return firebaseFirestore.collection("Savings_Transactions")
                .whereEqualTo("savingsId", savingsId)
                .get();
    }

    /***************Retrieve All Savings_Transactionss for a loan Details******************/
    public Task<QuerySnapshot> retrieveAllDebitTransactions(){
        return firebaseFirestore.collection("Savings_Transactions")
                .whereEqualTo("transactionType", TransactionTable.TYPE_DEBIT)
                .get();
    }

    /***************Retrieve All Savings_Transactionss for a loan Details******************/
    public Task<QuerySnapshot> retrieveAllCreditTransactions(){
        return firebaseFirestore.collection("Savings_Transactions")
                .whereEqualTo("transactionType", TransactionTable.TYPE_CREDIT)
                .get();
    }
}
