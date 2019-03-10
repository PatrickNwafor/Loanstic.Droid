package com.icubed.loansticdroid.cloudqueries;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class LoanNumberGeneratorQueries {

    private FirebaseFirestore firebaseFirestore;

    public LoanNumberGeneratorQueries(){
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public Task<DocumentReference> addLoanNumber(String loanNumber){
        Map<String, Object> loanNumberMap = new HashMap<>();
        loanNumberMap.put("loanNumber", loanNumber);

        return firebaseFirestore.collection("Loan_Number")
                .add(loanNumberMap);
    }

    public Task<QuerySnapshot> validateLoanNumber(String loanNumber){
        return firebaseFirestore.collection("Loan_Number")
                .whereEqualTo("loanNumber", loanNumber)
                .get();
    }
}
