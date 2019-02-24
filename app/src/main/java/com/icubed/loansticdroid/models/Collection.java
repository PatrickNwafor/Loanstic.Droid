package com.icubed.loansticdroid.models;

import android.app.Application;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

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

public class Collection {

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

    private static final String TAG = ".collection";

    public Collection(Application application, FragmentActivity activity){
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

                                    if((DateUtil.dateString(collectionTable.getCollectionDueDate()).equals(DateUtil.dateString(new Date())) &&
                                            !collectionTable.getIsDueCollected()) || collectionTable.getCollectionDueDate().before(new Date())) {
                                        Log.d(TAG, "onComplete: "+collectionTable.toString());
                                        getLoansData(collectionTable.getLoanId(), collectionTable.getCollectionId());
                                        saveNewCollectionToLocalStorage(collectionTable);
                                    }else collectionSize--;
                                }

                            }else{
                                Log.d(TAG, "onComplete: No due collections for today");
                            }
                        }else{
                            Log.d(TAG, "onComplete: Failed to retrieve new due collections");
                        }
                    }
                });
    }

    private void getLoansData(String loanId, final String collectionId) {
        List<LoansTable> loan = loanTableQueries.loadSingleLoanList(loanId);
        if(!loan.isEmpty()){
            if (loan.get(0).getBorrowerId() != null) getBorrowersDetails(loan.get(0).getBorrowerId(), collectionId);
            else getGroupDetails(loan.get(0).getGroupId(), collectionId);
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
                        Log.d(TAG, "onComplete: Loan retrieved Failed");
                    }
                }
            });
        }
    }

    private void getGroupDetails(final String groupId, final String collectionId) {
        List<GroupBorrowerTable> group = groupBorrowerTableQueries.loadSingleBorrowerGroupList(groupId);

        if(!group.isEmpty()){
            count++;
            if (!isDueCollectionSingle) {
                saveGroupToLocalStorage(group.get(0));
            } else {
                saveSingleGroupToLocalStorage(group.get(0), collectionId);
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
                        Log.d(TAG, "onComplete: " + task.getException().getMessage());
                    }
                }
            });
        }
    }

    private void saveSingleGroupToLocalStorage(GroupBorrowerTable groupBorrowerTable, String collectionId) {

        List<GroupBorrowerTable> allGroups = groupBorrowerTableQueries.loadSingleBorrowerGroupList(groupBorrowerTable.getGroupId());

        if(allGroups.isEmpty()){
            groupBorrowerTableQueries.insertGroupToStorage(groupBorrowerTable);
        }

        getSingleDueCollectionData(collectionId);
        isDueCollectionSingle = false;
    }

    private void saveGroupToLocalStorage(GroupBorrowerTable groupBorrowerTable) {
        List<GroupBorrowerTable> allGroups = groupBorrowerTableQueries.loadSingleBorrowerGroupList(groupBorrowerTable.getGroupId());

        if(allGroups.isEmpty()){
            groupBorrowerTableQueries.insertGroupToStorage(groupBorrowerTable);
        }

        if(count == collectionSize){
            getDueCollectionData();
            count = 0;
        }
    }

    private void getBorrowersDetails(String borrowerId, final String collectionId) {

        List<BorrowersTable> borrower = borrowersTableQueries.loadSingleBorrowerList(borrowerId);

        if(!borrower.isEmpty()){
            count++;
            if (!isDueCollectionSingle) {
                saveBorrowerToLocalStorage(borrower.get(0));
            } else {
                saveSingleBorrowerToLocalStorage(borrower.get(0), collectionId);
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

        List<BorrowersTable> allBorrowers = borrowersTableQueries.loadAllBorrowers();

        Boolean doesLoanAlreadyExist = false;

        for(BorrowersTable borrower : allBorrowers){
            if(borrower.getBorrowersId().equals(borrowersTable.getBorrowersId())){
                doesLoanAlreadyExist = true;
                break;
            }
        }

        if(!doesLoanAlreadyExist) {
            borrowersTableQueries.insertBorrowersToStorage(borrowersTable);
        }

        if(count == collectionSize){
            getDueCollectionData();
            count = 0;
        }
    }

    public void saveSingleBorrowerToLocalStorage(BorrowersTable borrowersTable, String collectionId){
        List<BorrowersTable> allBorrowers = borrowersTableQueries.loadAllBorrowers();

        Boolean doesLoanAlreadyExist = false;

        for(BorrowersTable borrower : allBorrowers){
            if(borrower.getBorrowersId().equals(borrowersTable.getBorrowersId())){
                doesLoanAlreadyExist = true;
                break;
            }
        }

        if(!doesLoanAlreadyExist) {
            borrowersTableQueries.insertBorrowersToStorage(borrowersTable);
        }

        getSingleDueCollectionData(collectionId);
        isDueCollectionSingle = false;
    }

    /****************Save loansQueries to storage************************/
    public void saveLoanToLocalStorage(LoansTable loansTable) {
        List<LoansTable> loansTables = loanTableQueries.loadAllLoans();

        for(LoansTable loan : loansTables){
            if(loan.getLoanId().equals(loansTable.getLoanId())){
                return;
            }
        }

        loanTableQueries.insertLoanToStorage(loansTable);
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
        List<CollectionTable>[] collectionTables = collectionTableQueries.loadAllDueCollections();

        //Updates UI
        FragmentManager fm = fragmentActivity.getSupportFragmentManager();
        MapFragment fragment = (MapFragment) fm.findFragmentByTag("home");

        fragment.dueCollectionList.clear();
        fragment.overDueCollectionList.clear();
        drawCollectionMarker(collectionTables[0]);

        if(!collectionTables[0].isEmpty()) {
            for (CollectionTable collectionTable : collectionTables[0]) {

                DueCollectionDetails dueCollectionDetails = new DueCollectionDetails();
                dueCollectionDetails.setDueAmount(collectionTable.getCollectionDueAmount());
                dueCollectionDetails.setCollectionNumber(collectionTable.getCollectionNumber());
                dueCollectionDetails.setDueCollectionDate(DateUtil.dateString(collectionTable.getCollectionDueDate()));
                dueCollectionDetails.setIsDueCollected(collectionTable.getIsDueCollected());

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

                fragment.dueCollectionList.add(dueCollectionDetails);
                fragment.slideUpPanelRecyclerAdapter.notifyDataSetChanged();
            }
        }

        if(!collectionTables[1].isEmpty()){

            //Log.d(TAG, "getDueCollectionData: "+collectionTables[1].toString());

            for (CollectionTable collectionTable : collectionTables[1]) {

                DueCollectionDetails dueCollectionDetails = new DueCollectionDetails();
                dueCollectionDetails.setDueAmount(collectionTable.getCollectionDueAmount());
                dueCollectionDetails.setCollectionNumber(collectionTable.getCollectionNumber());
                dueCollectionDetails.setDueCollectionDate(DateUtil.dateString(collectionTable.getCollectionDueDate()));
                dueCollectionDetails.setIsDueCollected(collectionTable.getIsDueCollected());

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

                fragment.overDueCollectionList.add(dueCollectionDetails);
                fragment.overDueAdapter.notifyDataSetChanged();
            }
        }

        fragment.hideProgressBar();

    }

    private void drawCollectionMarker(final List<CollectionTable> collectionTable) {
        FragmentManager fm = fragmentActivity.getSupportFragmentManager();
        final MapFragment fragment = (MapFragment) fm.findFragmentByTag("home");

        final ArrayList<Marker> markers = new ArrayList<>();
        fragment.mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                fragment.mGoogleMap.clear();

                if(!collectionTable.isEmpty()){
                    for (CollectionTable table : collectionTable) {
                        LoansTable loan = loanTableQueries.loadSingleLoan(table.getLoanId());
                        if(loan.getBorrowerId() != null) {
                            BorrowersTable borrowersTable = borrowersTableQueries.loadSingleBorrower(loan.getBorrowerId());
                            String markerTitle = borrowersTable.getLastName() +" "+borrowersTable.getFirstName();
                            LatLng latLng = new LatLng(borrowersTable.getBorrowerLocationLatitude(), borrowersTable.getBorrowerLocationLongitude());

                            MarkerOptions markerOptions = new MarkerOptions();

                            //adding markerOptions properties for driver
                            markerOptions.position(latLng);
                            markerOptions.title(markerTitle);
                            markerOptions.anchor(0.5f, 0.5f);
                            if(borrowersTable.getBorrowerImageByteArray() == null) markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                            else markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapUtil.getBitMapFromBytes(borrowersTable.getBorrowerImageByteArray())));
                            markers.add(fragment.mGoogleMap.addMarker(markerOptions));
                        }else {
                            GroupBorrowerTable groupBorrowerTable = groupBorrowerTableQueries.loadSingleBorrowerGroup(loan.getGroupId());
                            String markerTitle = groupBorrowerTable.getGroupName();
                            LatLng latLng = new LatLng(groupBorrowerTable.getGroupLocationLatitude(), groupBorrowerTable.getGroupLocationLongitude());
                            //markers.clear();

                            MarkerOptions markerOptions = new MarkerOptions();

                            //adding markerOptions properties for driver
                            markerOptions.position(latLng);
                            markerOptions.title(markerTitle);
                            markerOptions.anchor(0.5f, 0.5f);
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                            markers.add(fragment.mGoogleMap.addMarker(markerOptions));
                        }
                    }

                    markers.add(fragment.mGoogleMap.addMarker(fragment.markerOptions));
                    fragment.moveCamera(markers);

                }else{
                    fragment.drawMarker(fragment.markerOptions);
                }
            }
        });
    }

    public void getSingleDueCollectionData(String collectionId){
        CollectionTable collectionTable = collectionTableQueries.loadSingleCollection(collectionId);

        //Updates UI
        FragmentManager fm = fragmentActivity.getSupportFragmentManager();
        MapFragment fragment = (MapFragment) fm.findFragmentByTag("home");

        DueCollectionDetails dueCollectionDetails = new DueCollectionDetails();
        dueCollectionDetails.setDueAmount(collectionTable.getCollectionDueAmount());
        dueCollectionDetails.setCollectionNumber(collectionTable.getCollectionNumber());
        dueCollectionDetails.setDueCollectionDate(DateUtil.dateString(collectionTable.getCollectionDueDate()));
        dueCollectionDetails.setIsDueCollected(collectionTable.getIsDueCollected());

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

        if(DateUtil.dateString(collectionTable.getCollectionDueDate()).equals(DateUtil.dateString(new Date()))){
            fragment.dueCollectionList.add(dueCollectionDetails);
            fragment.slideUpPanelRecyclerAdapter.notifyDataSetChanged();
        }else if(collectionTable.getCollectionDueDate().before(new Date())){
            fragment.overDueCollectionList.add(dueCollectionDetails);
            fragment.overDueAdapter.notifyDataSetChanged();
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

                                        if((DateUtil.dateString(collectionTable.getCollectionDueDate()).equals(DateUtil.dateString(new Date())) &&
                                                !collectionTable.getIsDueCollected()) || collectionTable.getCollectionDueDate().before(new Date())) {
                                            saveNewCollectionToLocalStorage(collectionTable);
                                            getLoansData(collectionTable.getLoanId(), collectionTable.getCollectionId());
                                        }
                                    }else {
                                        updateTable(documentSnapshot);
                                    }
                                }

                            }else{
                                Log.d(TAG, "onComplete: No New due collections for today");
                            }
                        }else{
                            Log.d(TAG, "onComplete: Failed to retrieve new due collections");
                        }
                    }
                });
    }

    private void updateTable(DocumentSnapshot doc) {
        CollectionTable collectionTable = doc.toObject(CollectionTable.class);
        collectionTable.setCollectionId(doc.getId());

        CollectionTable currentlySaved = collectionTableQueries.loadSingleCollection(doc.getId());
        collectionTable.setId(currentlySaved.getId());

        if(collectionTable.getLastUpdatedAt().getTime() != currentlySaved.getLastUpdatedAt().getTime()){

            collectionTableQueries.updateCollection(collectionTable);
            getDueCollectionData();
            Log.d("Collection", "Collection Detailed updated");

        }
    }
}
