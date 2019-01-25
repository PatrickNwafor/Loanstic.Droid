package com.icubed.loansticdroid.cloudqueries;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.icubed.loansticdroid.localdatabase.BorrowerFilesTable;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.UUID;

public class BorrowerFilesQueries {

    private FirebaseFirestore firebaseFirestore;
    private StorageReference borrowerFilesStorageRef;
    private StorageReference borrowerFilesThumbStorageRef;
    private String filesID;
    Context context;

    public BorrowerFilesQueries(Context context){
        this.context = context;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    /***********Upload file image of borrower*************/
    public UploadTask uploadImageFiles(Bitmap bitmap){

        filesID = UUID.randomUUID().toString();
        borrowerFilesStorageRef = FirebaseStorage.getInstance()
                .getReference("files/")
                .child(filesID+".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        return borrowerFilesStorageRef.putBytes(data);
    }

    /***********Upload file image thumb of borrower*************/
    public UploadTask uploadThumbImageFiles(Bitmap bitmap){
        borrowerFilesThumbStorageRef = FirebaseStorage.getInstance()
                .getReference("files/thumb/")
                .child(filesID+".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
        byte[] data = baos.toByteArray();

        return borrowerFilesThumbStorageRef.putBytes(data);
    }

    public Task<DocumentReference> saveFileToCloud(String borrowerId, Map<String, Object> borrowerGroupMap){
        return firebaseFirestore.collection("Borrowers")
                .document(borrowerId)
                .collection("files")
                .add(borrowerGroupMap);
    }

    public Task<DocumentReference> saveFileToCloud(String borrowerId, BorrowerFilesTable borrowerFilesTable){
        return firebaseFirestore.collection("Borrowers")
                .document(borrowerId)
                .collection("files")
                .add(borrowerFilesTable);
    }

    public Task<QuerySnapshot> retrieveFilesFromCloud(String borrowerId, String activityCycleId){
        return firebaseFirestore.collection("Borrowers")
                .document(borrowerId)
                .collection("files")
                .whereEqualTo("activityCycleId",activityCycleId)
                .get();
    }

    public Task<Void> deleteGroupFromBorrower(String fileID, String borrowerId){
        return firebaseFirestore.collection("Borrowers")
                .document(borrowerId)
                .collection("files")
                .document(fileID)
                .delete();
    }

}
