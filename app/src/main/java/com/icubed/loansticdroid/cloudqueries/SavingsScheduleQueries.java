package com.icubed.loansticdroid.cloudqueries;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.icubed.loansticdroid.localdatabase.SavingsScheduleTable;

import java.util.Map;

public class SavingsScheduleQueries {
    private Account account;
    private FirebaseFirestore firebaseFirestore;

    public SavingsScheduleQueries(){
        account = new Account();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public Task<DocumentReference> createSavingsSchedule(SavingsScheduleTable savingsScheduleTable){
        return firebaseFirestore.collection("Savings_Schedule").add(savingsScheduleTable);
    }

    public Task<DocumentReference> createSavingsSchedule(Map<String, Object> loanMap){
        return firebaseFirestore.collection("Savings_Schedule").add(loanMap);
    }

    /**************Retrieve all Savings_SchedulesQueries***********/
    public Task<QuerySnapshot> retrieveAllSavingsSchedule(){
        return firebaseFirestore.collection("Savings_Schedule").get();
    }

    /**************Retrieve single Savings_Schedule***********/
    public Task<DocumentSnapshot> retrieveSingleSavingsSchedule(String savingsId){
        return firebaseFirestore.collection("Savings_Schedule")
                .document(savingsId)
                .get();
    }

    public Task<QuerySnapshot> retrieveSavingsScheduleForLoanOfficer(){
        return firebaseFirestore.collection("Savings_Schedule")
                .whereEqualTo("loanOfficerId", account.getCurrentUserId())
                .get();
    }

    public Task<Void> updateSavingsScheduleDetails(Map<String, Object> objectMap, String savingsId){
        return firebaseFirestore.collection("Savings_Schedule")
                .document(savingsId)
                .set(objectMap, SetOptions.merge());

    }
}
