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
import com.icubed.loansticdroid.localdatabase.SavingsPlanTypeTable;
import com.icubed.loansticdroid.util.BitmapUtil;

import java.util.Map;
import java.util.UUID;

public class SavingsPlanTypeQueries {
    private FirebaseFirestore firebaseFirestore;
    private StorageReference borrowerFilesStorageRef;
    private StorageReference borrowerFilesThumbStorageRef;
    private String filesID;

    public SavingsPlanTypeQueries(){
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    /***********Upload file image of borrower*************/
    public UploadTask uploadSavingsPlanTypeImage(Bitmap bitmap){

        filesID = UUID.randomUUID().toString();
        borrowerFilesStorageRef = FirebaseStorage.getInstance()
                .getReference("Savings_Plan_Type/")
                .child(filesID+".jpg");

        byte[] data = BitmapUtil.getBytesFromBitmapInPNG(bitmap, 100);

        return borrowerFilesStorageRef.putBytes(data);
    }

    /***********Upload file image thumb of borrower*************/
    public UploadTask uploadSavingsPlanTypeImageThumb(Bitmap bitmap){
        borrowerFilesThumbStorageRef = FirebaseStorage.getInstance()
                .getReference("Savings_Plan_Type/thumb/")
                .child(filesID+".jpg");

        byte[] data = BitmapUtil.getBytesFromBitmapInPNG(bitmap, 10);

        return borrowerFilesThumbStorageRef.putBytes(data);
    }

    public Task<DocumentReference> saveSavingsPlanType(Map<String, Object> borrowerGroupMap){
        return firebaseFirestore.collection("Savings_Plan_Type")
                .add(borrowerGroupMap);
    }

    public Task<DocumentReference> saveSavingsPlanType(SavingsPlanTypeTable savingsPlanTypeTable){
        return firebaseFirestore.collection("Savings_Plan_Type")
                .add(savingsPlanTypeTable);
    }

    public Task<QuerySnapshot> retrieveAllSavingsPlanType(){
        return firebaseFirestore.collection("Savings_Plan_Type")
                .get();
    }

    public Task<DocumentSnapshot> retrieveSingleSavingsPlanType(String savingsPlanTypeId){
        return firebaseFirestore.collection("Savings_Plan_Type")
                .document(savingsPlanTypeId)
                .get();
    }

    public Task<Void> deleteSavingsPlanType(String savingsPlanTypeId){
        return firebaseFirestore.collection("Savings_Plan_Type")
                .document(savingsPlanTypeId)
                .delete();
    }

}
