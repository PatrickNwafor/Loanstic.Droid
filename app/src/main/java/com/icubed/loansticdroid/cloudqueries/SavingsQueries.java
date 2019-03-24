package com.icubed.loansticdroid.cloudqueries;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.icubed.loansticdroid.localdatabase.SavingsTable;

import java.util.Map;

public class SavingsQueries {
    private Account account;
    private FirebaseFirestore firebaseFirestore;

    public SavingsQueries(){
        account = new Account();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public Task<DocumentReference> createSavings(SavingsTable savingsTable){
        return firebaseFirestore.collection("Savings").add(savingsTable);
    }

    public Task<DocumentReference> createSavings(Map<String, Object> loanMap){
        return firebaseFirestore.collection("Savings").add(loanMap);
    }

    /**************Retrieve all SavingssQueries***********/
    public Task<QuerySnapshot> retrieveAllSavings(){
        return firebaseFirestore.collection("Savings").get();
    }

    /**************Retrieve single Savings***********/
    public Task<DocumentSnapshot> retrieveSingleSavings(String savingsId){
        return firebaseFirestore.collection("Savings")
                .document(savingsId)
                .get();
    }

    public Task<QuerySnapshot> retrieveSavingsForLoanOfficer(){
        return firebaseFirestore.collection("Savings")
                .whereEqualTo("loanOfficerId", account.getCurrentUserId())
                .get();
    }

    public Task<QuerySnapshot> retrieveSavingsForBorrower(String borrowerId){
        return firebaseFirestore.collection("Savings")
                .whereEqualTo("borrowerId", borrowerId)
                .get();
    }

    public Task<QuerySnapshot> retrieveSavingsForGroup(String groupId){
        return firebaseFirestore.collection("Savings")
                .whereEqualTo("groupId", groupId)
                .get();
    }

    public Task<Void> updateSavingsDetails(Map<String, Object> objectMap, String savingsId){
        return firebaseFirestore.collection("Savings")
                .document(savingsId)
                .set(objectMap, SetOptions.merge());

    }
}
