package com.icubed.loansticdroid.models;

import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

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
import com.icubed.loansticdroid.activities.LoanRepayment;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.cloudqueries.CollectionQueries;
import com.icubed.loansticdroid.cloudqueries.GroupBorrowerQueries;
import com.icubed.loansticdroid.cloudqueries.LoansQueries;
import com.icubed.loansticdroid.fragments.CollectionFragments.DueCollectionFragment;
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
import com.icubed.loansticdroid.util.CustomDialogBox.PaymentDialogBox;
import com.icubed.loansticdroid.util.DateUtil;
import com.icubed.loansticdroid.util.MapInfoWindow.OnInfoWindowElemTouchListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DueCollection {

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
    private int loadCount = 0;
    private int loadSize;

    private Boolean isDueCollectionSingle;

    private FragmentActivity fragmentActivity;
    private DueCollectionFragment fragment;
    private MapFragment mapFragment;
    private PaymentDialogBox paymentDialogBox;

    private static final String TAG = ".DueCollection";

    public DueCollection(Application application, FragmentActivity activity){
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
        fragment = (DueCollectionFragment) fm.findFragmentByTag("due");
        mapFragment = (MapFragment) fm.findFragmentByTag("home");
        paymentDialogBox = new PaymentDialogBox(fragmentActivity);
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
                                            !collectionTable.getIsDueCollected())) {
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
    
    private void hideProgressBar(){
        if(fragment != null)
            fragment.hideProgressBar();
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
        List<CollectionTable> collectionTables = collectionTableQueries.loadAllDueCollections();

        if(fragment != null)
        fragment.dueCollectionList.clear();
        fragment.collectionTableList.clear();
        drawCollectionMarker(collectionTables);

        loadCount = 0;

        if(!collectionTables.isEmpty()) {

            loadSize = collectionTables.size();
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

                if(loan != null) {

                    if (loan.getBorrowerId() != null) {
                        BorrowersTable borrowersTable = borrowersTableQueries.loadSingleBorrower(loan.getBorrowerId());

                        if(borrowersTable != null) {
                            dueCollectionDetails.setFirstName(borrowersTable.getFirstName());
                            dueCollectionDetails.setLastName(borrowersTable.getLastName());
                            dueCollectionDetails.setWorkAddress(borrowersTable.getWorkAddress());
                            dueCollectionDetails.setBusinessName(borrowersTable.getBusinessName());
                            dueCollectionDetails.setImageUri(borrowersTable.getProfileImageUri());
                            dueCollectionDetails.setImageUriThumb(borrowersTable.getProfileImageThumbUri());
                            dueCollectionDetails.setImageByteArray(borrowersTable.getBorrowerImageByteArray());
                            dueCollectionDetails.setLatitude(borrowersTable.getBorrowerLocationLatitude());
                            dueCollectionDetails.setLongitude(borrowersTable.getBorrowerLocationLongitude());

                            loadCount++;
                            if (fragment != null) {
                                fragment.dueCollectionList.add(dueCollectionDetails);
                                fragment.slideUpPanelRecyclerAdapter.notifyDataSetChanged();

                                if(loadCount == loadSize){
                                    hideProgressBar();
                                    removeRefresher();
                                }
                            }
                        } else getSingleBorrower(loan.getBorrowerId(), dueCollectionDetails);

                    } else {
                        GroupBorrowerTable groupBorrowerTable = groupBorrowerTableQueries.loadSingleBorrowerGroup(loan.getGroupId());

                        if(groupBorrowerTable != null) {
                            dueCollectionDetails.setGroupName(groupBorrowerTable.getGroupName());
                            dueCollectionDetails.setLatitude(groupBorrowerTable.getGroupLocationLatitude());
                            dueCollectionDetails.setLongitude(groupBorrowerTable.getGroupLocationLongitude());
                            dueCollectionDetails.setWorkAddress(groupBorrowerTable.getMeetingLocation());

                            loadCount++;
                            if (fragment != null) {
                                fragment.dueCollectionList.add(dueCollectionDetails);
                                fragment.slideUpPanelRecyclerAdapter.notifyDataSetChanged();

                                if(loadCount == loadSize){
                                    hideProgressBar();
                                    removeRefresher();
                                }
                            }
                        } getSingleGroup(loan.getGroupId(), dueCollectionDetails);
                    }

                } else getSingleLoan(collectionTable.getLoanId(), dueCollectionDetails);
            }
        }else{
            if(fragment != null)
            fragment.emptyCollection.setVisibility(View.VISIBLE);
        }

    }

    private void getSingleLoan(String loanId, final DueCollectionDetails dueCollectionDetails) {
        loansQueries.retrieveSingleLoan(loanId)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Loan retrieved");
                            if (task.getResult().exists()) {

                                LoansTable loansTable = task.getResult().toObject(LoansTable.class);
                                loansTable.setLoanId(task.getResult().getId());

                                saveLoanToLocalStorage(loansTable);
                                if (loansTable.getBorrowerId() != null) {
                                    BorrowersTable borrowersTable = borrowersTableQueries.loadSingleBorrower(loansTable.getBorrowerId());

                                    if(borrowersTable != null) {
                                        dueCollectionDetails.setFirstName(borrowersTable.getFirstName());
                                        dueCollectionDetails.setLastName(borrowersTable.getLastName());
                                        dueCollectionDetails.setWorkAddress(borrowersTable.getWorkAddress());
                                        dueCollectionDetails.setBusinessName(borrowersTable.getBusinessName());
                                        dueCollectionDetails.setImageUri(borrowersTable.getProfileImageUri());
                                        dueCollectionDetails.setImageUriThumb(borrowersTable.getProfileImageThumbUri());
                                        dueCollectionDetails.setImageByteArray(borrowersTable.getBorrowerImageByteArray());
                                        dueCollectionDetails.setLatitude(borrowersTable.getBorrowerLocationLatitude());
                                        dueCollectionDetails.setLongitude(borrowersTable.getBorrowerLocationLongitude());

                                        loadCount++;
                                        if (fragment != null) {
                                            fragment.dueCollectionList.add(dueCollectionDetails);
                                            fragment.slideUpPanelRecyclerAdapter.notifyDataSetChanged();

                                            if(loadCount == loadSize){
                                                hideProgressBar();
                                                removeRefresher();
                                            }
                                        }
                                    } else getSingleBorrower(loansTable.getBorrowerId(), dueCollectionDetails);

                                } else {
                                    GroupBorrowerTable groupBorrowerTable = groupBorrowerTableQueries.loadSingleBorrowerGroup(loansTable.getGroupId());

                                    if(groupBorrowerTable != null) {
                                        dueCollectionDetails.setGroupName(groupBorrowerTable.getGroupName());
                                        dueCollectionDetails.setLatitude(groupBorrowerTable.getGroupLocationLatitude());
                                        dueCollectionDetails.setLongitude(groupBorrowerTable.getGroupLocationLongitude());
                                        dueCollectionDetails.setWorkAddress(groupBorrowerTable.getMeetingLocation());

                                        loadCount++;
                                        if (fragment != null) {
                                            fragment.dueCollectionList.add(dueCollectionDetails);
                                            fragment.slideUpPanelRecyclerAdapter.notifyDataSetChanged();

                                            if(loadCount == loadSize){
                                                hideProgressBar();
                                                removeRefresher();
                                            }
                                        }
                                    } getSingleGroup(loansTable.getGroupId(), dueCollectionDetails);
                                }
                            }
                        } else {
                            Log.d(TAG, "onComplete: Loan retrieved Failed");
                        }
                    }
                });
    }

    private void getSingleGroup(final String groupId, final DueCollectionDetails dueCollectionDetails) {
        groupBorrowerQueries.retrieveSingleBorrowerGroup(groupId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    count++;
                    GroupBorrowerTable groupBorrowerTable = task.getResult().toObject(GroupBorrowerTable.class);
                    groupBorrowerTable.setGroupId(task.getResult().getId());

                    saveSingleNewGroupLocal(groupBorrowerTable);
                    dueCollectionDetails.setGroupName(groupBorrowerTable.getGroupName());
                    dueCollectionDetails.setLatitude(groupBorrowerTable.getGroupLocationLatitude());
                    dueCollectionDetails.setLongitude(groupBorrowerTable.getGroupLocationLongitude());
                    dueCollectionDetails.setWorkAddress(groupBorrowerTable.getMeetingLocation());

                    loadCount++;
                    if (fragment != null) {
                        fragment.dueCollectionList.add(dueCollectionDetails);
                        fragment.slideUpPanelRecyclerAdapter.notifyDataSetChanged();
                        if(loadCount == loadSize){
                            hideProgressBar();
                            removeRefresher();
                        }
                    }
                } else {
                    Log.d(TAG, "onComplete: " + task.getException().getMessage());
                }
            }
        });
    }

    private void saveSingleNewGroupLocal(GroupBorrowerTable groupBorrowerTable) {
        GroupBorrowerTable allGroups = groupBorrowerTableQueries.loadSingleBorrowerGroup(groupBorrowerTable.getGroupId());

        if(allGroups == null){
            groupBorrowerTableQueries.insertGroupToStorage(groupBorrowerTable);
        }
    }

    private void getSingleBorrower(String borrowerId, final DueCollectionDetails dueCollectionDetails) {
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
                                dueCollectionDetails.setFirstName(borrowersTable.getFirstName());
                                dueCollectionDetails.setLastName(borrowersTable.getLastName());
                                dueCollectionDetails.setWorkAddress(borrowersTable.getWorkAddress());
                                dueCollectionDetails.setBusinessName(borrowersTable.getBusinessName());
                                dueCollectionDetails.setImageUri(borrowersTable.getProfileImageUri());
                                dueCollectionDetails.setImageUriThumb(borrowersTable.getProfileImageThumbUri());
                                dueCollectionDetails.setImageByteArray(borrowersTable.getBorrowerImageByteArray());
                                dueCollectionDetails.setLatitude(borrowersTable.getBorrowerLocationLatitude());
                                dueCollectionDetails.setLongitude(borrowersTable.getBorrowerLocationLongitude());

                                loadCount++;
                                if (fragment != null) {
                                    fragment.dueCollectionList.add(dueCollectionDetails);
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

    private void drawCollectionMarker(final List<CollectionTable> collectionTable) {

        LayoutInflater layoutInflater = (LayoutInflater) fragmentActivity.getSystemService(fragmentActivity.LAYOUT_INFLATER_SERVICE);
        final View view = layoutInflater.inflate(R.layout.custom_marker_layout_collection, null);
        final CircleImageView circleImageView = view.findViewById(R.id.user_dp);

        final ArrayList<Marker> markers = new ArrayList<>();
        
        if(mapFragment.mGoogleMap != null) {
            mapFragment.mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mapFragment.mGoogleMap.clear();

                    if (!collectionTable.isEmpty()) {
                        for (final CollectionTable table : collectionTable) {
                            LoansTable loan = loanTableQueries.loadSingleLoan(table.getLoanId());
                            if (loan.getBorrowerId() != null) {
                                BorrowersTable borrowersTable = borrowersTableQueries.loadSingleBorrower(loan.getBorrowerId());
                                String markerTitle = borrowersTable.getLastName() + " " + borrowersTable.getFirstName();
                                LatLng latLng = new LatLng(borrowersTable.getBorrowerLocationLatitude(), borrowersTable.getBorrowerLocationLongitude());

                                MarkerOptions markerOptions = new MarkerOptions();

                                //adding markerOptions properties for driver
                                markerOptions.position(latLng);
                                markerOptions.title(markerTitle);
                                markerOptions.anchor(0.5f, 0.5f);
                                // sorting marker icon
                                if (borrowersTable.getBorrowerImageByteArray() == null) circleImageView.setImageResource(R.drawable.new_borrower);
                                else circleImageView.setImageBitmap(BitmapUtil.getBitMapFromBytes(borrowersTable.getBorrowerImageByteArray()));
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapUtil.convertViewsToBitmap(view)));
                                Marker mark = mapFragment.mGoogleMap.addMarker(markerOptions);

                                //custom info window collection button click listener
                                OnInfoWindowElemTouchListener infoButtonListener = new OnInfoWindowElemTouchListener(mapFragment.colBtn,
                                        mapFragment.getResources().getDrawable(R.color.whiteEnd),
                                        mapFragment.getResources().getDrawable(R.color.darkGrey)) {
                                    @Override
                                    protected void onClickConfirmed(View v, Marker marker) {
                                        // Here we can perform some action triggered after clicking the button
                                        newPayment(table);
                                    }
                                };

                                mapFragment.colBtn.setOnTouchListener(infoButtonListener);
                                mapFragment.colTitle.setText("Collection Number: "+table.getCollectionNumber());
                                infoButtonListener.setMarker(mark);

                                //adding marker to map
                                markers.add(mark);

                            } else {
                                GroupBorrowerTable groupBorrowerTable = groupBorrowerTableQueries.loadSingleBorrowerGroup(loan.getGroupId());
                                String markerTitle = groupBorrowerTable.getGroupName();
                                LatLng latLng = new LatLng(groupBorrowerTable.getGroupLocationLatitude(), groupBorrowerTable.getGroupLocationLongitude());
                                //markers.clear();

                                MarkerOptions markerOptions = new MarkerOptions();

                                //adding markerOptions properties for driver
                                markerOptions.position(latLng);
                                markerOptions.title(markerTitle);
                                markerOptions.anchor(0.5f, 0.5f);
                                circleImageView.setImageResource(R.drawable.new_group);
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapUtil.convertViewsToBitmap(view)));

                                Marker mark = mapFragment.mGoogleMap.addMarker(markerOptions);

                                //custom info window collection button click listener
                                OnInfoWindowElemTouchListener infoButtonListener = new OnInfoWindowElemTouchListener(mapFragment.colBtn,
                                        mapFragment.getResources().getDrawable(R.color.whiteEnd),
                                        mapFragment.getResources().getDrawable(R.color.darkGrey)) {
                                    @Override
                                    protected void onClickConfirmed(View v, Marker marker) {
                                        // Here we can perform some action triggered after clicking the button
                                        newPayment(table);
                                    }
                                };

                                mapFragment.colBtn.setOnTouchListener(infoButtonListener);
                                mapFragment.colTitle.setText("Collection Number: "+table.getCollectionNumber());
                                infoButtonListener.setMarker(mark);

                                markers.add(mark);
                            }
                        }

                        mapFragment.myMarker = mapFragment.mGoogleMap.addMarker(mapFragment.markerOptions);
                        markers.add(mapFragment.myMarker);
                        mapFragment.moveCamera(markers, null);

                    } else {
                        mapFragment.isNoCol = true;
                        mapFragment.getCurrentLocation();
                    }
                }
            });
        }
    }

    private void newPayment(final CollectionTable collectionTable){
        paymentDialogBox.setOnYesClicked(new PaymentDialogBox.OnButtonClick() {
            @Override
            public void onYesButtonClick() {
                Intent intent = new Intent(mapFragment.getContext(), LoanRepayment.class);
                intent.putExtra("collection", collectionTable);
                intent.putExtra("lastUpdatedAt", collectionTable.getLastUpdatedAt());
                intent.putExtra("dueDate", collectionTable.getCollectionDueDate());
                intent.putExtra("timestamp", collectionTable.getTimestamp());
                mapFragment.startActivity(intent);
            }
        });
        paymentDialogBox.show();
    }

    public void getSingleDueCollectionData(String collectionId){
        CollectionTable collectionTable = collectionTableQueries.loadSingleCollection(collectionId);

        //draw markers
        List<CollectionTable> collectionTables = collectionTableQueries.loadAllDueCollections();
        drawCollectionMarker(collectionTables);

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
            removeRefresher();
            fragment.emptyCollection.setVisibility(View.GONE);
            fragment.dueCollectionList.add(dueCollectionDetails);
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

                                        if((DateUtil.dateString(collectionTable.getCollectionDueDate()).equals(DateUtil.dateString(new Date())) &&
                                                !collectionTable.getIsDueCollected())) {
                                            saveNewCollectionToLocalStorage(collectionTable);
                                            getLoansData(collectionTable.getLoanId(), collectionTable.getCollectionId());
                                        }
                                    }else {
                                        updateTable(documentSnapshot);
                                    }
                                }
                                getDueCollectionData();
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
