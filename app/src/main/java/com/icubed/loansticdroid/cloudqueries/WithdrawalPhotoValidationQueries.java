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
import com.icubed.loansticdroid.util.BitmapUtil;

import java.util.Map;
import java.util.UUID;

public class WithdrawalPhotoValidationQueries {
    private FirebaseFirestore firebaseFirestore;
    private StorageReference paymentImageStorageRef;
    private StorageReference paymentImageThumbStorageRef;
    private String uniqueID;

    public WithdrawalPhotoValidationQueries(){
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    /************Upload PaymentQueries Validation Image***************/
    public UploadTask uploadImage(Bitmap bitmap){
        uniqueID = UUID.randomUUID().toString();
        paymentImageStorageRef = FirebaseStorage.getInstance()
                .getReference("Withdrawal_Photo_Verification/")
                .child(uniqueID+".jpg");

        byte[] data = BitmapUtil.getBytesFromBitmapInJPG(bitmap, 100);
        return paymentImageStorageRef.putBytes(data);
    }

    /***************Upload payment validation image thumb**********/
    public UploadTask uploadImageThumb(Bitmap bitmap){

        paymentImageThumbStorageRef = FirebaseStorage.getInstance()
                .getReference("Withdrawal_Photo_Verification/thumb/")
                .child(uniqueID+".jpg");

        byte[] data = BitmapUtil.getBytesFromBitmapInJPG(bitmap, 10);
        return paymentImageThumbStorageRef.putBytes(data);
    }

    /**********Save image Uri on firestore database**************/
    public Task<DocumentReference> saveWithdrawalPhotoUriToCloud(Map<String, Object> photoVerifMap) {

        return firebaseFirestore.collection("Withdrawal_Photo_Verification")
                .add(photoVerifMap);

    }

    /********************Retrieve Single PaymentQueries Details**************/
    public Task<DocumentSnapshot> retieveSingleWithdrawalVerifPhoto(String photoVerifId){
        return firebaseFirestore.collection("Withdrawal_Photo_Verification")
                .document(photoVerifId)
                .get();
    }

    /***************Retrieve All Payments Details******************/
    public Task<QuerySnapshot> retrieveAllPhotoVerifForWithdrawal(String withdrawalId){
        return firebaseFirestore.collection("Withdrawal_Photo_Verification")
                .whereEqualTo("withdrawalId", withdrawalId)
                .get();
    }
}
