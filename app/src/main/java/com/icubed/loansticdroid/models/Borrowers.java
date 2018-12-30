package com.icubed.loansticdroid.models;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.activities.BorrowerActivity;
import com.icubed.loansticdroid.adapters.BorrowerRecyclerAdapter;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.BorrowersTableQueries;

import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class Borrowers {
    private BorrowersQueries borrowersQueries;
    private BorrowersTableQueries borrowersTableQueries;
    Activity activity;

    public Borrowers(Activity activity) {
        this.activity = activity;
        borrowersQueries = new BorrowersQueries(activity.getApplicationContext());
        borrowersTableQueries = new BorrowersTableQueries(activity.getApplication());
    }

    public Boolean doesBorrowersTableExistInLocalStorage(){
        List<BorrowersTable> borrowersTables = borrowersTableQueries.loadAllBorrowers();
        if(borrowersTables.isEmpty()){
            return false;
        }
        return true;
    }

    /****
     * @TODO
     * to method borrowersQueries.retrieveAllBorrowers() to borrowersQueries.retrieveAllBorrowersForLoanOfficer()
     * this is necessary so that only the borrowers assigned to a loan officer is seen on his borrowers page.
     * if the officers need to search for a non assigned borrower, he has to use the search field
     */
    public void loadAllBorrowers(){
        borrowersQueries.retrieveAllBorrowers().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().isEmpty()){

                        for(DocumentSnapshot doc : task.getResult().getDocuments()){
                            BorrowersTable borrowersTable = doc.toObject(BorrowersTable.class);
                            borrowersTable.setBorrowersId(doc.getId());

                            saveBorrowersToLocalStorage(borrowersTable);
                        }

                        loadBorrowersToUI();
                    }else{
                        Toast.makeText(activity, "Document is empty", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Log.d(TAG, "onComplete: "+task.getException().getMessage());
                }
            }
        });
    }

    private void saveBorrowersToLocalStorage(BorrowersTable borrowersTable) {
        borrowersTableQueries.insertBorrowersToStorage(borrowersTable);
    }

    public void loadBorrowersToUI(){
        List<BorrowersTable> borrowersTables = borrowersTableQueries.loadAllBorrowersOrderByLastName();

        ((BorrowerActivity) activity).borrowerRecyclerAdapter = new BorrowerRecyclerAdapter(borrowersTables);
        ((BorrowerActivity) activity).borrowerRecyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));
        ((BorrowerActivity) activity).borrowerRecyclerView.setAdapter(((BorrowerActivity) activity).borrowerRecyclerAdapter);
        ((BorrowerActivity) activity).borrowerProgressBar.setVisibility(View.GONE);
    }

    /****
     * @TODO
     * to method borrowersQueries.retrieveAllBorrowers() to borrowersQueries.retrieveAllBorrowersForLoanOfficer()
     * this is necessary so that only the borrowers assigned to a loan officer is seen on his borrowers page.
     * if the officers need to search for a non assigned borrower, he has to use the search field
     */
    public void loadAllBorrowersAndCompareToLocal() {
        final List<BorrowersTable> borrowerList = borrowersTableQueries.loadAllBorrowers();

        borrowersQueries.retrieveAllBorrowers()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){

                                Boolean isThereNewData = false;
                                List<BorrowersTable> borrowersInStorage = borrowerList;
                                for(DocumentSnapshot doc : task.getResult().getDocuments()) {

                                    Boolean doesDataExist = false;
                                    for (BorrowersTable bowTab : borrowerList) {
                                        if (bowTab.getBorrowersId().equals(doc.getId())) {
                                            doesDataExist = true;
                                            borrowersInStorage.remove(bowTab);
                                            Log.d(TAG, "onComplete: borrower id of " + doc.getId() + " already exist");
                                            break;
                                        }
                                    }

                                    if (!doesDataExist) {
                                        Log.d(TAG, "onComplete: borrower id of " + doc.getId() + " does not exist");

                                        BorrowersTable borrowersTable = doc.toObject(BorrowersTable.class);
                                        borrowersTable.setBorrowersId(doc.getId());
                                        isThereNewData = true;

                                        saveBorrowersToLocalStorage(borrowersTable);
                                    }
                                }

                                //to delete deleted borrower in cloud from storage
                                if(!borrowersInStorage.isEmpty()){
                                    for(BorrowersTable borowTab : borrowersInStorage){
                                        deleteBorrowerFromLocalStorage(borowTab);
                                        Log.d("Delete", "deleted "+borowTab.getBorrowersId()+ " from storage");
                                    }
                                }

                                if(isThereNewData || !borrowersInStorage.isEmpty()) {
                                    loadBorrowersToUI();
                                }
                            }else{
                                Toast.makeText(activity, "Document is empty", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Log.d("Borrower", "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void deleteBorrowerFromLocalStorage(BorrowersTable borowTab) {
        borrowersTableQueries.deleteBorrowersFromStorage(borowTab);
    }
}
