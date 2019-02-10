package com.icubed.loansticdroid.cloudqueries;

import android.graphics.Bitmap;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.icubed.loansticdroid.util.BitmapUtil;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PaymentQueries {

    private Account account;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference paymentImageStorageRef;
    private StorageReference paymentImageThumbStorageRef;
    private String uniqueID;

    public PaymentQueries(){

        account = new Account();
        firebaseFirestore = FirebaseFirestore.getInstance();
        uniqueID = UUID.randomUUID().toString();
        paymentImageStorageRef = FirebaseStorage.getInstance()
                .getReference("payment_validation_image/");

        paymentImageThumbStorageRef = FirebaseStorage.getInstance()
                .getReference("payment_validation_image/thumb/");
    }

    /************Upload PaymentQueries Validation Image***************/
    public UploadTask uploadImage(Bitmap bitmap, String paymentId){

        paymentImageStorageRef.child(paymentId+".jpg");

        byte[] data = BitmapUtil.getBytesFromBitmapInJPG(bitmap, 100);

        return paymentImageStorageRef.putBytes(data);
    }

    /***************Upload payment validation image thumb**********/
    public UploadTask uploadImageThumb(Bitmap bitmap, String paymentId){

        paymentImageThumbStorageRef.child(paymentId+".jpg");

        byte[] data = BitmapUtil.getBytesFromBitmapInJPG(bitmap, 10);

        return paymentImageThumbStorageRef.putBytes(data);
    }

    /**********Save image Uri on firestore database**************/
    public Task<Void> storeUriToFirestore(String paymentId, String paymentImageUri, String paymentImageThumbUri) {

        Map<String, String> paymentMap = new HashMap<>();
        paymentMap.put("paymentValidationPhotoUrl", paymentImageUri);
        paymentMap.put("paymentValidationThumbPhotoUrl", paymentImageThumbUri);

        return firebaseFirestore.collection("Payment")
                .document(paymentId)
                .set(paymentMap, SetOptions.merge());

    }

    /****************Create a new payment**************/
    public Task<DocumentReference> createPayment(){
        Map<String, Object> paymentMap = new HashMap<>();
        paymentMap.put("loanId","lkcjdjccwec");
        paymentMap.put("isPaid", false);

        return firebaseFirestore.collection("Payment")
                .add(paymentMap);
    }

    /********************Retrieve Single PaymentQueries Details**************/
    public Task<DocumentSnapshot> retieveSinglePayment(String paymentId){
        return firebaseFirestore.collection("Payment")
                .document(paymentId)
                .get();
    }

    /***************Retrieve All Payments Details******************/
    public Task<QuerySnapshot> retrieveAllPayment(){
        return firebaseFirestore.collection("Payment").get();
    }

}
