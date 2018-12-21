package com.icubed.loansticdroid.models;

import android.app.Application;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.localdatabase.BorrowersTableQueries;
import com.icubed.loansticdroid.localdatabase.LoanTableQueries;

import java.util.HashMap;
import java.util.Map;

public class Loans {

    private Account account;
    private FirebaseFirestore firebaseFirestore;
    private LoanTableQueries loanTableQueries;

    public Loans(Application application){

        account = new Account();
        firebaseFirestore = FirebaseFirestore.getInstance();
        loanTableQueries = new LoanTableQueries(application);

    }

    /**************Retrieve all Loans***********/
    public Task<QuerySnapshot> retrieveAllLoans(){
        return firebaseFirestore.collection("Loan").get();
    }

    /**************Retrieve single Loan***********/
    public Task<DocumentSnapshot> retrieveSingleLoan(String loanId){
        return firebaseFirestore.collection("Loan")
                .document(loanId)
                .get();
    }

}
