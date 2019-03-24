package com.icubed.loansticdroid.cloudqueries;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.icubed.loansticdroid.localdatabase.SavingsScheduleTable;
import com.icubed.loansticdroid.util.DateUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SavingsScheduleQueries {

    private FirebaseFirestore firebaseFirestore;
    private Account account;

    public SavingsScheduleQueries(){
        firebaseFirestore = FirebaseFirestore.getInstance();
        account = new Account();
    }

    /*****************Create new SavingsScheduleTable ****************/
    public Task<DocumentReference> createSavingsSchedule(SavingsScheduleTable savingsScheduleTable){
        return firebaseFirestore.collection("Savings_Schedule").add(savingsScheduleTable);
    }

    /*****************Create new SavingsScheduleTable ****************/
    public Task<DocumentReference> createSavingsSchedule(Map<String, Object> collectionMap){
        return firebaseFirestore.collection("Savings_Schedule").add(collectionMap);
    }

    /*****************Retrieve SavingsScheduleTable*********************/
    public Task<QuerySnapshot> retrieveAllSavingsSchedule(){
        return firebaseFirestore.collection("Savings_Schedule").get();
    }

    /*****************Retrieve SavingsScheduleTable for loan*******************/
    public Task<QuerySnapshot> retrieveSavingsSchedulesDataForALoanAcending(String savingsId){

        return firebaseFirestore.collection("Savings_Schedule")
                .whereEqualTo("loanId", savingsId)
                .orderBy("collectionNumber", Query.Direction.ASCENDING)
                .get();

    }

    /*****************Retrieve SavingsScheduleTable for loan*******************/
    public Task<QuerySnapshot> retrieveSavingsSchedulesDataForALoan(String savingsId){

        return firebaseFirestore.collection("Savings_Schedule")
                .whereEqualTo("loanId", savingsId)
                .get();

    }

    public Task<DocumentSnapshot> retrieveSingleSavingsSchedule(String savingsScheduleId){
        return firebaseFirestore.collection("Savings_Schedule")
                .document(savingsScheduleId)
                .get();
    }

    /*****************Retrieve Due SavingsScheduleTable for all officers*******************/
    public Task<QuerySnapshot> retrieveSavingsSchedulesDataForAllOfficer(){

        return firebaseFirestore.collection("Savings_Schedule")
                .whereEqualTo("collectionDueDate", DateUtil.dateString(new Date()))
                .get();

    }

    /*****************Confirm Due SavingsScheduleTable*************************/
    public Task<Void> confrimSavings_Schedule(String savingsScheduleId, Date timestamp){
        Map<String, Object> dueCollectedMap = new HashMap<>();

        dueCollectedMap.put("timestamp", timestamp);
        dueCollectedMap.put("isDueCollected", true);

        return firebaseFirestore.collection("Savings_Schedule").document(savingsScheduleId)
                .update(dueCollectedMap);
    }

    public Task<Void> updateSavingsScheduleDetails(Map<String, Object> objectMap, String savingsScheduleId) {
        return firebaseFirestore.collection("Savings_Schedule")
                .document(savingsScheduleId)
                .set(objectMap, SetOptions.merge());
    }
    
}
