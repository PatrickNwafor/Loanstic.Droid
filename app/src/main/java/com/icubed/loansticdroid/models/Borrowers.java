package com.icubed.loansticdroid.models;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.activities.BorrowerActivity;
import com.icubed.loansticdroid.adapters.BorrowerRecyclerAdapter;
import com.icubed.loansticdroid.cloudqueries.ActivityCycleQueries;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.fragments.BorrowersFragment.SingleBorrowerFragment;
import com.icubed.loansticdroid.localdatabase.ActivityCycleTable;
import com.icubed.loansticdroid.localdatabase.ActivityCycleTableQueries;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.BorrowersTableQueries;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class Borrowers {
    private BorrowersQueries borrowersQueries;
    private BorrowersTableQueries borrowersTableQueries;
    private ActivityCycleTableQueries activityCycleTableQueries;
    private ActivityCycleQueries activityCycleQueries;
    FragmentActivity activity;
    SingleBorrowerFragment fragment;

    public Borrowers(FragmentActivity activity) {
        this.activity = activity;
        borrowersQueries = new BorrowersQueries(activity.getApplicationContext());
        borrowersTableQueries = new BorrowersTableQueries(activity.getApplication());
        activityCycleTableQueries = new ActivityCycleTableQueries(activity.getApplication());
        activityCycleQueries = new ActivityCycleQueries();

        FragmentManager fm = activity.getSupportFragmentManager();
        fragment = (SingleBorrowerFragment) fm.findFragmentByTag("single");
    }

    public Boolean doesBorrowersTableExistInLocalStorage(){
        List<BorrowersTable> borrowersTables = borrowersTableQueries.loadAllBorrowers();
        return !borrowersTables.isEmpty();
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
                            getActivityCycleData(doc.getId());
                        }

                        loadBorrowersToUI();
                    }else{
                        removeRefresher();
                        ((BorrowerActivity) activity).borrowerProgressBar.setVisibility(View.GONE);
                        Toast.makeText(activity, "Document is empty", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Log.d(TAG, "onComplete: "+task.getException().getMessage());
                }
            }
        });
    }

    private void getActivityCycleData(final String id) {
        activityCycleQueries.retrieveAllCycleForBorrower(id)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot doc : queryDocumentSnapshots){
                            ActivityCycleTable activityCycleTable = doc.toObject(ActivityCycleTable.class);
                            activityCycleTable.setActivityCycleId(doc.getId());

                            List<ActivityCycleTable> activityCycleTable1 = activityCycleTableQueries.loadAllActivityCyclesForBorrower(id);
                            ActivityCycleTable cycleTable = null;
                            Boolean doesActivityExist = false;

                            for(ActivityCycleTable act : activityCycleTable1){
                                if(act.getActivityCycleId().equals(activityCycleTable.getActivityCycleId())){
                                    cycleTable = act;
                                    doesActivityExist = true;
                                    break;
                                }
                            }

                            if(!doesActivityExist){
                                saveActivityCycleToLocalStorage(activityCycleTable);
                            }else if(activityCycleTable.getIsActive() != cycleTable.getIsActive()){
                                activityCycleTable.setId(cycleTable.getId());
                                updateActivityCycleTable(activityCycleTable);
                                Log.i("data", activityCycleTableQueries.loadSingleActivityCycle(cycleTable.getActivityCycleId()).toString());
                            }

                        }
                    }
                });
    }

    private void saveActivityCycleToLocalStorage(ActivityCycleTable activityCycleTable) {
        ActivityCycleTable activityCycleTable1 = activityCycleTableQueries.loadSingleActivityCycle(activityCycleTable.getActivityCycleId());
        if(activityCycleTable1 == null) activityCycleTableQueries.insertActivityCycleToStorage(activityCycleTable);
    }

    private void saveBorrowersToLocalStorage(BorrowersTable borrowersTable) {
        BorrowersTable borrowersTable1 = borrowersTableQueries.loadSingleBorrower(borrowersTable.getBorrowersId());
        if(borrowersTable1 == null) borrowersTableQueries.insertBorrowersToStorage(borrowersTable);
    }

    public void loadBorrowersToUI(){
        List<BorrowersTable> borrowersTables = borrowersTableQueries.loadAllBorrowersOrderByLastName();

        ((BorrowerActivity) activity).isSearch = false;

        if(fragment != null) {
            fragment.borrowerRecyclerAdapter = new BorrowerRecyclerAdapter(borrowersTables);
            fragment.borrowerRecyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));
            fragment.borrowerRecyclerView.setAdapter((fragment.borrowerRecyclerAdapter));
        }

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

                                        getActivityCycleData(doc.getId());
                                        saveBorrowersToLocalStorage(borrowersTable);
                                    }else{
                                        //Update local table if any changes
                                        updateTable(doc);
                                        getActivityCycleData(doc.getId());
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
                                removeRefresher();
                            }else{
                                Toast.makeText(activity, "Document is empty", Toast.LENGTH_SHORT).show();
                                removeRefresher();
                            }
                        }else{
                            Log.d("Borrower", "onComplete: "+task.getException().getMessage());
                            removeRefresher();
                        }
                    }
                });
    }

    private void updateActivityCycleTable(ActivityCycleTable doc) {
        activityCycleTableQueries.updateStorageAfterActivityCycleEnds(doc);
    }

    private void deleteBorrowerFromLocalStorage(BorrowersTable borowTab) {
        borrowersTableQueries.deleteBorrowersFromStorage(borowTab);
    }

    private void removeRefresher(){
        if(fragment != null) {
            fragment.swipeRefreshLayout.setRefreshing(false);
            fragment.swipeRefreshLayout.destroyDrawingCache();
            fragment.swipeRefreshLayout.clearAnimation();
        }
    }

    private void updateTable(DocumentSnapshot doc) {
        BorrowersTable borrowersTable = doc.toObject(BorrowersTable.class);
        borrowersTable.setBorrowersId(doc.getId());

        BorrowersTable currentlySaved = borrowersTableQueries.loadSingleBorrower(doc.getId());
        borrowersTable.setId(currentlySaved.getId());

        if(borrowersTable.getLastUpdatedAt().getTime() != currentlySaved.getLastUpdatedAt().getTime()){

            borrowersTableQueries.updateBorrowerDetails(borrowersTable);
            loadBorrowersToUI();
            Log.d("Borrower", "Borrower Detailed updated");

        }
    }
}
