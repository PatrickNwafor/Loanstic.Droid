package com.icubed.loansticdroid.models;

import android.app.Application;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

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
import com.icubed.loansticdroid.activities.MainActivity;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.cloudqueries.CollectionQueries;
import com.icubed.loansticdroid.cloudqueries.GroupBorrowerQueries;
import com.icubed.loansticdroid.cloudqueries.LoansQueries;
import com.icubed.loansticdroid.fragments.CollectionFragments.OverDueCollectionFragment;
import com.icubed.loansticdroid.fragments.HomeFragments.MapFragment;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.BorrowersTableQueries;
import com.icubed.loansticdroid.localdatabase.CollectionTable;
import com.icubed.loansticdroid.localdatabase.CollectionTableQueries;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTableQueries;
import com.icubed.loansticdroid.localdatabase.LoanTableQueries;
import com.icubed.loansticdroid.localdatabase.LoansTable;
import com.icubed.loansticdroid.util.BitmapUtil;
import com.icubed.loansticdroid.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OverDueCollection {

    private LoanTableQueries loanTableQueries;
    private LoansQueries loansQueries;
    private CollectionQueries collectionQueries;
    private CollectionTableQueries collectionTableQueries;
    private BorrowersTableQueries borrowersTableQueries;
    private BorrowersQueries borrowersQueries;
    private GroupBorrowerTableQueries groupBorrowerTableQueries;
    private GroupBorrowerQueries groupBorrowerQueries;
    private int collectionSize;
    private int count;

    private Boolean isDueCollectionSingle;

    private FragmentActivity fragmentActivity;
    private MapFragment mapFragment;
    private OverDueCollectionFragment fragment;

    private static final String TAG = ".collection";

    public OverDueCollection(Application application, FragmentActivity activity){
        loanTableQueries = new LoanTableQueries(application);
        loansQueries = new LoansQueries();
        collectionQueries = new CollectionQueries();
        collectionTableQueries = new CollectionTableQueries(application);
        borrowersTableQueries = new BorrowersTableQueries(application);
        borrowersQueries = new BorrowersQueries(activity.getApplicationContext());
        groupBorrowerTableQueries = new GroupBorrowerTableQueries(application);
        groupBorrowerQueries = new GroupBorrowerQueries();

        isDueCollectionSingle = false;

        fragmentActivity = activity;
        //Updates UI
        FragmentManager fm = fragmentActivity.getSupportFragmentManager();
        fragment = (OverDueCollectionFragment) fm.findFragmentByTag("overdue");
        mapFragment = (MapFragment) fm.findFragmentByTag("home");
    }

    /***********check if collection exist in storage********/
    public boolean doesCollectionExistInLocalStorage(){
        List<CollectionTable> listOFcollections = collectionTableQueries.loadAllCollections();

        if(listOFcollections.isEmpty()){
            return false;
        }
        return true;
    }

    /***************retrieve new due collection****************/
    public void retrieveNewDueCollectionData(){

        collectionQueries.retrieveAllCollection()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: Success in retrieving data from server");
                            if(!task.getResult().isEmpty()){
                                count = 0;
                                collectionSize = task.getResult().size();

                                for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                                    CollectionTable collectionTable = documentSnapshot.toObject(CollectionTable.class);
                                    collectionTable.setCollectionId(documentSnapshot.getId());

                                    if(collectionTable.getCollectionDueDate().before(new Date()) && !collectionTable.getIsDueCollected()) {
                                        Log.d(TAG, "onComplete: "+collectionTable.toString());
                                        getLoansData(collectionTable.getLoanId(), collectionTable.getCollectionId());
                                        saveNewCollectionToLocalStorage(collectionTable);
                                    }else collectionSize--;

                                    if(collectionSize == 0){
                                        if(fragment != null) fragment.emptyCollection.setVisibility(View.VISIBLE);
                                        hideProgressBar();
                                        removeRefresher();
                                        Log.d(TAG, "onComplete: No due collections for today");
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
    
    private void hideProgressBar(){
        if(fragment != null)
            fragment.hideProgressBar();
    }

    private void getLoansData(String loanId, final String collectionId) {
        LoansTable loan = loanTableQueries.loadSingleLoan(loanId);
        if(loan != null){
            if (loan.getBorrowerId() != null) getBorrowersDetails(loan.getBorrowerId(), collectionId);
            else getGroupDetails(loan.getGroupId(), collectionId);
        }else {
            loansQueries.retrieveSingleLoan(loanId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: Loan retrieved");
                        if (task.getResult().exists()) {

                            LoansTable loansTable = task.getResult().toObject(LoansTable.class);
                            loansTable.setLoanId(task.getResult().getId());

                            saveLoanToLocalStorage(loansTable);
                            if (loansTable.getBorrowerId() != null) getBorrowersDetails(loansTable.getBorrowerId(), collectionId);
                            else getGroupDetails(loansTable.getGroupId(), collectionId);
                        }
                    } else {
                        hideProgressBar();
                        removeRefresher();
                        Log.d(TAG, "onComplete: Loan retrieved Failed");
                    }
                }
            });
        }
    }

    private void getGroupDetails(final String groupId, final String collectionId) {
        GroupBorrowerTable group = groupBorrowerTableQueries.loadSingleBorrowerGroup(groupId);

        if(group != null){
            count++;
            if (!isDueCollectionSingle) {
                saveGroupToLocalStorage(group);
            } else {
                saveSingleGroupToLocalStorage(group, collectionId);
            }
        }else {
            groupBorrowerQueries.retrieveSingleBorrowerGroup(groupId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        count++;
                        GroupBorrowerTable groupBorrowerTable = task.getResult().toObject(GroupBorrowerTable.class);
                        groupBorrowerTable.setGroupId(groupId);

                        if (!isDueCollectionSingle) {
                            saveGroupToLocalStorage(groupBorrowerTable);
                        } else {
                            saveSingleGroupToLocalStorage(groupBorrowerTable, collectionId);
                        }
                    } else {
                        hideProgressBar();
                        removeRefresher();
                        Log.d(TAG, "onComplete: " + task.getException().getMessage());
                    }
                }
            });
        }
    }

    private void saveSingleGroupToLocalStorage(GroupBorrowerTable groupBorrowerTable, String collectionId) {

        GroupBorrowerTable allGroups = groupBorrowerTableQueries.loadSingleBorrowerGroup(groupBorrowerTable.getGroupId());

        if(allGroups == null){
            groupBorrowerTableQueries.insertGroupToStorage(groupBorrowerTable);
        }

        getSingleDueCollectionData(collectionId);
        isDueCollectionSingle = false;
    }

    private void saveGroupToLocalStorage(GroupBorrowerTable groupBorrowerTable) {
        GroupBorrowerTable allGroups = groupBorrowerTableQueries.loadSingleBorrowerGroup(groupBorrowerTable.getGroupId());

        if(allGroups == null){
            groupBorrowerTableQueries.insertGroupToStorage(groupBorrowerTable);
        }

        if(count == collectionSize){
            getDueCollectionData();
            count = 0;
        }
    }

    private void getBorrowersDetails(String borrowerId, final String collectionId) {

        BorrowersTable borrower = borrowersTableQueries.loadSingleBorrower(borrowerId);

        if(borrower != null){
            count++;
            if (!isDueCollectionSingle) {
                saveBorrowerToLocalStorage(borrower);
            } else {
                saveSingleBorrowerToLocalStorage(borrower, collectionId);
            }
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

                            if (!isDueCollectionSingle) {
                                saveBorrowerToLocalStorage(borrowersTable);
                            } else {
                                saveSingleBorrowerToLocalStorage(borrowersTable, collectionId);
                            }
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

    public void saveSingleBorrowerToLocalStorage(BorrowersTable borrowersTable, String collectionId){
        BorrowersTable allBorrowers = borrowersTableQueries.loadSingleBorrower(borrowersTable.getBorrowersId());

        if(allBorrowers == null){borrowersTableQueries.insertBorrowersToStorage(borrowersTable);}

        getSingleDueCollectionData(collectionId);
        isDueCollectionSingle = false;
    }

    /****************Save loansQueries to storage************************/
    public void saveLoanToLocalStorage(LoansTable loansTable) {
        LoansTable loansTables = loanTableQueries.loadSingleLoan(loansTable.getLoanId());

        if(loansTables == null){
            loanTableQueries.insertLoanToStorage(loansTable);
        }
    }

    /**********************Sae new collections to storage************/
    public void saveNewCollectionToLocalStorage(CollectionTable collectionTable) {
        collectionTableQueries.insertCollectionToStorage(collectionTable);
    }

    /*******************retrieve all collections in storage*************/
    public List<CollectionTable> retrieveCollectionToLocalStorage(){

        if(doesCollectionExistInLocalStorage()) {
            return collectionTableQueries.loadAllCollections();
        }
        return null;
    }

    /**************Retrieves data to show on slideuppanel************/
    public void getDueCollectionData(){
        List<CollectionTable> collectionTables = collectionTableQueries.loadAllOverDueCollection();

        if(fragment != null)
        fragment.overDueCollectionList.clear();
        fragment.collectionTableList.clear();

        if(!collectionTables.isEmpty()) {

            if(fragment != null) fragment.emptyCollection.setVisibility(View.GONE);

            for (CollectionTable collectionTable : collectionTables) {

                DueCollectionDetails dueCollectionDetails = new DueCollectionDetails();
                dueCollectionDetails.setDueAmount(collectionTable.getCollectionDueAmount());
                dueCollectionDetails.setCollectionNumber(collectionTable.getCollectionNumber());
                dueCollectionDetails.setDueCollectionDate(DateUtil.dateString(collectionTable.getCollectionDueDate()));
                dueCollectionDetails.setIsDueCollected(collectionTable.getIsDueCollected());
                dueCollectionDetails.setCollectionTable(collectionTable);
                dueCollectionDetails.setAmountPaid(collectionTable.getAmountPaid());
                fragment.collectionTableList.add(collectionTable);

                LoansTable loan = loanTableQueries.loadSingleLoan(collectionTable.getLoanId());

                if(loan.getBorrowerId() != null) {
                    BorrowersTable borrowersTable = borrowersTableQueries.loadSingleBorrower(loan.getBorrowerId());
                    dueCollectionDetails.setFirstName(borrowersTable.getFirstName());
                    dueCollectionDetails.setLastName(borrowersTable.getLastName());
                    dueCollectionDetails.setWorkAddress(borrowersTable.getWorkAddress());
                    dueCollectionDetails.setBusinessName(borrowersTable.getBusinessName());
                    dueCollectionDetails.setImageUri(borrowersTable.getProfileImageUri());
                    dueCollectionDetails.setImageUriThumb(borrowersTable.getProfileImageThumbUri());
                    dueCollectionDetails.setImageByteArray(borrowersTable.getBorrowerImageByteArray());
                    dueCollectionDetails.setLatitude(borrowersTable.getBorrowerLocationLatitude());
                    dueCollectionDetails.setLongitude(borrowersTable.getBorrowerLocationLongitude());
                }else{
                    GroupBorrowerTable groupBorrowerTable = groupBorrowerTableQueries.loadSingleBorrowerGroup(loan.getGroupId());
                    dueCollectionDetails.setGroupName(groupBorrowerTable.getGroupName());
                    dueCollectionDetails.setLatitude(groupBorrowerTable.getGroupLocationLatitude());
                    dueCollectionDetails.setLongitude(groupBorrowerTable.getGroupLocationLongitude());
                    dueCollectionDetails.setWorkAddress(groupBorrowerTable.getMeetingLocation());
                }

                if(fragment != null) {
                    fragment.overDueCollectionList.add(dueCollectionDetails);
                    fragment.slideUpPanelRecyclerAdapter.notifyDataSetChanged();
                }
            }
        }else {
            if(fragment != null)
            fragment.emptyCollection.setVisibility(View.VISIBLE);
        }


        hideProgressBar();
        removeRefresher();

    }

    public void getSingleDueCollectionData(String collectionId){
        CollectionTable collectionTable = collectionTableQueries.loadSingleCollection(collectionId);

        DueCollectionDetails dueCollectionDetails = new DueCollectionDetails();
        dueCollectionDetails.setDueAmount(collectionTable.getCollectionDueAmount());
        dueCollectionDetails.setCollectionNumber(collectionTable.getCollectionNumber());
        dueCollectionDetails.setDueCollectionDate(DateUtil.dateString(collectionTable.getCollectionDueDate()));
        dueCollectionDetails.setIsDueCollected(collectionTable.getIsDueCollected());
        dueCollectionDetails.setCollectionTable(collectionTable);
        dueCollectionDetails.setAmountPaid(collectionTable.getAmountPaid());
        fragment.collectionTableList.add(collectionTable);

        LoansTable loan = loanTableQueries.loadSingleLoan(collectionTable.getLoanId());

        if(loan.getBorrowerId() != null) {
            BorrowersTable borrowersTable = borrowersTableQueries.loadSingleBorrower(loan.getBorrowerId());
            dueCollectionDetails.setFirstName(borrowersTable.getFirstName());
            dueCollectionDetails.setLastName(borrowersTable.getLastName());
            dueCollectionDetails.setWorkAddress(borrowersTable.getWorkAddress());
            dueCollectionDetails.setBusinessName(borrowersTable.getBusinessName());
            dueCollectionDetails.setImageUri(borrowersTable.getProfileImageUri());
            dueCollectionDetails.setImageUriThumb(borrowersTable.getProfileImageThumbUri());
            dueCollectionDetails.setImageByteArray(borrowersTable.getBorrowerImageByteArray());
            dueCollectionDetails.setLatitude(borrowersTable.getBorrowerLocationLatitude());
            dueCollectionDetails.setLongitude(borrowersTable.getBorrowerLocationLongitude());
        }else{
            GroupBorrowerTable groupBorrowerTable = groupBorrowerTableQueries.loadSingleBorrowerGroup(loan.getGroupId());
            dueCollectionDetails.setGroupName(groupBorrowerTable.getGroupName());
            dueCollectionDetails.setLatitude(groupBorrowerTable.getGroupLocationLatitude());
            dueCollectionDetails.setLongitude(groupBorrowerTable.getGroupLocationLongitude());
            dueCollectionDetails.setWorkAddress(groupBorrowerTable.getMeetingLocation());
        }

        if(fragment != null) {
            fragment.emptyCollection.setVisibility(View.GONE);
            fragment.overDueCollectionList.add(dueCollectionDetails);
            fragment.slideUpPanelRecyclerAdapter.notifyDataSetChanged();
        }
    }

    /***********************retrieve all collection and comparing to local***********/
    public void retrieveDueCollectionToLocalStorageAndCompareToCloud(){
        final List<CollectionTable> localCollection = retrieveCollectionToLocalStorage();

        collectionQueries.retrieveAllCollection()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: Success in retrieving data from server");
                            if(!task.getResult().isEmpty()){

                                for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){

                                    Boolean doesDataExist = false;
                                    for(CollectionTable colTab : localCollection){
                                        if(colTab.getCollectionId().equals(documentSnapshot.getId())){
                                            doesDataExist = true;
                                            Log.d(TAG, "onComplete: collection id of "+documentSnapshot.getId()+" already exist");
                                            break;
                                        }
                                    }

                                    if(!doesDataExist) {
                                        Log.d(TAG, "onComplete: collection id of "+documentSnapshot.getId()+" does not exist");

                                        isDueCollectionSingle = true;
                                        CollectionTable collectionTable = documentSnapshot.toObject(CollectionTable.class);
                                        collectionTable.setCollectionId(documentSnapshot.getId());

                                        if(collectionTable.getCollectionDueDate().before(new Date()) && !collectionTable.getIsDueCollected()) {
                                            saveNewCollectionToLocalStorage(collectionTable);
                                            getLoansData(collectionTable.getLoanId(), collectionTable.getCollectionId());
                                        }
                                    }else {
                                        updateTable(documentSnapshot);
                                    }
                                }
                                getDueCollectionData();
                                removeRefresher();
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

    private void updateTable(DocumentSnapshot doc) {
        CollectionTable collectionTable = doc.toObject(CollectionTable.class);
        collectionTable.setCollectionId(doc.getId());

        CollectionTable currentlySaved = collectionTableQueries.loadSingleCollection(doc.getId());
        collectionTable.setId(currentlySaved.getId());

        if(collectionTable.getLastUpdatedAt().getTime() != currentlySaved.getLastUpdatedAt().getTime()){

            collectionTableQueries.updateCollection(collectionTable);
            Log.d("DueCollection", "DueCollection Detailed updated");

        }
    }
}
