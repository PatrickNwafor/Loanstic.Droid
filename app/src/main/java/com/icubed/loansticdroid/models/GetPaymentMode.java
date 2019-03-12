package com.icubed.loansticdroid.models;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.cloudqueries.PaymentModeQueries;
import com.icubed.loansticdroid.localdatabase.PaymentModeTable;
import com.icubed.loansticdroid.localdatabase.PaymentModeTable;
import com.icubed.loansticdroid.localdatabase.PaymentModeTableQueries;

import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class GetPaymentMode {
    PaymentModeQueries paymentModeQueries;
    PaymentModeTableQueries paymentModeTableQueries;
    Activity activity;

    public GetPaymentMode(Activity activity) {
        this.activity = activity;
        paymentModeQueries = new PaymentModeQueries();
        paymentModeTableQueries = new PaymentModeTableQueries(activity.getApplication());
    }
    
    private boolean doesPaymentModeExist(){
        List<PaymentModeTable> paymentModeTables = paymentModeTableQueries.loadAllPaymentModes();
        
        if(paymentModeTables.isEmpty()) return false;
        
        return true;
    }

    public void getPaymentMode(){
        if(doesPaymentModeExist()) loadAllPaymentModesAndCompareToLocal();
        else retrieveNewPaymentMode();
    }

    
    private void retrieveNewPaymentMode(){
        paymentModeQueries.retrievePaymentMode()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){
                                for(DocumentSnapshot doc : task.getResult()){
                                    PaymentModeTable paymentModeTable = doc.toObject(PaymentModeTable.class);
                                    paymentModeTable.setPaymentModeId(doc.getId());

                                    Log.d(TAG, "onComplete: "+paymentModeTable.toString());
                                    savePaymentMode(paymentModeTable);
                                }
                            }
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void savePaymentMode(PaymentModeTable paymentModeTable) {
        PaymentModeTable paymentModeTable1 = paymentModeTableQueries.loadSinglePaymentMode(paymentModeTable.getPaymentModeId());
        if(paymentModeTable1 == null) paymentModeTableQueries.insertPaymentModeToStorage(paymentModeTable);
    }

    private void loadAllPaymentModesAndCompareToLocal() {
        final List<PaymentModeTable> paymentList = paymentModeTableQueries.loadAllPaymentModes();

        paymentModeQueries.retrievePaymentMode()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){

                                List<PaymentModeTable> borrowersInStorage = paymentList;
                                for(DocumentSnapshot doc : task.getResult().getDocuments()) {

                                    Boolean doesDataExist = false;
                                    for (PaymentModeTable bowTab : paymentList) {
                                        if (bowTab.getPaymentModeId().equals(doc.getId())) {
                                            doesDataExist = true;
                                            borrowersInStorage.remove(bowTab);
                                            Log.d(TAG, "onComplete: payment id of " + doc.getId() + " already exist");
                                            break;
                                        }
                                    }

                                    if (!doesDataExist) {
                                        Log.d(TAG, "onComplete: payment id of " + doc.getId() + " does not exist");

                                        PaymentModeTable PaymentModeTable = doc.toObject(PaymentModeTable.class);
                                        PaymentModeTable.setPaymentModeId(doc.getId());

                                        savePaymentMode(PaymentModeTable);
                                    }else{
                                        //Update local table if any changes
                                        updateTable(doc);
                                    }
                                }

                                //to delete deleted borrower in cloud from storage
                                if(!borrowersInStorage.isEmpty()){
                                    for(PaymentModeTable borowTab : borrowersInStorage){
                                        deletePaymentModeFromLocalStorage(borowTab);
                                        Log.d("Delete", "deleted "+borowTab.getPaymentModeId()+ " from storage");
                                    }
                                }
                            }
                        }else{
                            Log.d("Borrower", "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void updateTable(DocumentSnapshot doc) {
        PaymentModeTable PaymentModeTable = doc.toObject(PaymentModeTable.class);
        PaymentModeTable.setPaymentModeId(doc.getId());

        PaymentModeTable currentlySaved = paymentModeTableQueries.loadSinglePaymentMode(doc.getId());
        PaymentModeTable.setId(currentlySaved.getId());

        if(PaymentModeTable.getLastUpdatedAt().getTime() != currentlySaved.getLastUpdatedAt().getTime()){

            paymentModeTableQueries.updatePaymentMode(PaymentModeTable);
            Log.d("Borrower", "Borrower Detailed updated");

        }
    }

    private void deletePaymentModeFromLocalStorage(PaymentModeTable paymentModeTable) {
        paymentModeTableQueries.deletePaymentMode(paymentModeTable);
    }
}
