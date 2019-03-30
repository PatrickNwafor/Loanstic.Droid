package com.icubed.loansticdroid.cloudqueries;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SavingsNumberGeneratorQueries {
    private FirebaseFirestore firebaseFirestore;

    public SavingsNumberGeneratorQueries(){
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public Task<DocumentReference> addLoanNumber(String savingsNumber){
        Map<String, Object> loanNumberMap = new HashMap<>();
        loanNumberMap.put("savingsNumber", savingsNumber);

        return firebaseFirestore.collection("Savings_Number")
                .add(loanNumberMap);
    }

    public Task<QuerySnapshot> validateLoanNumber(String savingsNumber){
        return firebaseFirestore.collection("Savings_Number")
                .whereEqualTo("savingsNumber", savingsNumber)
                .get();
    }
}
