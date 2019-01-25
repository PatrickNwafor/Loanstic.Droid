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

    public Task<DocumentReference> saveNewGroupForBorrower(String borrowerId, Map<String, Object> borrowerGroupMap){
        return firebaseFirestore.collection("Borrowers")
                .document(borrowerId)
                .collection("groups")
                .add(borrowerGroupMap);
    }

    public Task<DocumentReference> saveNewGroupForBorrower(String borrowerId, BorrowerGroupsTable borrowerGroupsTable){
        return firebaseFirestore.collection("Borrowers")
                .document(borrowerId)
                .collection("groups")
                .add(borrowerGroupsTable);
    }

    public Task<QuerySnapshot> retrieveGroupsOfBorrower(String borrowerId){
        return firebaseFirestore.collection("Borrowers")
                .document(borrowerId)
                .collection("groups")
                .get();
    }

    public Task<QuerySnapshot> retrieveSingleGroupOfBorrower(String borrowerId, String groupId){
        return firebaseFirestore.collection("Borrowers")
                .document(borrowerId)
                .collection("groups")
                .whereEqualTo("groupId", groupId)
                .get();
    }

    public Task<Void> deleteGroupFromBorrower(String documentId, String borrowerId){
        return firebaseFirestore.collection("Borrowers")
                .document(borrowerId)
                .collection("groups")
                .document(documentId)
                .delete();
    }
}
