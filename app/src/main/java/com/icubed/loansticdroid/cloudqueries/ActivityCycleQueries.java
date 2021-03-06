package com.icubed.loansticdroid.cloudqueries;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.icubed.loansticdroid.localdatabase.ActivityCycleTable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ActivityCycleQueries {

    private FirebaseFirestore firebaseFirestore;

    public ActivityCycleQueries(){

        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public Task<DocumentReference> createNewActivityCycle(Map<String, Object> borrowerGroupMap){
        return firebaseFirestore.collection("Activity_Cycle")
                .add(borrowerGroupMap);
    }

    public Task<DocumentReference> createNewActivityCycle(ActivityCycleTable activityCycleTable){
        return firebaseFirestore.collection("Activity_Cycle")
                .add(activityCycleTable);
    }

    public Task<QuerySnapshot> retrieveAllCycleForBorrower(String borrowerId){
        return firebaseFirestore.collection("Activity_Cycle")
                .whereEqualTo("borrowerId", borrowerId)
                .get();
    }

    public Task<QuerySnapshot> retrieveSingleActiveCycleForBorrower(String borrowerId){
        return firebaseFirestore.collection("Activity_Cycle")
                .whereEqualTo("borrowerId", borrowerId)
                .whereEqualTo("isActive", true)
                .get();
    }

    public Task<QuerySnapshot> retrieveLastCreatedCycleForBorrower(String borrowerId){
        return firebaseFirestore.collection("Activity_Cycle")
                .whereEqualTo("borrowerId", borrowerId)
                .orderBy("startCycleTime", Query.Direction.DESCENDING)
                .limit(1)
                .get();
    }

    public Task<DocumentSnapshot> retrieveSingleCycleForBorrower(String activityCycleId){
        return firebaseFirestore.collection("Activity_Cycle")
                .document(activityCycleId)
                .get();
    }

    public Task<Void> activateBorrower(String activityCycleId){

        Map<String,Object> objectMap = new HashMap<>();
        objectMap.put("isActive", true);
        objectMap.put("endCycleTime", null);


        return firebaseFirestore.collection("Activity_Cycle")
                .document(activityCycleId)
                .set(objectMap, SetOptions.merge());

    }

    public Task<Void> deactivateBorrower(String activityCycleId, Date deactivationDate){

        Map<String,Object> objectMap = new HashMap<>();
        objectMap.put("isActive", false);
        objectMap.put("endCycleTime", deactivationDate);


        return firebaseFirestore.collection("Activity_Cycle")
                .document(activityCycleId)
                .set(objectMap, SetOptions.merge());

    }
    
}
