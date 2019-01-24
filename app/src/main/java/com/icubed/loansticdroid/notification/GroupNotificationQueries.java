package com.icubed.loansticdroid.notification;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class GroupNotificationQueries {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;

    public GroupNotificationQueries() {
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }


    public Task<DocumentReference> sendNotification(GroupNotificationTable groupNotificationTable, String loanOfficerId){
        return firebaseFirestore.collection("notification")
                .document(loanOfficerId)
                .collection("group_notification")
                .add(groupNotificationTable);
    }
}
