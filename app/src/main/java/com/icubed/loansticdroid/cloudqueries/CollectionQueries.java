package com.icubed.loansticdroid.cloudqueries;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.icubed.loansticdroid.localdatabase.CollectionTable;
import com.icubed.loansticdroid.util.DateUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CollectionQueries {

    private FirebaseFirestore firebaseFirestore;
    private Account account;

    public CollectionQueries(){
        firebaseFirestore = FirebaseFirestore.getInstance();
        account = new Account();
    }

    /*****************Create new CollectionTable ****************/
    public Task<DocumentReference> createCollection(CollectionTable collectionTable){
        return firebaseFirestore.collection("Collection").add(collectionTable);
    }

    /*****************Create new CollectionTable ****************/
    public Task<DocumentReference> createCollection(Map<String, Object> collectionMap){
        return firebaseFirestore.collection("Collection").add(collectionMap);
    }

    /*****************Retrieve CollectionTable*********************/
    public Task<QuerySnapshot> retrieveAllCollection(){
        return firebaseFirestore.collection("Collection").get();
    }

    /*****************Retrieve CollectionTable for loan*******************/
    public Task<QuerySnapshot> retrieveCollectionsDataForALoanAcending(String loanId){

        return firebaseFirestore.collection("Collection")
                .whereEqualTo("loanId", loanId)
                .orderBy("collectionNumber", Query.Direction.ASCENDING)
                .get();

    }

    /*****************Retrieve CollectionTable for loan*******************/
    public Task<QuerySnapshot> retrieveCollectionsDataForALoan(String loanId){

        return firebaseFirestore.collection("Collection")
                .whereEqualTo("loanId", loanId)
                .get();

    }

    public Task<DocumentSnapshot> retrieveSingleCollection(String collectionId){
        return firebaseFirestore.collection("Collection")
                .document(collectionId)
                .get();
    }

    /*****************Retrieve Due CollectionTable for all officers*******************/
    public Task<QuerySnapshot> retrieveCollectionsDataForAllOfficer(){

        return firebaseFirestore.collection("Collection")
                .whereEqualTo("collectionDueDate", DateUtil.dateString(new Date()))
                .get();

    }

    /*****************Confirm Due CollectionTable*************************/
    public Task<Void> confrimCollection(String collectionId, Date timestamp){
        Map<String, Object> dueCollectedMap = new HashMap<>();

        dueCollectedMap.put("timestamp", timestamp);
        dueCollectedMap.put("isDueCollected", true);

        return firebaseFirestore.collection("Collection").document(collectionId)
                .update(dueCollectedMap);
    }

    public Task<Void> updateCollectionDetails(Map<String, Object> objectMap, String collectionId) {
        return firebaseFirestore.collection("Collection")
                .document(collectionId)
                .set(objectMap, SetOptions.merge());
    }
}
