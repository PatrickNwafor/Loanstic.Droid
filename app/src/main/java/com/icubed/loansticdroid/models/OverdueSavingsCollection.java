package com.icubed.loansticdroid.models;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.SavingsRepayment;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.cloudqueries.SavingsPlanCollectionQueries;
import com.icubed.loansticdroid.cloudqueries.GroupBorrowerQueries;
import com.icubed.loansticdroid.cloudqueries.SavingsPlanCollectionQueries;
import com.icubed.loansticdroid.cloudqueries.SavingsPlanTypeQueries;
import com.icubed.loansticdroid.cloudqueries.SavingsQueries;
import com.icubed.loansticdroid.cloudqueries.SavingsQueries;
import com.icubed.loansticdroid.fragments.CollectionFragments.DueCollectionFragment;
import com.icubed.loansticdroid.fragments.HomeFragments.DashboardFragment;
import com.icubed.loansticdroid.fragments.HomeFragments.MapFragment;
import com.icubed.loansticdroid.fragments.SavingsCollectionFragments.DueSavingsCollectionFragment;
import com.icubed.loansticdroid.fragments.SavingsCollectionFragments.OverdueSavingsCollectionFragment;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.BorrowersTableQueries;
import com.icubed.loansticdroid.localdatabase.CollectionTable;
import com.icubed.loansticdroid.localdatabase.SavingsPlanCollectionTable;
import com.icubed.loansticdroid.localdatabase.SavingsPlanCollectionTableQueries;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTableQueries;
import com.icubed.loansticdroid.localdatabase.SavingsPlanCollectionTableQueries;
import com.icubed.loansticdroid.localdatabase.SavingsPlanTypeTable;
import com.icubed.loansticdroid.localdatabase.SavingsPlanTypeTableQueries;
import com.icubed.loansticdroid.localdatabase.SavingsTableQueries;
import com.icubed.loansticdroid.localdatabase.SavingsTable;
import com.icubed.loansticdroid.localdatabase.SavingsTableQueries;
import com.icubed.loansticdroid.util.BitmapUtil;
import com.icubed.loansticdroid.util.CustomDialogBox.PaymentDialogBox;
import com.icubed.loansticdroid.util.DateUtil;
import com.icubed.loansticdroid.util.MapInfoWindow.OnInfoWindowElemTouchListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class OverdueSavingsCollection {

    private SavingsTableQueries savingsTableQueries;
    private SavingsQueries savingsQueries;
    private SavingsPlanCollectionQueries savingsPlanCollectionQueries;
    private SavingsPlanCollectionTableQueries savingsPlanCollectionTableQueries;
    private BorrowersTableQueries borrowersTableQueries;
    private BorrowersQueries borrowersQueries;
    private SavingsPlanTypeQueries savingsPlanTypeQueries;
    private SavingsPlanTypeTableQueries savingsPlanTypeTableQueries;
    private int collectionSize;
    private int count;
    private int loadCount = 0;
    private int loadSize;

    private FragmentActivity fragmentActivity;
    private OverdueSavingsCollectionFragment fragment;
    private DashboardFragment dashboardFragment;
    private PaymentDialogBox paymentDialogBox;


    private static final String TAG = ".DueCollection";

    public OverdueSavingsCollection(Application application, FragmentActivity activity){

        savingsTableQueries = new SavingsTableQueries(application);
        savingsQueries = new SavingsQueries();
        savingsPlanCollectionQueries = new SavingsPlanCollectionQueries();
        savingsPlanCollectionTableQueries = new SavingsPlanCollectionTableQueries(application);
        borrowersTableQueries = new BorrowersTableQueries(application);
        borrowersQueries = new BorrowersQueries(activity.getApplicationContext());
        savingsPlanTypeQueries = new SavingsPlanTypeQueries();
        savingsPlanTypeTableQueries = new SavingsPlanTypeTableQueries(activity.getApplication());

        fragmentActivity = activity;
        //Updates UI
        FragmentManager fm = fragmentActivity.getSupportFragmentManager();
        fragment = (OverdueSavingsCollectionFragment) fm.findFragmentByTag("overdue");
        dashboardFragment = (DashboardFragment) fm.findFragmentByTag("dashboard");
        paymentDialogBox = new PaymentDialogBox(fragmentActivity);
    }

    /***********check if collection exist in storage********/
    public boolean doesCollectionExistInLocalStorage(){
        List<SavingsPlanCollectionTable> listOfcollections = savingsPlanCollectionTableQueries.loadAllCollections();

        if(listOfcollections.isEmpty()){
            return false;
        }
        return true;
    }

    /***************retrieve new due collection****************/
    public void retrieveNewDueCollectionData(){

        savingsPlanCollectionQueries.retrieveAllSavingsScheduleCollection()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: Success in retrieving data from server");
                            if(!task.getResult().isEmpty()){
                                count = 0;
                                collectionSize = task.getResult().size();

                                //to know the size of due collections today
                                for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                                    SavingsPlanCollectionTable savingsPlanCollectionTable = documentSnapshot.toObject(SavingsPlanCollectionTable.class);
                                    savingsPlanCollectionTable.setSavingsPlanCollectionId(documentSnapshot.getId());

                                    if (!savingsPlanCollectionTable.getSavingsCollectionDueDate().before(new Date())) {
                                        collectionSize--;
                                    }
                                }

                                //to check is due collection size is zero
                                if(collectionSize == 0){
                                    if(fragment != null) fragment.emptyCollection.setVisibility(View.VISIBLE);
                                    hideProgressBar();
                                    removeRefresher();
                                    Log.d(TAG, "onComplete: No due collections for today");
                                    return;
                                }

                                for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                                    SavingsPlanCollectionTable savingsPlanCollectionTable = documentSnapshot.toObject(SavingsPlanCollectionTable.class);
                                    savingsPlanCollectionTable.setSavingsPlanCollectionId(documentSnapshot.getId());

                                    if(savingsPlanCollectionTable.getSavingsCollectionDueDate().before(new Date()) &&
                                            !savingsPlanCollectionTable.getIsSavingsCollected()) {
                                        Log.d(TAG, "onComplete: "+savingsPlanCollectionTable.toString());
                                        getSavingsData(savingsPlanCollectionTable.getSavingsId(), savingsPlanCollectionTable.getSavingsPlanCollectionId());
                                        saveNewCollectionToLocalStorage(savingsPlanCollectionTable);
                                    }
                                }

                            }else{
                                if(fragment != null) fragment.emptyCollection.setVisibility(View.VISIBLE);
                                hideProgressBar();
                                removeRefresher();
                                Log.d(TAG, "onComplete: No due collections for today");
                            }
                        }else{
                            hideProgressBar();
                            removeRefresher();
                            Log.d(TAG, "onComplete: Failed to retrieve new due collections");
                        }
                    }
                });
    }

    private void getSavingsData(String savingsId, final String collectionId) {
        SavingsTable savings = savingsTableQueries.loadSingleSavings(savingsId);
        if(savings != null){
            getSavingsType(savings, collectionId);
        }else {
            savingsQueries.retrieveSingleSavings(savingsId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: Savings retrieved");
                        if (task.getResult().exists()) {

                            SavingsTable savingsTable = task.getResult().toObject(SavingsTable.class);
                            savingsTable.setSavingsId(task.getResult().getId());

                            saveSavingsToLocalStorage(savingsTable);
                            getSavingsType(savingsTable, collectionId);
                        }
                    } else {
                        hideProgressBar();
                        removeRefresher();
                        Log.d(TAG, "onComplete: Savings retrieved Failed");
                    }
                }
            });
        }
    }

    private void getSavingsType(final SavingsTable savings, final String collectionId) {
        if(savings.getSavingsPlanTypeId() != null){
            SavingsPlanTypeTable savingsPlanTypeTable = savingsPlanTypeTableQueries.loadSingleSavingsPlanType(savings.getSavingsPlanTypeId());
            if(savingsPlanTypeTable == null){

                savingsPlanTypeQueries.retrieveSingleSavingsPlanType(savings.getSavingsPlanTypeId())
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){

                                    SavingsPlanTypeTable savingsPlanTypeTable1 = task.getResult().toObject(SavingsPlanTypeTable.class);
                                    savingsPlanTypeTable1.setSavingsPlanTypeId(task.getResult().getId());

                                    savePlanTypeToLocal(savingsPlanTypeTable1);
                                    getBorrowersDetails(savings.getBorrowerId(), collectionId);

                                }else {
                                    Log.d(TAG, "onComplete: "+task.getException().getMessage());
                                }
                            }
                        });

            }else getBorrowersDetails(savings.getBorrowerId(), collectionId);

        }else getBorrowersDetails(savings.getBorrowerId(), collectionId);
    }

    private void savePlanTypeToLocal(SavingsPlanTypeTable savingsPlanTypeTable) {
        SavingsPlanTypeTable savingsPlanTypeTable1 = savingsPlanTypeTableQueries.loadSingleSavingsPlanType(savingsPlanTypeTable.getSavingsPlanTypeId());
        if(savingsPlanTypeTable1 == null) savingsPlanTypeTableQueries.insertSavingsPlanTypeToStorage(savingsPlanTypeTable);
    }

    private void hideProgressBar(){
        if(fragment != null)
            fragment.hideProgressBar();
    }

    private void getBorrowersDetails(String borrowerId, final String collectionId) {

        BorrowersTable borrower = borrowersTableQueries.loadSingleBorrower(borrowerId);

        if(borrower != null){
            count++;
            saveBorrowerToLocalStorage(borrower);
        }else {
            borrowersQueries.retrieveSingleBorrowers(borrowerId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: BorrowersQueries retrieved");
                        if (task.getResult().exists()) {
                            count++;
                            //Set local storage table
                            BorrowersTable borrowersTable = task.getResult().toObject(BorrowersTable.class);
                            borrowersTable.setBorrowersId(task.getResult().getId());

                            saveBorrowerToLocalStorage(borrowersTable);
                            getBorrowerImage(borrowersTable);
                        }
                    } else {
                        hideProgressBar();
                        removeRefresher();
                        Log.d(TAG, "onComplete: BorrowersQueries retrieved Failed");
                    }
                }
            });
        }
    }

    private void getBorrowerImage(BorrowersTable borrowersTable) {
        final BorrowersTable table = borrowersTableQueries.loadSingleBorrower(borrowersTable.getBorrowersId());

        if(table.getBorrowerImageByteArray() == null){

            BitmapUtil.getImageWithGlide(fragmentActivity, table.getProfileImageUri())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            table.setBorrowerImageByteArray(BitmapUtil.getBytesFromBitmapInJPG(resource, 100));
                            borrowersTableQueries.updateBorrowerDetails(table);
                        }
                    });

        }
    }

    /***********Save new borrowersQueries to storage********************/
    public void saveBorrowerToLocalStorage(BorrowersTable borrowersTable) {
        BorrowersTable allBorrowers = borrowersTableQueries.loadSingleBorrower(borrowersTable.getBorrowersId());

        if(allBorrowers == null){borrowersTableQueries.insertBorrowersToStorage(borrowersTable);}

        if(count == collectionSize){
            getDueCollectionData();
            count = 0;
        }
    }

    /****************Save savingsQueries to storage************************/
    public void saveSavingsToLocalStorage(SavingsTable savingsTable) {
        SavingsTable savingssTables = savingsTableQueries.loadSingleSavings(savingsTable.getSavingsId());

        if(savingssTables == null){
            savingsTableQueries.insertSavingsToStorage(savingsTable);
        }
    }

    /**********************Sae new collections to storage************/
    public void saveNewCollectionToLocalStorage(SavingsPlanCollectionTable collectionTable) {
        SavingsPlanCollectionTable collectionTable1 = savingsPlanCollectionTableQueries.loadSingleCollection(collectionTable.getSavingsPlanCollectionId());
        if (collectionTable1 == null) savingsPlanCollectionTableQueries.insertCollectionToStorage(collectionTable);
    }

    /*******************retrieve all collections in storage*************/
    public List<SavingsPlanCollectionTable> retrieveCollectionToLocalStorage(){

        if(doesCollectionExistInLocalStorage()) {
            return savingsPlanCollectionTableQueries.loadAllCollections();
        }
        return null;
    }

    /**************Retrieves data to show on slideuppanel************/
    public void getDueCollectionData(){
        List<SavingsPlanCollectionTable> collectionTables = savingsPlanCollectionTableQueries.loadAllOverDueCollection();

        if(fragment != null) {
            fragment.savingsDetailsList.clear();
        }

        loadCount = 0;

        if(!collectionTables.isEmpty()) {

            loadSize = collectionTables.size();
            if(fragment != null) fragment.emptyCollection.setVisibility(View.GONE);

            for (SavingsPlanCollectionTable savingsPlanCollectionTable : collectionTables) {

                SavingsDetails savingsDetails = new SavingsDetails();
                savingsDetails.setSavingsPlanCollectionTable(savingsPlanCollectionTable);

                SavingsTable savings = savingsTableQueries.loadSingleSavings(savingsPlanCollectionTable.getSavingsId());

                if(savings != null) {
                    savingsDetails.setSavingsTable(savings);

                    BorrowersTable borrowersTable = borrowersTableQueries.loadSingleBorrower(savings.getBorrowerId());

                    if(borrowersTable != null) {

                        savingsDetails.setBorrowersTable(borrowersTable);

                        loadCount++;
                        if (fragment != null) {
                            fragment.savingsDetailsList.add(savingsDetails);
                            fragment.slideUpPanelRecyclerAdapter.notifyDataSetChanged();

                            if(loadCount == loadSize){
                                hideProgressBar();
                                removeRefresher();
                            }
                        }

                        Log.d(TAG, "getDueCollectionData: "+savingsDetails.getSavingsPlanCollectionTable().toString());
                    } else getSingleBorrower(savings.getBorrowerId(), savingsDetails, collectionTables);

                } else getSingleSavings(savingsPlanCollectionTable.getSavingsId(), savingsDetails, collectionTables);
            }
        }else{
            if(fragment != null)
                fragment.emptyCollection.setVisibility(View.VISIBLE);
            hideProgressBar();
        }

    }

    private void getSingleSavings(String savingsId, final SavingsDetails savingsDetails, final List<SavingsPlanCollectionTable> collectionTables) {
        savingsQueries.retrieveSingleSavings(savingsId)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Savings retrieved");
                            if (task.getResult().exists()) {

                                SavingsTable savingsTable = task.getResult().toObject(SavingsTable.class);
                                savingsTable.setSavingsId(task.getResult().getId());

                                saveSavingsToLocalStorage(savingsTable);
                                savingsDetails.setSavingsTable(savingsTable);

                                BorrowersTable borrowersTable = borrowersTableQueries.loadSingleBorrower(savingsTable.getBorrowerId());

                                if(borrowersTable != null) {

                                    savingsDetails.setBorrowersTable(borrowersTable);

                                    loadCount++;
                                    if (fragment != null) {
                                        fragment.savingsDetailsList.add(savingsDetails);
                                        fragment.slideUpPanelRecyclerAdapter.notifyDataSetChanged();

                                        if(loadCount == loadSize){
                                            hideProgressBar();
                                            removeRefresher();
                                        }
                                    }
                                } else getSingleBorrower(savingsTable.getBorrowerId(), savingsDetails, collectionTables);
                            }
                        } else {
                            Log.d(TAG, "onComplete: Savings retrieved Failed");
                        }
                    }
                });
    }

    private void getSingleBorrower(String borrowerId, final SavingsDetails savingsDetails, final List<SavingsPlanCollectionTable> collectionTables) {
        borrowersQueries.retrieveSingleBorrowers(borrowerId)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: BorrowersQueries retrieved");
                            if (task.getResult().exists()) {
                                count++;
                                //Set local storage table
                                BorrowersTable borrowersTable = task.getResult().toObject(BorrowersTable.class);
                                borrowersTable.setBorrowersId(task.getResult().getId());

                                saveSingleNewBorrowerToLocal(borrowersTable);
                                getBorrowerImage(borrowersTable);

                                savingsDetails.setBorrowersTable(borrowersTable);

                                loadCount++;
                                if (fragment != null) {
                                    fragment.savingsDetailsList.add(savingsDetails);
                                    fragment.slideUpPanelRecyclerAdapter.notifyDataSetChanged();
                                    if(loadCount == loadSize){
                                        hideProgressBar();
                                        removeRefresher();
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, "onComplete: BorrowersQueries retrieved Failed");
                        }
                    }
                });
    }

    private void saveSingleNewBorrowerToLocal(BorrowersTable borrowersTable) {
        BorrowersTable allBorrowers = borrowersTableQueries.loadSingleBorrower(borrowersTable.getBorrowersId());
        if(allBorrowers == null){borrowersTableQueries.insertBorrowersToStorage(borrowersTable);}
    }

    /***********************retrieve all collection and comparing to local***********/
    public void retrieveDueCollectionToLocalStorageAndCompareToCloud(){
        final List<SavingsPlanCollectionTable> localCollection = retrieveCollectionToLocalStorage();

        count = 0;

        savingsPlanCollectionQueries.retrieveAllSavingsScheduleCollection()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: Success in retrieving data from server");
                            if(!task.getResult().isEmpty()){

                                List<SavingsPlanCollectionTable> newCol = new ArrayList<>();
                                List<SavingsPlanCollectionTable> oldCol = new ArrayList<>();

                                //to separate old collections from new ones
                                for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){

                                    SavingsPlanCollectionTable savingsPlanCollectionTable = documentSnapshot.toObject(SavingsPlanCollectionTable.class);
                                    savingsPlanCollectionTable.setSavingsPlanCollectionId(documentSnapshot.getId());

                                    Log.d(TAG, "onComplete: "+savingsPlanCollectionTable.toString());

                                    boolean newData = true;
                                    for(SavingsPlanCollectionTable colTab : localCollection){

                                        if(colTab.getSavingsPlanCollectionId().equals(documentSnapshot.getId())){
                                            newData = false;
                                            break;
                                        }
                                    }

                                    if(newData) newCol.add(savingsPlanCollectionTable);
                                    else oldCol.add(savingsPlanCollectionTable);
                                }

                                collectionSize = newCol.size();
                                Log.d(TAG, "onComplete: "+collectionSize);

                                //to get only over due collections size
                                for (SavingsPlanCollectionTable collectionTable : newCol) {
                                    if(!collectionTable.getIsSavingsCollected()){
                                        if (!collectionTable.getSavingsCollectionDueDate().before(new Date())) {
                                            collectionSize--;
                                        }
                                    }else collectionSize--;
                                }

                                Log.d(TAG, "onComplete: "+collectionSize);

                                if(collectionSize == 0){
                                    removeRefresher();
                                    Toast.makeText(fragmentActivity, "No new due collection", Toast.LENGTH_SHORT).show();
                                }


                                //dealing with new collection
                                for (SavingsPlanCollectionTable savingsPlanCollectionTable : newCol) {
                                    if(savingsPlanCollectionTable.getSavingsCollectionDueDate().before(new Date()) &&
                                            !savingsPlanCollectionTable.getIsSavingsCollected()) {
                                        getSavingsData(savingsPlanCollectionTable.getSavingsId(), savingsPlanCollectionTable.getSavingsPlanCollectionId());
                                        saveNewCollectionToLocalStorage(savingsPlanCollectionTable);
                                    }
                                }

                                //checking old collections for update
                                for (SavingsPlanCollectionTable savingsPlanCollectionTable : oldCol) {
                                    updateTable(savingsPlanCollectionTable);
                                }

                            }else{
                                removeRefresher();
                                Log.d(TAG, "onComplete: No New due collections for today");
                            }
                        }else{
                            removeRefresher();
                            Log.d(TAG, "onComplete: Failed to retrieve new due collections");
                        }
                    }
                });
    }

    private void removeRefresher(){
        if(fragment != null) {
            fragment.swipeRefreshLayout.setRefreshing(false);
            fragment.swipeRefreshLayout.destroyDrawingCache();
            fragment.swipeRefreshLayout.clearAnimation();
        }
    }

    private void updateTable(SavingsPlanCollectionTable savingsPlanCollectionTable) {

        SavingsPlanCollectionTable currentlySaved = savingsPlanCollectionTableQueries.loadSingleCollection(savingsPlanCollectionTable.getSavingsPlanCollectionId());
        savingsPlanCollectionTable.setId(currentlySaved.getId());

        if(savingsPlanCollectionTable.getLastUpdatedAt().getTime() != currentlySaved.getLastUpdatedAt().getTime()){

            savingsPlanCollectionTableQueries.updateCollection(savingsPlanCollectionTable);
            Log.d("DueCollection", "DueCollection Detailed updated");

        }
    }

}
