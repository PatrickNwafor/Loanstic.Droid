package com.icubed.loansticdroid.models;

import android.app.Application;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.BorrowersTableQueries;

import java.util.HashMap;
import java.util.Map;

public class BorrowersQueries {

    private Account account;
    private FirebaseFirestore firebaseFirestore;

    public BorrowersQueries(){

        account = new Account();
        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    /*******************Add new borrower*************/
    public Task<DocumentReference> addNewBorrower(BorrowersTable borrowersTable){
        return firebaseFirestore.collection("Borrowers").add(borrowersTable);
    }

    /**************Retrieve all borrower***********/
    public Task<QuerySnapshot> retrieveAllBorrowers(){
        return firebaseFirestore.collection("Borrowers").get();
    }

    /**************Retrieve all borrower for a loanOfficer***********/
    public Task<QuerySnapshot> retrieveAllBorrowersForLoanOfficer(){
        return firebaseFirestore.collection("Borrowers")
                .whereEqualTo("loanOfficerId", account.getCurrentUserId())
                .get();
    }

    /**************Retrieve single borrower***********/
    public Task<DocumentSnapshot> retrieveSingleBorrowers(String borrowersId){
        return firebaseFirestore.collection("Borrowers")
                .document(borrowersId)
                .get();
    }

}
