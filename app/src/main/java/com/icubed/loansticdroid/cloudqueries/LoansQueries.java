package com.icubed.loansticdroid.cloudqueries;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.localdatabase.LoansTable;

import java.util.Map;

public class LoansQueries {

    private Account account;
    private FirebaseFirestore firebaseFirestore;

    public LoansQueries(){
        account = new Account();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public Task<DocumentReference> createLoan(LoansTable loansTable){
        return firebaseFirestore.collection("Loan").add(loansTable);
    }

    public Task<DocumentReference> createLoan(Map<String, Object> loanMap){
        return firebaseFirestore.collection("Loan").add(loanMap);
    }

    /**************Retrieve all LoansQueries***********/
    public Task<QuerySnapshot> retrieveAllLoans(){
        return firebaseFirestore.collection("Loan").get();
    }

    /**************Retrieve single Loan***********/
    public Task<DocumentSnapshot> retrieveSingleLoan(String loanId){
        return firebaseFirestore.collection("Loan")
                .document(loanId)
                .get();
    }

    public Task<QuerySnapshot> retrieveLoanForLoanOfficer(){
        return firebaseFirestore.collection("Loan")
                .whereEqualTo("loanOfficerId", account.getCurrentUserId())
                .get();
    }

}
