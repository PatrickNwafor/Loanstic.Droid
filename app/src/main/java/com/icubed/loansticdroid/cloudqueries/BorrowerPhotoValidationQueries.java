package com.icubed.loansticdroid.cloudqueries;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.icubed.loansticdroid.localdatabase.BorrowerPhotoValidationTable;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.models.Account;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class BorrowerPhotoValidationQueries {

    private Account account;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference borrowerImageStorageRef;
    private StorageReference borrowerImageThumbStorageRef;
    private StorageReference borrowerFilesStorageRef;
    private StorageReference borrowerFilesThumbStorageRef;
    private String uniqueID;
    Context context;

    public BorrowerPhotoValidationQueries(Context context){

        account = new Account();
        this.context = context;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    /***********Upload shop image of borrower*************/
    public UploadTask uploadImage(Bitmap bitmap){
        uniqueID = UUID.randomUUID().toString();
        borrowerImageStorageRef = FirebaseStorage.getInstance()
                .getReference("borrower_shop_images/")
                .child(uniqueID+".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        return borrowerFilesStorageRef.putBytes(data);
    }

    /*******************Add new photo*************/
    public Task<DocumentReference> addNewValidationPhoto(BorrowerPhotoValidationTable borrowerPhotoValidationTable){
        return firebaseFirestore.collection("Photo_Validation").add(borrowerPhotoValidationTable);
    }

    /**************Retrieve all photos***********/
    public Task<QuerySnapshot> retrieveAllValidationPhoto(){
        return firebaseFirestore.collection("Photo_Validation").get();
    }

    /**************Retrieve all photos for a borrower***********/
    public Task<QuerySnapshot> retrieveAllValidationPhotosForBorrower(String borrowerId){
        return firebaseFirestore.collection("Photo_Validation")
                .whereEqualTo("borrowerId", borrowerId)
                .get();
    }

    /**************Retrieve single borrower***********/
    public Task<DocumentSnapshot> retrieveSingleValidationPhoto(String validationPhotoId){
        return firebaseFirestore.collection("Photo_Validation")
                .document(validationPhotoId)
                .get();
    }

    /***********Upload shop image thumb of borrower*************/
    public UploadTask uploadThumbImage(Bitmap bitmap){
        borrowerImageThumbStorageRef = FirebaseStorage.getInstance()
                .getReference("borrower_shop_images/thumb/")
                .child(uniqueID+".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
        byte[] data = baos.toByteArray();

        return borrowerFilesThumbStorageRef.putBytes(data);
    }

}
