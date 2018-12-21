package com.icubed.loansticdroid.models;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.models.Account;

import java.text.SimpleDateFormat;
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

    /*****************Set CollectionTable Details****************/
    public Task<DocumentReference> sendCollectionsDetails(String loanId, int collectionNumber,
                                                          double collectionDueAmount, String collectionDueDate){

        Date timestamp = new Date();

        Map<String, Object> collectionMap = new HashMap<>();
        collectionMap.put("loanId", loanId);
        collectionMap.put("collectionNumber", collectionNumber);
        collectionMap.put("collectionDueAmount", collectionDueAmount);
        collectionMap.put("collectionDueDate", collectionDueDate);
        collectionMap.put("timestamp", timestamp);
        collectionMap.put("isDueCollected", false);

        return firebaseFirestore.collection("Collection").add(collectionMap);

    }

    /*****************Retrieve CollectionTable*********************/
    public Task<QuerySnapshot> retrieveAllCollection(){
        return firebaseFirestore.collection("Collection").get();
    }

    /*****************Retrieve CollectionTable for loan*******************/
    public Task<QuerySnapshot> retrieveCollectionsDataForALoan(String loanId){

        return firebaseFirestore.collection("Collection")
                .whereEqualTo("loanId", loanId)
                .get();

    }

    /*****************Retrieve Due CollectionTable for all officers*******************/
    public Task<QuerySnapshot> retrieveDueCollectionsDataForAllOfficer(){

        return firebaseFirestore.collection("Collection")
                .whereEqualTo("collectionDueDate", dateString(new Date()))
                .get();

    }

    /*****************Confirm Due CollectionTable*************************/
    public Task<Void> confrimDueCollection(String collectionId, Date timestamp){
        Map<String, Object> dueCollectedMap = new HashMap<>();

        dueCollectedMap.put("timestamp", timestamp);
        dueCollectedMap.put("isDueCollected", true);

        return firebaseFirestore.collection("Collection").document(collectionId)
                .update(dueCollectedMap);
    }

    /****************Convert date to string format*****************/
    private String dateString(Date date){
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
        String finalDate = timeFormat.format(date);
        return  finalDate;
    }

}
