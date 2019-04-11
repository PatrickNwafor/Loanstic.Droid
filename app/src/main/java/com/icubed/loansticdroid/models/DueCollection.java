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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.security.AccessController.getContext;

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

                                //to know the size of due collections today
                                for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                                    CollectionTable collectionTable = documentSnapshot.toObject(CollectionTable.class);
                                    collectionTable.setCollectionId(documentSnapshot.getId());

                                    if ((!DateUtil.dateString(collectionTable.getCollectionDueDate()).equals(DateUtil.dateString(new Date())))) {
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
                                    CollectionTable collectionTable = documentSnapshot.toObject(CollectionTable.class);
                                    collectionTable.setCollectionId(documentSnapshot.getId());

                                    if((DateUtil.dateString(collectionTable.getCollectionDueDate()).equals(DateUtil.dateString(new Date())) &&
                                            !collectionTable.getIsDueCollected())) {
                                        Log.d(TAG, "onComplete: "+collectionTable.toString());
                                        getLoansData(collectionTable.getLoanId(), collectionTable.getCollectionId());
                                        saveNewCollectionToLocalStorage(collectionTable);
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
            saveGroupToLocalStorage(group);
        }else {
            groupBorrowerQueries.retrieveSingleBorrowerGroup(groupId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        count++;
                        GroupBorrowerTable groupBorrowerTable = task.getResult().toObject(GroupBorrowerTable.class);
                        groupBorrowerTable.setGroupId(groupId);

                        saveGroupToLocalStorage(groupBorrowerTable);
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

        if(fragment != null) {
            fragment.dueCollectionList.clear();
            fragment.collectionTableList.clear();
        }

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
                                    drawCollectionMarker(collectionTables);
                                }
                            }
                        } else getSingleBorrower(loan.getBorrowerId(), dueCollectionDetails, collectionTables);

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
                                    drawCollectionMarker(collectionTables);
                                }
                            }
                        } getSingleGroup(loan.getGroupId(), dueCollectionDetails, collectionTables);
                    }

                } else getSingleLoan(collectionTable.getLoanId(), dueCollectionDetails, collectionTables);
            }
        }else{
            if(fragment != null)
            fragment.emptyCollection.setVisibility(View.VISIBLE);
        }

    }

    private void getSingleLoan(String loanId, final DueCollectionDetails dueCollectionDetails, final List<CollectionTable> collectionTables) {
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
                                                drawCollectionMarker(collectionTables);
                                            }
                                        }
                                    } else getSingleBorrower(loansTable.getBorrowerId(), dueCollectionDetails, collectionTables);

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
                                                drawCollectionMarker(collectionTables);
                                            }
                                        }
                                    } getSingleGroup(loansTable.getGroupId(), dueCollectionDetails, collectionTables);
                                }
                            }
                        } else {
                            Log.d(TAG, "onComplete: Loan retrieved Failed");
                        }
                    }
                });
    }

    private void getSingleGroup(final String groupId, final DueCollectionDetails dueCollectionDetails, final List<CollectionTable> collectionTables) {
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
                            drawCollectionMarker(collectionTables);
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

    private void getSingleBorrower(String borrowerId, final DueCollectionDetails dueCollectionDetails, final List<CollectionTable> collectionTables) {
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
                                        drawCollectionMarker(collectionTables);
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

                            if(loan != null) {
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
                                    if (borrowersTable.getBorrowerImageByteArray() == null){
                                        RequestOptions placeholderOption = new RequestOptions();
                                        placeholderOption.placeholder(R.drawable.new_borrower);
                                        BitmapUtil.getImageAndThumbnailWithRequestOptionsGlide(
                                                fragment.getContext(),
                                                borrowersTable.getProfileImageUri(),
                                                borrowersTable.getProfileImageThumbUri(),
                                                placeholderOption)
                                                .into(circleImageView);
                                    } else circleImageView.setImageBitmap(BitmapUtil.getBitMapFromBytes(borrowersTable.getBorrowerImageByteArray()));
                                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapUtil.convertViewsToBitmap(view)));
                                    Marker mark = mapFragment.mGoogleMap.addMarker(markerOptions);
                                    mark.setTag(table);

                                    customInfoWindowInit(mapFragment.mGoogleMap);

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
                                    mark.setTag(table);

                                    customInfoWindowInit(mapFragment.mGoogleMap);

                                    //adding marker to map
                                    markers.add(mark);
                                }
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

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    private void customInfoWindowInit(GoogleMap map) {
        mapFragment.mapWrapperLayout.init(map, getPixelsFromDp(mapFragment.getContext(), 39 + 20));

        // We want to reuse the info window for all the markers,
        // so let's create only one class member instance
        final View infoWindow = fragmentActivity.getLayoutInflater().inflate(R.layout.custom_info_layout, null);
        final TextView infoTitle = infoWindow.findViewById(R.id.title);
        final TextView colTitle = infoWindow.findViewById(R.id.title1);
        final ImageButton navBtn = infoWindow.findViewById(R.id.nav);
        final ImageButton colBtn = infoWindow.findViewById(R.id.col);

        // Setting custom OnTouchListener which deals with the pressed state
        // so it shows up
        final OnInfoWindowElemTouchListener infoButtonListener = new OnInfoWindowElemTouchListener(navBtn,
                mapFragment.getResources().getDrawable(R.color.whiteEnd),
                mapFragment.getResources().getDrawable(R.color.darkGrey))
        {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr="+marker.getPosition().latitude+","+marker.getPosition().longitude+""));
                mapFragment.getActivity().startActivity(intent);
            }
        };
        navBtn.setOnTouchListener(infoButtonListener);

        //custom info window collection button click listener
        final OnInfoWindowElemTouchListener infoButtonListener2 = new OnInfoWindowElemTouchListener(colBtn, mapFragment.getResources().getDrawable(R.color.whiteEnd), mapFragment.getResources().getDrawable(R.color.darkGrey)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button
                CollectionTable collectionTable = (CollectionTable) marker.getTag();
                newPayment(collectionTable);
            }
        };
        colBtn.setOnTouchListener(infoButtonListener2);


        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info
                if(marker.getTitle().equals("Your Location")){
                    navBtn.setVisibility(GONE);
                    colBtn.setVisibility(GONE);
                    colTitle.setVisibility(GONE);
                }else {
                    navBtn.setVisibility(VISIBLE);
                    colBtn.setVisibility(VISIBLE);
                    colTitle.setVisibility(VISIBLE);
                }

                infoTitle.setText(marker.getTitle());
                CollectionTable collectionTable = (CollectionTable) marker.getTag();
                colTitle.setText("Collection Number: " + collectionTable.getCollectionNumber());
                infoButtonListener.setMarker(marker);
                infoButtonListener2.setMarker(marker);
                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapFragment.mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });
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

    /***********************retrieve all collection and comparing to local***********/
    public void retrieveDueCollectionToLocalStorageAndCompareToCloud(){
        final List<CollectionTable> localCollection = retrieveCollectionToLocalStorage();

        count = 0;

        collectionQueries.retrieveAllCollection()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: Success in retrieving data from server");
                            if(!task.getResult().isEmpty()){

                                List<CollectionTable> newCol = new ArrayList<>();
                                List<CollectionTable> oldCol = new ArrayList<>();

                                //to separate old collections from new ones
                                for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){

                                    CollectionTable collectionTable = documentSnapshot.toObject(CollectionTable.class);
                                    collectionTable.setCollectionId(documentSnapshot.getId());

                                    boolean newData = true;
                                    for(CollectionTable colTab : localCollection){

                                        if(colTab.getCollectionId().equals(documentSnapshot.getId())){
                                            newData = false;
                                            break;
                                        }
                                    }

                                    if(newData) newCol.add(collectionTable);
                                    else oldCol.add(collectionTable);
                                }

                                collectionSize = newCol.size();

                                //to get only due collections size
                                for (CollectionTable collectionTable : newCol) {
                                    if ((!DateUtil.dateString(collectionTable.getCollectionDueDate()).equals(DateUtil.dateString(new Date())))) {
                                        if(!collectionTable.getIsDueCollected()){
                                            collectionSize--;
                                        }
                                    }
                                }

                                if(collectionSize == 0){
                                    removeRefresher();
                                    Toast.makeText(fragmentActivity, "No new due collection", Toast.LENGTH_SHORT).show();

                                }


                                //dealing with new collection
                                for (CollectionTable collectionTable : newCol) {
                                    if((DateUtil.dateString(collectionTable.getCollectionDueDate()).equals(DateUtil.dateString(new Date())) &&
                                            !collectionTable.getIsDueCollected())) {
                                        getLoansData(collectionTable.getLoanId(), collectionTable.getCollectionId());
                                        saveNewCollectionToLocalStorage(collectionTable);
                                    }
                                }

                                //checking old collections for update
                                for (CollectionTable collectionTable : oldCol) {
                                    updateTable(collectionTable);
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

    private void updateTable(CollectionTable collectionTable) {

        CollectionTable currentlySaved = collectionTableQueries.loadSingleCollection(collectionTable.getCollectionId());
        collectionTable.setId(currentlySaved.getId());

        if(collectionTable.getLastUpdatedAt().getTime() != currentlySaved.getLastUpdatedAt().getTime()){

            collectionTableQueries.updateCollection(collectionTable);
            Log.d("DueCollection", "DueCollection Detailed updated");

        }
    }
}
