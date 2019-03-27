package com.icubed.loansticdroid.cloudqueries;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.icubed.loansticdroid.localdatabase.SavingsPlanCollectionTable;
import com.icubed.loansticdroid.util.DateUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SavingsScheduleCollectionQueries {

    private FirebaseFirestore firebaseFirestore;
    private Account account;

    public SavingsScheduleCollectionQueries(){
        firebaseFirestore = FirebaseFirestore.getInstance();
        account = new Account();
    }

    /*****************Create new SavingsPlanCollectionTable ****************/
    public Task<DocumentReference> createSavingsScheduleCollection(SavingsPlanCollectionTable savingsScheduleCollectionTable){
        return firebaseFirestore.collection("Savings_Plan_Collection").add(savingsScheduleCollectionTable);
    }

    /*****************Create new SavingsPlanCollectionTable ****************/
    public Task<DocumentReference> createSavingsScheduleCollection(Map<String, Object> collectionMap){
        return firebaseFirestore.collection("Savings_Plan_Collection").add(collectionMap);
    }

    /*****************Retrieve SavingsPlanCollectionTable*********************/
    public Task<QuerySnapshot> retrieveAllSavingsScheduleCollection(){
        return firebaseFirestore.collection("Savings_Plan_Collection").get();
    }

    /*****************Retrieve SavingsPlanCollectionTable for loan*******************/
    public Task<QuerySnapshot> retrieveSavingsSchedulesCollectionDataForALoanAcending(String savingsScheduleId){

        return firebaseFirestore.collection("Savings_Plan_Collection")
                .whereEqualTo("loanId", savingsScheduleId)
                .orderBy("collectionNumber", Query.Direction.ASCENDING)
                .get();

    }

    /*****************Retrieve SavingsPlanCollectionTable for loan*******************/
    public Task<QuerySnapshot> retrieveSavingsSchedulesCollectionDataForALoan(String savingsScheduleId){

        return firebaseFirestore.collection("Savings_Plan_Collection")
                .whereEqualTo("loanId", savingsScheduleId)
                .get();

    }

    public Task<DocumentSnapshot> retrieveSingleSavingsScheduleCollection(String savingsScheduleCollectionId){
        return firebaseFirestore.collection("Savings_Plan_Collection")
                .document(savingsScheduleCollectionId)
                .get();
    }

    /*****************Retrieve Due SavingsPlanCollectionTable for all officers*******************/
    public Task<QuerySnapshot> retrieveSavingsSchedulesDataForAllOfficer(){

        return firebaseFirestore.collection("Savings_Plan_Collection")
                .whereEqualTo("collectionDueDate", DateUtil.dateString(new Date()))
                .get();

    }

    /*****************Confirm Due SavingsPlanCollectionTable*************************/
    public Task<Void> confrimSavingsScheduleCollection(String savingsScheduleCollectionId, Date timestamp){
        Map<String, Object> dueCollectedMap = new HashMap<>();

        dueCollectedMap.put("timestamp", timestamp);
        dueCollectedMap.put("isDueCollected", true);

        return firebaseFirestore.collection("Savings_Plan_Collection").document(savingsScheduleCollectionId)
                .update(dueCollectedMap);
    }

    public Task<Void> updateSavingsScheduleDetails(Map<String, Object> objectMap, String savingsScheduleId) {
        return firebaseFirestore.collection("Savings_Plan_Collection")
                .document(savingsScheduleId)
                .set(objectMap, SetOptions.merge());
    }
    
}
