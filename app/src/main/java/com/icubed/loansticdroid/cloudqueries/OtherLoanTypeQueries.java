package com.icubed.loansticdroid.cloudqueries;

import android.graphics.Bitmap;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.icubed.loansticdroid.localdatabase.LoanTypeTable;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.UUID;

public class OtherLoanTypeQueries {

    private FirebaseFirestore firebaseFirestore;
    private StorageReference borrowerFilesStorageRef;
    private StorageReference borrowerFilesThumbStorageRef;
    private String filesID;

    public OtherLoanTypeQueries(){
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public Task<DocumentReference> saveOtherLoanType(Map<String, Object> borrowerGroupMap){
        return firebaseFirestore.collection("Other_Loan_Type")
                .add(borrowerGroupMap);
    }

    public Task<DocumentReference> saveOtherLoanType(LoanTypeTable loanTypeTable){
        return firebaseFirestore.collection("Other_Loan_Type")
                .add(loanTypeTable);
    }

    public Task<QuerySnapshot> retrieveAllOtherLoanType(){
        return firebaseFirestore.collection("Other_Loan_Type")
                .whereEqualTo("branchId", "2s6biiTANBZ4VqTUDrtEsdwgc822")
                .get();
    }

    public Task<DocumentSnapshot> retrieveSingleOtherLoanType(String loanTypeId){
        return firebaseFirestore.collection("Other_Loan_Type")
                .document(loanTypeId)
                .get();
    }

    public Task<Void> deleteOtherLoanType(String loanTypeId){
        return firebaseFirestore.collection("Other_Loan_Type")
                .document(loanTypeId)
                .delete();
    }
    
}
