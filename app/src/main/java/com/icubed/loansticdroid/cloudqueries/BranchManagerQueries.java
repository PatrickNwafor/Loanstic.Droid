package com.icubed.loansticdroid.cloudqueries;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class BranchManagerQueries {
    private Account account;
    private FirebaseFirestore firebaseFirestore;

    public BranchManagerQueries() {
        account = new Account();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    /********************Approve group************************/
    public Task<Void> approveBorrowersGroup(String groupId){

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("isGroupApproved", true);
        objectMap.put("approvedBy", account.getCurrentUserId());

        return firebaseFirestore.collection("Borrowers_Group")
                .document(groupId)
                .set(objectMap, SetOptions.merge());

    }

    /*************Assign loan officer to a borrower*************/
    public Task<Void> assignLoanOfficerToBorrower(String loanOfficerId, String borrowerId){
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("loanOfficerId", loanOfficerId);
        objectMap.put("assignedBy", account.getCurrentUserId());

        return firebaseFirestore.collection("Borrowers")
                .document(borrowerId)
                .set(objectMap, SetOptions.merge());
    }

    /*************Assign loan officer to a borrower*************/
    public Task<Void> assignLoanOfficerToBorrowerGroup(String loanOfficerId, String groupId){
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("loanOfficerId", loanOfficerId);
        objectMap.put("assignedBy", account.getCurrentUserId());

        return firebaseFirestore.collection("Borrowers_Group")
                .document(groupId)
                .set(objectMap, SetOptions.merge());
    }
}
