package com.icubed.loansticdroid.cloudqueries;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;

import java.util.HashMap;
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

    /**************Retrieve a borrower"s borrower group***********/
    public Task<QuerySnapshot> retrieveABorrowersGroup(String borrowerId){

        return firebaseFirestore.collection("Borrowers_Group")
                .whereEqualTo("borrowerId", borrowerId)
                .get();

    }
}
