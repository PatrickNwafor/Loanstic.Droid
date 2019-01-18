package com.icubed.loansticdroid.cloudqueries;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.localdatabase.BorrowerGroupsTable;

import java.util.Map;

public class BorrowerGroupsQueries {
    private Account account;
    private FirebaseFirestore firebaseFirestore;

    public BorrowerGroupsQueries(){

        account = new Account();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public Task<DocumentReference> saveNewGroupForBorrower(Map<String, Object> borrowerGroupMap){
        return firebaseFirestore.collection("borrower_groups_ids")
                .add(borrowerGroupMap);
    }

    public Task<DocumentReference> saveNewGroupForBorrower(BorrowerGroupsTable borrowerGroupsTable){
        return firebaseFirestore.collection("borrower_groups_ids")
                .add(borrowerGroupsTable);
    }

    public Task<QuerySnapshot> retrieveGroupsOfBorrower(String borrowerId){
        return firebaseFirestore.collection("borrower_groups_ids")
                .whereEqualTo("borrowerId", borrowerId)
                .get();
    }

    public Task<Void> deleteGroupFromBorrower(String documentId){
        return firebaseFirestore.collection("borrower_groups_ids")
                .document(documentId)
                .delete();
    }
}
