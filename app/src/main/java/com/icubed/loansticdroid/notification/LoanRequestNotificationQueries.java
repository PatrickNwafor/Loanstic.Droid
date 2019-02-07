package com.icubed.loansticdroid.notification;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoanRequestNotificationQueries {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;

    public LoanRequestNotificationQueries() {
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }


    public Task<DocumentReference> sendNotification(LoanRequestNotificationTable loanRequestNotificationTable, String loanOfficerId){
        return firebaseFirestore.collection("notification")
                .document(loanOfficerId)
                .collection("loan_request_notification")
                .add(loanRequestNotificationTable);
    }

}
