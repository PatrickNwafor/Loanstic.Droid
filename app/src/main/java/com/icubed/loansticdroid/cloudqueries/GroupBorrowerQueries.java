package com.icubed.loansticdroid.cloudqueries;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;

import java.util.Map;

public class GroupBorrowerQueries {

    private FirebaseFirestore firebaseFirestore;
    private Account account;

    public GroupBorrowerQueries(){
        firebaseFirestore = FirebaseFirestore.getInstance();
        account = new Account();
    }

    /*****************Create new Borrower Group ****************/
    public Task<DocumentReference> createBorrowersGroup(GroupBorrowerTable groupBorrowerTable){
        return firebaseFirestore.collection("Borrowers_Group").add(groupBorrowerTable);
    }

    /*****************Create new Borrower Group ****************/
    public Task<DocumentReference> createBorrowersGroup(Map<String, Object> groupBorrowerMap){
        return firebaseFirestore.collection("Borrowers_Group").add(groupBorrowerMap);
    }

    /*****************Retrieve All Borrower Group*********************/
    public Task<QuerySnapshot> retrieveAllBorrowersGroup(){
        return firebaseFirestore.collection("Borrowers_Group").get();
    }

    /*****************Retrieve Sinlge Borrower Group*******************/
    public Task<DocumentSnapshot> retrieveSingleBorrowerGroup(String groupId){

        return firebaseFirestore.collection("Borrowers_Group")
                .document(groupId)
                .get();

    }

    /*****************Retrieve Sinlge Borrower Group*******************/
    public Task<Void> updateGroupAfterAddingNewMembers(String groupId, Map<String, Object> groupMap){

        return firebaseFirestore.collection("Borrowers_Group")
                .document(groupId)
                .set(groupMap, SetOptions.merge());
    }

    /**************Retrieve a borrower"s borrower group***********/
    public Task<QuerySnapshot> retrieveABorrowersGroup(String borrowerId){

        return firebaseFirestore.collection("Borrowers_Group")
                .whereEqualTo("borrowerId", borrowerId)
                .get();

    }
}
