package com.icubed.loansticdroid.cloudqueries;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.models.Account;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class BorrowersQueries {

    private Account account;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference borrowerImageStorageRef;
    private StorageReference borrowerImageThumbStorageRef;
    private StorageReference borrowerFilesStorageRef;
    private StorageReference borrowerFilesThumbStorageRef;
    private String uniqueID;
    private String filesID;
    Context context;

    public BorrowersQueries(Context context){

        account = new Account();
        this.context = context;
        firebaseFirestore = FirebaseFirestore.getInstance();
        uniqueID = UUID.randomUUID().toString();
        borrowerImageStorageRef = FirebaseStorage.getInstance()
                .getReference("borrower_profile_image/")
                .child(uniqueID+".jpg");

        borrowerImageThumbStorageRef = FirebaseStorage.getInstance()
                .getReference("borrower_profile_image/thumb/")
                .child(uniqueID+".jpg");
    }

    /************Upload PaymentQueries Validation Image***************/
    public UploadTask uploadBorrowerImage(Bitmap bitmap){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        return borrowerImageStorageRef.putBytes(data);
    }

    /***************Upload payment validation image thumb**********/
    public UploadTask uploadBorrowerImageThumb(Bitmap bitmap){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,
                10, baos);
        byte[] data = baos.toByteArray();

        return borrowerImageThumbStorageRef.putBytes(data);
    }

    /***********Upload file image of borrower*************/
    public UploadTask uploadImageFiles(Bitmap bitmap){

        filesID = UUID.randomUUID().toString();
        borrowerFilesStorageRef = FirebaseStorage.getInstance()
                .getReference("borrower_files/")
                .child(filesID+".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        return borrowerFilesStorageRef.putBytes(data);
    }

    /***********Upload file image thumb of borrower*************/
    public UploadTask uploadThumbImageFiles(Bitmap bitmap){
        borrowerFilesThumbStorageRef = FirebaseStorage.getInstance()
                .getReference("borrower_files/thumb/")
                .child(filesID+".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
        byte[] data = baos.toByteArray();

        return borrowerFilesThumbStorageRef.putBytes(data);
    }

    /*******************Add new borrower*************/
    public Task<DocumentReference> addNewBorrower(BorrowersTable borrowersTable){
        return firebaseFirestore.collection("Borrowers").add(borrowersTable);
    }

    /*******************Add new borrower*************/
    public Task<DocumentReference> addNewBorrower(Map<String, Object> borrowersTable){
        return firebaseFirestore.collection("Borrowers").add(borrowersTable);
    }

    /**************Retrieve all borrower***********/
    public Task<QuerySnapshot> retrieveAllBorrowers(){
        return firebaseFirestore.collection("Borrowers").get();
    }

    /**************Retrieve all borrower for a loanOfficer***********/
    public Task<QuerySnapshot> retrieveAllBorrowersForLoanOfficer(){
        return firebaseFirestore.collection("Borrowers")
                .whereEqualTo("loanOfficerId", account.getCurrentUserId())
                .get();
    }

    /**************Retrieve single borrower***********/
    public Task<DocumentSnapshot> retrieveSingleBorrowers(String borrowersId){
        return firebaseFirestore.collection("Borrowers")
                .document(borrowersId)
                .get();
    }

}
