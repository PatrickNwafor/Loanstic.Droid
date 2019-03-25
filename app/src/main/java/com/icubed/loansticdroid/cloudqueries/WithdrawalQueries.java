package com.icubed.loansticdroid.cloudqueries;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.localdatabase.WithdrawalTable;

import java.util.Map;

public class WithdrawalQueries {
    private FirebaseFirestore firebaseFirestore;

    public WithdrawalQueries(){
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    /****************Create a new payment**************/
    public Task<DocumentReference> makeWithdrawal(WithdrawalTable withdrawalTable){
        return firebaseFirestore.collection("Withdrawal").add(withdrawalTable);
    }

    /****************Create a new payment**************/
    public Task<DocumentReference> makeWithdrawal(Map<String, Object> withdrawalTableMap){
        return firebaseFirestore.collection("Withdrawal").add(withdrawalTableMap);
    }

    /********************Retrieve Single WithdrawalQueries Details**************/
    public Task<DocumentSnapshot> retieveSingleWithdrawal(String withdrawalId){
        return firebaseFirestore.collection("Withdrawal")
                .document(withdrawalId)
                .get();
    }

    /***************Retrieve All Withdrawals for a loan Details******************/
    public Task<QuerySnapshot> retrieveAllWithdrawalForSavings(String savingsId){
        return firebaseFirestore.collection("Withdrawal")
                .whereEqualTo("savingsId", savingsId)
                .get();
    }
}
