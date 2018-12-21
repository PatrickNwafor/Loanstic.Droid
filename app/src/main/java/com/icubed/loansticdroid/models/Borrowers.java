package com.icubed.loansticdroid.models;

import android.app.Application;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.localdatabase.BorrowersTableQueries;

import java.util.HashMap;
import java.util.Map;

public class Borrowers {

    private Account account;
    private FirebaseFirestore firebaseFirestore;
    private BorrowersTableQueries borrowersTableQueries;

    public Borrowers(Application application){

        account = new Account();
        firebaseFirestore = FirebaseFirestore.getInstance();
        borrowersTableQueries = new BorrowersTableQueries(application);

    }

    /*******************Add new borrower*************/
    public Task<DocumentReference> addNewBorrower(String name, String business){

        Map<String, Object> borrowersMap = new HashMap<>();
        borrowersMap.put("name", name);
        borrowersMap.put("business", business);

        return firebaseFirestore.collection("Borrowers").add(borrowersMap);
    }

    /**************Retrieve all borrower***********/
    public Task<QuerySnapshot> retrieveAllBorrowers(){
        return firebaseFirestore.collection("Borrowers").get();
    }

    /**************Retrieve single borrower***********/
    public Task<DocumentSnapshot> retrieveSingleBorrowers(String borrowersId){
        return firebaseFirestore.collection("Borrowers")
                .document(borrowersId)
                .get();
    }

}
