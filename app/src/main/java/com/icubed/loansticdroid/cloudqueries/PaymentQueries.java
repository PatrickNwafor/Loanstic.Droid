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
import com.icubed.loansticdroid.localdatabase.PaymentTable;
import com.icubed.loansticdroid.util.BitmapUtil;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PaymentQueries {

    private FirebaseFirestore firebaseFirestore;

    public PaymentQueries(){
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    /****************Create a new payment**************/
    public Task<DocumentReference> makePayment(PaymentTable paymentTable){
        return firebaseFirestore.collection("Payment").add(paymentTable);
    }

    /****************Create a new payment**************/
    public Task<DocumentReference> makePayment(Map<String, Object> paymentTableMap){
        return firebaseFirestore.collection("Payment").add(paymentTableMap);
    }

    /********************Retrieve Single PaymentQueries Details**************/
    public Task<DocumentSnapshot> retieveSinglePayment(String paymentId){
        return firebaseFirestore.collection("Payment")
                .document(paymentId)
                .get();
    }

    /***************Retrieve All Payments for a loan Details******************/
    public Task<QuerySnapshot> retrieveAllPaymentForLoan(String loanId){
        return firebaseFirestore.collection("Payment")
                .whereEqualTo("loanId", loanId)
                .get();
    }

}
