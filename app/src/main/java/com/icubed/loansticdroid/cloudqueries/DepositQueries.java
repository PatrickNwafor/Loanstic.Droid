package com.icubed.loansticdroid.cloudqueries;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.localdatabase.DepositTable;

import java.util.Map;

public class DepositQueries {
    private FirebaseFirestore firebaseFirestore;

    public DepositQueries(){
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    /****************Create a new payment**************/
    public Task<DocumentReference> makeDeposit(DepositTable depositTable){
        return firebaseFirestore.collection("Deposit").add(depositTable);
    }

    /****************Create a new payment**************/
    public Task<DocumentReference> makeDeposit(Map<String, Object> depositTableMap){
        return firebaseFirestore.collection("Deposit").add(depositTableMap);
    }

    /********************Retrieve Single DepositQueries Details**************/
    public Task<DocumentSnapshot> retieveSingleDeposit(String depositId){
        return firebaseFirestore.collection("Deposit")
                .document(depositId)
                .get();
    }

    /***************Retrieve All Deposits for a loan Details******************/
    public Task<QuerySnapshot> retrieveAllDepositForSavingSchedule(String savingsScheduleId){
        return firebaseFirestore.collection("Deposit")
                .whereEqualTo("savingsScheduleId", savingsScheduleId)
                .get();
    }

    /***************Retrieve All Deposits for a loan Details******************/
    public Task<QuerySnapshot> retrieveAllDepositForSavings(String savingsId){
        return firebaseFirestore.collection("Deposit")
                .whereEqualTo("savingsId", savingsId)
                .get();
    }
}
