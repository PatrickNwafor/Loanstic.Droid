package com.icubed.loansticdroid.cloudqueries;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class PaymentModeQueries {
    private FirebaseFirestore firebaseFirestore;

    public PaymentModeQueries(){
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    /********************Retrieve Single PaymentModeQueries Details**************/
    public Task<QuerySnapshot> retrievePaymentMode(){
        return firebaseFirestore.collection("Payment_Mode")
                .get();
    }
}
