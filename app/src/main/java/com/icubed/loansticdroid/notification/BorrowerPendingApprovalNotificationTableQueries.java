package com.icubed.loansticdroid.notification;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BorrowerPendingApprovalNotificationTableQueries {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;

    public BorrowerPendingApprovalNotificationTableQueries() {
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }


    public Task<DocumentReference> sendNotification(BorrowerPendingApprovalNotificationTable borrowerPendingApprovalNotificationTable, String loanOfficerId){
        return firebaseFirestore.collection("notification")
                .document(loanOfficerId)
                .collection("borrower_pending_approval")
                .add(borrowerPendingApprovalNotificationTable);
    }

}
