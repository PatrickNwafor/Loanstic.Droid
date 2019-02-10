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
import com.icubed.loansticdroid.util.BitmapUtil;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.UUID;

public class LoanTypeQueries {

    private FirebaseFirestore firebaseFirestore;
    private StorageReference borrowerFilesStorageRef;
    private StorageReference borrowerFilesThumbStorageRef;
    private String filesID;

    public LoanTypeQueries(){
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    /***********Upload file image of borrower*************/
    public UploadTask uploadLoanTypeImage(Bitmap bitmap){

        filesID = UUID.randomUUID().toString();
        borrowerFilesStorageRef = FirebaseStorage.getInstance()
                .getReference("loanTypes/")
                .child(filesID+".jpg");

        byte[] data = BitmapUtil.getBytesFromBitmapInPNG(bitmap, 100);

        return borrowerFilesStorageRef.putBytes(data);
    }

    /***********Upload file image thumb of borrower*************/
    public UploadTask uploadLoanTypeImageThumb(Bitmap bitmap){
        borrowerFilesThumbStorageRef = FirebaseStorage.getInstance()
                .getReference("loanTypes/thumb/")
                .child(filesID+".jpg");

        byte[] data = BitmapUtil.getBytesFromBitmapInPNG(bitmap, 10);

        return borrowerFilesThumbStorageRef.putBytes(data);
    }

    public Task<DocumentReference> saveLoanType(Map<String, Object> borrowerGroupMap){
        return firebaseFirestore.collection("Loan_Type")
                .add(borrowerGroupMap);
    }

    public Task<DocumentReference> saveLoanType(LoanTypeTable loanTypeTable){
        return firebaseFirestore.collection("Loan_Type")
                .add(loanTypeTable);
    }

    public Task<QuerySnapshot> retrieveAllLoanType(){
        return firebaseFirestore.collection("Loan_Type")
                .whereEqualTo("branchId", "2s6biiTANBZ4VqTUDrtEsdwgc822")
                .get();
    }

    public Task<DocumentSnapshot> retrieveSingleLoanType(String loanTypeId){
        return firebaseFirestore.collection("Loan_Type")
                .document(loanTypeId)
                .get();
    }

    public Task<Void> deleteLoanType(String loanTypeId){
        return firebaseFirestore.collection("Loan_Type")
                .document(loanTypeId)
                .delete();
    }
    
}
