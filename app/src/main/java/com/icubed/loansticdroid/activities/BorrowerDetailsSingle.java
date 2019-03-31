package com.icubed.loansticdroid.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.BorrowerBusinessVerificationRecyclerAdapter;
import com.icubed.loansticdroid.adapters.BorrowersGroupRecyclerAdapter;
import com.icubed.loansticdroid.adapters.DocumentRecyclerAdapter;
import com.icubed.loansticdroid.cloudqueries.ActivityCycleQueries;
import com.icubed.loansticdroid.cloudqueries.BorrowerFilesQueries;
import com.icubed.loansticdroid.cloudqueries.BorrowerGroupsQueries;
import com.icubed.loansticdroid.cloudqueries.BorrowerPhotoValidationQueries;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.cloudqueries.GroupBorrowerQueries;
import com.icubed.loansticdroid.localdatabase.ActivityCycleTable;
import com.icubed.loansticdroid.localdatabase.ActivityCycleTableQueries;
import com.icubed.loansticdroid.localdatabase.BorrowerFilesTable;
import com.icubed.loansticdroid.localdatabase.BorrowerFilesTableQueries;
import com.icubed.loansticdroid.localdatabase.BorrowerGroupsTable;
import com.icubed.loansticdroid.localdatabase.BorrowerGroupsTableQueries;
import com.icubed.loansticdroid.localdatabase.BorrowerPhotoValidationTable;
import com.icubed.loansticdroid.localdatabase.BorrowerPhotoValidationTableQueries;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTableQueries;
import com.icubed.loansticdroid.util.BitmapUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BorrowerDetailsSingle extends AppCompatActivity {
    private static final String TAG = ".BorrowerDetailsSingle";
    private Toolbar toolbar;
    private BorrowersTable borrower;
    private CircleImageView profileImageView;
    private Button savingsButton, loanButton;
    private ImageView statusIndicator, addGroupBtn;
    private TextView nameTextView, numberTextView, emailTextView, numberOfBizVerifTextView
            , businessNameTextView, businessLocationTextView, businessDescriptionTextView
            , genderTextView, dobTextView, homeAddressTextView, countryTextView, groupCountTextView
            , stateTextView, cityTextView, numberOfDocTextView, borrowerLocationTextView, statusText;
    private Switch statusSwitch;
    private ProgressBar progressBar;
    private NestedScrollView content;
    private String activityCycleId;
    private boolean isDataFromSearch = false;
    private String borrowerId;
    private AlertDialog.Builder alert;
    private byte[] borrowerImageByteArray;

    private RecyclerView docRecyclerView, borrowerGroupRecyclerView;
    private RecyclerView businessVerificationRecyclerView;
    private DocumentRecyclerAdapter documentRecyclerAdapter;
    private List<BorrowerFilesTable> borrowerFilesTables;
    private ActivityCycleTableQueries activityCycleTableQueries;
    private List<BorrowerPhotoValidationTable> borrowerPhotoValidationTables;
    private BorrowerBusinessVerificationRecyclerAdapter borrowerBusinessVerificationRecyclerAdapter;
    private BorrowerFilesQueries borrowerFilesQueries;
    private BorrowerPhotoValidationQueries borrowerPhotoValidationQueries;
    private BorrowerPhotoValidationTableQueries borrowerPhotoValidationTableQueries;
    private BorrowerFilesTableQueries borrowerFilesTableQueries;
    private BorrowersQueries borrowersQueries;
    private ActivityCycleQueries activityCycleQueries;
    private BorrowersGroupRecyclerAdapter borrowersGroupRecyclerAdapter;
    private List<GroupBorrowerTable> groupBorrowerTableList;
    private BorrowerGroupsQueries borrowerGroupsQueries;
    private BorrowerGroupsTableQueries borrowerGroupsTableQueries;
    private GroupBorrowerQueries groupBorrowerQueries;
    private GroupBorrowerTableQueries groupBorrowerTableQueries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrower_details_single);

        toolbar = findViewById(R.id.borrower_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Borrower profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        borrower = getIntent().getParcelableExtra("borrower");
        if(borrower != null){
            borrowerImageByteArray = getIntent().getByteArrayExtra("borrowerImageByteArray");
            borrower.setBorrowerImageByteArray(borrowerImageByteArray);
        }
        borrowerId = getIntent().getStringExtra("borrowerId");

        borrowerFilesQueries = new BorrowerFilesQueries(this);
        activityCycleTableQueries = new ActivityCycleTableQueries(getApplication());
        borrowerPhotoValidationQueries = new BorrowerPhotoValidationQueries(this);
        borrowerFilesTableQueries = new BorrowerFilesTableQueries(getApplication());
        borrowerPhotoValidationTableQueries = new BorrowerPhotoValidationTableQueries(getApplication());
        activityCycleQueries = new ActivityCycleQueries();
        borrowersQueries = new BorrowersQueries(this);
        borrowerGroupsQueries = new BorrowerGroupsQueries();
        borrowerGroupsTableQueries = new BorrowerGroupsTableQueries(getApplication());
        groupBorrowerQueries = new GroupBorrowerQueries();
        groupBorrowerTableQueries = new GroupBorrowerTableQueries(getApplication());

        statusSwitch = findViewById(R.id.active_switch);
        statusIndicator = findViewById(R.id.indicator);
        statusText = findViewById(R.id.status_state);
        profileImageView = findViewById(R.id.profileImageView);
        nameTextView = findViewById(R.id.name);
        numberTextView = findViewById(R.id.mobile_number);
        emailTextView = findViewById(R.id.email_ad);
        businessNameTextView = findViewById(R.id.business_name);
        businessLocationTextView = findViewById(R.id.business_location);
        businessDescriptionTextView = findViewById(R.id.business_description);
        genderTextView = findViewById(R.id.gender);
        dobTextView = findViewById(R.id.dob);
        homeAddressTextView = findViewById(R.id.home_adress);
        countryTextView = findViewById(R.id.country);
        stateTextView = findViewById(R.id.state);
        progressBar = findViewById(R.id.borrower_progressbar);
        cityTextView = findViewById(R.id.city);
        numberOfBizVerifTextView = findViewById(R.id.number_of_biz_verif);
        businessVerificationRecyclerView = findViewById(R.id.business_verif_recycler_view);
        numberOfDocTextView = findViewById(R.id.number_of_documents);
        docRecyclerView = findViewById(R.id.documentRecyclerView);
        borrowerLocationTextView = findViewById(R.id.borrower_location);
        content = findViewById(R.id.borrower_content);
        addGroupBtn = findViewById(R.id.addGroup);
        borrowerGroupRecyclerView = findViewById(R.id.groupsRecyclerView);
        groupCountTextView = findViewById(R.id.number_of_group);
        savingsButton = findViewById(R.id.savings_button);
        loanButton = findViewById(R.id.loan_button);

        alert = new AlertDialog.Builder(this);

        loanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AllBorrowerLoan.class);
                intent.putExtra("borrower", borrower);
                startActivity(intent);
            }
        });

        savingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SavingsUnderABorrower.class);
                intent.putExtra("borrower", borrower);
                startActivity(intent);
            }
        });

        addGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddBorrowerToExistingGroupActivity.class);
                intent.putExtra("borrower", borrower);
                startActivity(intent);
            }
        });

        statusSwitch.setChecked(true);
        statusSwitchChangeListener();

        borrowerLocationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                intent.putExtra("borrower", borrower);
                startActivity(intent);
            }
        });

        groupBorrowerTableList = new ArrayList<>();
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        borrowerGroupRecyclerView.setLayoutManager(layoutManager3);
        borrowersGroupRecyclerAdapter = new BorrowersGroupRecyclerAdapter(groupBorrowerTableList);
        borrowerGroupRecyclerView.setAdapter(borrowersGroupRecyclerAdapter);

        borrowerFilesTables = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        docRecyclerView.setLayoutManager(layoutManager);
        documentRecyclerAdapter = new DocumentRecyclerAdapter(borrowerFilesTables);
        docRecyclerView.setAdapter(documentRecyclerAdapter);

        borrowerPhotoValidationTables = new ArrayList<>();
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        businessVerificationRecyclerView.setLayoutManager(layoutManager2);
        borrowerBusinessVerificationRecyclerAdapter = new BorrowerBusinessVerificationRecyclerAdapter(borrowerPhotoValidationTables);
        businessVerificationRecyclerView.setAdapter(borrowerBusinessVerificationRecyclerAdapter);

        if(borrower != null){

            ActivityCycleTable activityCycleTable = activityCycleTableQueries.loadLastCreatedCycle(borrower.getBorrowersId());

            if(activityCycleTable == null){
                getActivityCycleData(borrower.getBorrowersId());
                return;
            }

            activityCycleId = activityCycleTable.getActivityCycleId();

            if (!activityCycleTable.getIsActive()) {
                inActiveIndicators();
            }

            setBorrowerDetailsOnUi();
            getFiles();
            getBusinessVerificationPhotos();
            getBorrowerGroups();
        }else{
            getActivityCycleData(borrowerId);
            isDataFromSearch = true;
        }

    }

    private void getBorrowerGroups() {
        List<BorrowerGroupsTable> borrowerGroupsTableList = borrowerGroupsTableQueries.loadBorrowerGroups(borrower.getBorrowersId());

        if(borrowerGroupsTableList.isEmpty()){
            getBorrowersGroupsOnline();
        }else{
            groupCountTextView.setText(String.valueOf(borrowerGroupsTableList.size()));

            for (BorrowerGroupsTable borrowerGroupsTable : borrowerGroupsTableList) {
                GroupBorrowerTable groupBorrowerTable = groupBorrowerTableQueries.loadSingleBorrowerGroup(borrowerGroupsTable.getGroupId());

                if(groupBorrowerTable == null){
                    getGroupDetails(borrowerGroupsTable.getGroupId());
                }else {
                    groupBorrowerTableList.add(groupBorrowerTable);
                    borrowersGroupRecyclerAdapter.notifyDataSetChanged();
                }

                getGroupAndCompareToCloud(borrowerGroupsTableList);
            }
        }
    }

    private void getGroupAndCompareToCloud(final List<BorrowerGroupsTable> borrowerGroupsTableList) {
        borrowerGroupsQueries.retrieveGroupsOfBorrower(borrower.getBorrowersId())
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){
                                for(DocumentSnapshot doc : task.getResult()){
                                    BorrowerGroupsTable borrowerGroupsTable = doc.toObject(BorrowerGroupsTable.class);
                                    borrowerGroupsTable.setDocumentId(doc.getId());
                                    borrowerGroupsTable.setBorrowerId(borrower.getBorrowersId());

                                    for (BorrowerGroupsTable groupsTable : borrowerGroupsTableList) {
                                        if(groupsTable.getGroupId().equals(borrowerGroupsTable.getGroupId())){
                                            return;
                                        }
                                    }

                                    saveBorrowerGroupsToStorage(borrowerGroupsTable);
                                    getGroup(borrowerGroupsTable);
                                }

                                groupCountTextView.setText(String.valueOf(task.getResult().size()));
                            }
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void getBorrowersGroupsOnline() {
        borrowerGroupsQueries.retrieveGroupsOfBorrower(borrower.getBorrowersId())
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){
                                for(DocumentSnapshot doc : task.getResult()){
                                    BorrowerGroupsTable borrowerGroupsTable = doc.toObject(BorrowerGroupsTable.class);
                                    borrowerGroupsTable.setDocumentId(doc.getId());
                                    borrowerGroupsTable.setBorrowerId(borrower.getBorrowersId());

                                    saveBorrowerGroupsToStorage(borrowerGroupsTable);
                                    getGroup(borrowerGroupsTable);
                                }

                                groupCountTextView.setText(String.valueOf(task.getResult().size()));
                            }
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void getGroup(BorrowerGroupsTable borrowerGroupsTable) {
        GroupBorrowerTable groupBorrowerTable = groupBorrowerTableQueries.loadSingleBorrowerGroup(borrowerGroupsTable.getGroupId());

        if(groupBorrowerTable == null){
            getGroupDetails(borrowerGroupsTable.getGroupId());
        }else {
            groupBorrowerTableList.add(groupBorrowerTable);
            borrowersGroupRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private void saveBorrowerGroupsToStorage(BorrowerGroupsTable borrowerGroupsTable) {
        borrowerGroupsTableQueries.insertGroupMemberToStorage(borrowerGroupsTable);
    }

    private void getGroupDetails(final String groupId) {
        groupBorrowerQueries.retrieveSingleBorrowerGroup(groupId)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            GroupBorrowerTable groupBorrowerTable = task.getResult().toObject(GroupBorrowerTable.class);
                            groupBorrowerTable.setGroupId(groupId);

                            saveGroupToStorage(groupBorrowerTable);
                            groupBorrowerTableList.add(groupBorrowerTable);
                            borrowersGroupRecyclerAdapter.notifyDataSetChanged();
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void saveGroupToStorage(GroupBorrowerTable groupBorrowerTable) {
        GroupBorrowerTable groupBorrowerTable1 = groupBorrowerTableQueries.loadSingleBorrowerGroup(groupBorrowerTable.getGroupId());
        if(groupBorrowerTable1 == null) groupBorrowerTableQueries.insertGroupToStorage(groupBorrowerTable);
    }

    /**
     * this method return the ochange listener feature for the status switch button
     * @return
     */
    private CompoundButton.OnCheckedChangeListener listener(){
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    alert.setCancelable(true);
                    alert.setTitle("Activate Borrower");
                    alert.setMessage("Upload documents to verify borrower?");
                    alert.setPositiveButton("Activate", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            makeBorrowerActive();
                        }
                    });
                    alert.setNegativeButton("Verify Borrower", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            maintainFalse();
                            Intent intent = new Intent(getApplicationContext(), ReactivateBorrowerActivity.class);
                            intent.putExtra("borrowerId", borrower.getBorrowersId());
                            startActivity(intent);
                        }
                    });
                    alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            maintainFalse();
                        }
                    });
                    final AlertDialog builder = alert.create();
                    builder.show();
                }else{
                    makeBorrowerInActive();
                }
            }
        };
    }

    /**
     * set status indicator to maintain inactive position
     */
    private void maintainFalse(){
        statusSwitch.setOnCheckedChangeListener(null);
        statusSwitch.setChecked(false);
        statusSwitchChangeListener();
    }

    /**
     * status switch wudget on change listener
     */
    private void statusSwitchChangeListener() {
        statusSwitch.setOnCheckedChangeListener(listener());
    }

    /**
     * this method makes borrower active
     * it updates the server and the local storage if the data already exist
     */
    private void makeBorrowerActive(){
        statusIndicator.setImageResource(R.drawable.indicator_active);
        statusText.setText("Active");

        activityCycleQueries.activateBorrower(activityCycleId)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            ActivityCycleTable activityCycleTable = activityCycleTableQueries.loadSingleActivityCycle(activityCycleId);

                            if(activityCycleTable != null){
                                activityCycleTable.setIsActive(true);
                                activityCycleTable.setEndCycleTime(null);

                                activityCycleTableQueries.updateStorageAfterActivityCycleEnds(activityCycleTable);
                            }

                            Toast.makeText(BorrowerDetailsSingle.this, "Borrower Activated", Toast.LENGTH_SHORT).show();
                        }else{
                            inActiveIndicators();
                            Toast.makeText(BorrowerDetailsSingle.this, "Borrower Activation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * this method makes a borrower in active
     * it updates the server and the local storage if the data already exist
     */
    private void makeBorrowerInActive(){
        inActiveIndicators();
        final Date deactivationDate = new Date();

        activityCycleQueries.deactivateBorrower(activityCycleId, deactivationDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            ActivityCycleTable activityCycleTable = activityCycleTableQueries.loadSingleActivityCycle(activityCycleId);

                            if(activityCycleTable != null){
                                activityCycleTable.setIsActive(false);
                                activityCycleTable.setEndCycleTime(deactivationDate);

                                activityCycleTableQueries.updateStorageAfterActivityCycleEnds(activityCycleTable);
                            }

                            Toast.makeText(BorrowerDetailsSingle.this, "Borrower made inactive", Toast.LENGTH_SHORT).show();
                        }else{
                            activeIndicators();
                            Toast.makeText(BorrowerDetailsSingle.this, "Failed to make borrower inactive", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * this methods changes all the necessary UI features for when a borrower is in active
     * like the status colour and text
     */
    private void inActiveIndicators(){
        statusSwitch.setOnCheckedChangeListener(null);
        statusSwitch.setChecked(false);
        statusSwitchChangeListener();
        statusText.setText("Inactive");
        statusIndicator.setImageResource(R.drawable.indicator_inactive);
    }

    /**
     * this methods changes all the necessary UI features for when a borrower is active
     * like the status colour and text
     */
    private void activeIndicators(){
        statusSwitch.setOnCheckedChangeListener(null);
        statusSwitch.setChecked(true);
        statusSwitchChangeListener();
        statusText.setText("Active");
        statusIndicator.setImageResource(R.drawable.indicator_active);
    }

    /**
     * gets the last active cycle ID of a borrower
     * this is needed to get the borrower documents and business verification photo
     * it also enables us to get the state of the borrower.
     * @param borrowerId
     */
    private void getActivityCycleData(final String borrowerId) {
        activityCycleQueries.retrieveLastCreatedCycleForBorrower(borrowerId)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot doc : task.getResult()){
                                ActivityCycleTable activityCycleTable = doc.toObject(ActivityCycleTable.class);
                                activityCycleTable.setActivityCycleId(doc.getId());

                                activityCycleId = doc.getId();

                                if (!activityCycleTable.getIsActive()) {
                                    inActiveIndicators();
                                }

                                saveActivityCycleData(activityCycleTable);

                                if(!isDataFromSearch){
                                    setBorrowerDetailsOnUi();
                                    getFiles();
                                    getBusinessVerificationPhotos();
                                    getBorrowerGroups();
                                }

                                retrieveBorrowerDetailsFromCloud();
                                getNewFiles(borrowerId, activityCycleTable.getActivityCycleId());
                                getNewBusinessVerificationPhotos(borrowerId, activityCycleTable.getActivityCycleId());
                            }
                        }else{
                            hideProgressBar();
                            Toast.makeText(BorrowerDetailsSingle.this, "Unable to retrieve borrower details", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void saveActivityCycleData(ActivityCycleTable activityCycleTable) {
        ActivityCycleTable table = activityCycleTableQueries.loadSingleActivityCycle(activityCycleTable.getActivityCycleId());

        if(table == null){
            activityCycleTableQueries.insertActivityCycleToStorage(activityCycleTable);
        }
    }

    /**
     * this retrieves borrower full details from the cloud
     * this method is usually called when the borrower is selected from a search and not from the borrower list in the borrower pages
     * because loading a borrower profile from the search, only the borrower id is returned
     */
    private void retrieveBorrowerDetailsFromCloud() {
        borrowersQueries.retrieveSingleBorrowers(borrowerId)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            BorrowersTable borrowersTable = task.getResult().toObject(BorrowersTable.class);
                            borrowersTable.setBorrowersId(task.getResult().getId());

                            borrower = borrowersTable;
                            borrowersTable.setId((long) 23);
                            setBorrowerDetailsOnUi();
                            getBorrowerGroups();

                        }else{
                            hideProgressBar();
                            Toast.makeText(BorrowerDetailsSingle.this, "Unable to retrieve borrower details", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * this hides progress bar from UI and show the other UI
     */
    private void hideProgressBar(){
        content.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    /**
     * checks if business verification photo already exist in the local storage and loads to the UI
     * if the photo does not exist, it call getNewBusinessVerificationPhotos(String borrowersId, String activityCycleId) method
     */
    private void getBusinessVerificationPhotos() {

        ActivityCycleTable activityCycleTable = activityCycleTableQueries.loadLastCreatedCycle(borrower.getBorrowersId());

        List<BorrowerPhotoValidationTable> validationTables = borrowerPhotoValidationTableQueries.loadAllPhotes(activityCycleTable.getActivityCycleId());

        if(validationTables.isEmpty()){
            getNewBusinessVerificationPhotos(borrower.getBorrowersId(), activityCycleTable.getActivityCycleId());
        }else{

            numberOfBizVerifTextView.setText(String.valueOf(validationTables.size()));

            for (BorrowerPhotoValidationTable validationTable : validationTables) {
                borrowerPhotoValidationTables.add(validationTable);
                borrowerBusinessVerificationRecyclerAdapter.notifyDataSetChanged();
            }

        }


    }

    /**
     * this gets the business verification photo of a borrower from the cloud
     * @param borrowersId
     * @param activityCycleId
     */
    private void getNewBusinessVerificationPhotos(String borrowersId, String activityCycleId){
        borrowerPhotoValidationQueries.retrieveAllValidationPhotosForBorrower(borrowersId, activityCycleId)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){

                                numberOfBizVerifTextView.setText(String.valueOf(task.getResult().size()));

                                for(DocumentSnapshot doc : task.getResult()){

                                    BorrowerPhotoValidationTable borrowerPhotoValidationTable = doc.toObject(BorrowerPhotoValidationTable.class);
                                    borrowerPhotoValidationTable.setBorrowerPhotoValidationId(doc.getId());

                                    borrowerPhotoValidationTables.add(borrowerPhotoValidationTable);
                                    borrowerBusinessVerificationRecyclerAdapter.notifyDataSetChanged();

                                    if(!isDataFromSearch) {
                                        savePhotoVerifToStorage(borrowerPhotoValidationTable);
                                        savePhotoVerifByteToStorage(borrowerPhotoValidationTable);
                                    }
                                }

                            }else{
                                Toast.makeText(getApplicationContext(), "No business verification photo uploaded", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "Failed to retrieve business verification photos", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void savePhotoVerifByteToStorage(final BorrowerPhotoValidationTable borrowerPhotoValidationTable) {
        final BorrowerPhotoValidationTable table = borrowerPhotoValidationTableQueries.loadSinglePhotes(borrowerPhotoValidationTable.getBorrowerPhotoValidationId());

        BitmapUtil.getImageWithGlide(this, table.getPhotoUri())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        saveVerifPhoto(resource, table);
                    }
                });
    }

    private void saveVerifPhoto(Bitmap resource, BorrowerPhotoValidationTable borrowerPhotoValidationTable) {
        byte[] bytes = BitmapUtil.getBytesFromBitmapInJPG(resource, 100);
        borrowerPhotoValidationTable.setImageByteArray(bytes);
        borrowerPhotoValidationTableQueries.updatePhoto(borrowerPhotoValidationTable);

        Log.d(TAG, "saveImage: file image byte[] saved");
    }

    /**
     * this saves a new business verification photo to local storage
     * @param borrowerPhotoValidationTable
     */
    private void savePhotoVerifToStorage(BorrowerPhotoValidationTable borrowerPhotoValidationTable) {
        BorrowerPhotoValidationTable borrowerPhotoValidationTable1 = borrowerPhotoValidationTableQueries.loadSinglePhotes(borrowerPhotoValidationTable.getBorrowerPhotoValidationId());
        if(borrowerPhotoValidationTable1 == null) borrowerPhotoValidationTableQueries.insertPhototsToStorage(borrowerPhotoValidationTable);
    }

    /**
     * checks if borrower document already exist in the local storage and loads to the UI
     * if the document does not exist, it call getNewFiles(String borrowersId, String activityCycleId) method
     */
    private void getFiles() {

        ActivityCycleTable activityCycleTable = activityCycleTableQueries.loadLastCreatedCycle(borrower.getBorrowersId());

        List<BorrowerFilesTable> bow = borrowerFilesTableQueries.loadAllBorrowersFile(activityCycleTable.getActivityCycleId());

        if(bow.isEmpty()){
            getNewFiles(borrower.getBorrowersId(), activityCycleTable.getActivityCycleId());
        }else{

            numberOfDocTextView.setText(String.valueOf(bow.size()));

            for (BorrowerFilesTable borrowerFilesTable : bow) {
                borrowerFilesTables.add(borrowerFilesTable);
                documentRecyclerAdapter.notifyDataSetChanged();
            }

        }
    }

    /**
     * this gets the borrower document from the server
     * @param borrowersId
     * @param activityCycleId
     */
    private void getNewFiles(String borrowersId, String activityCycleId) {
        borrowerFilesQueries.retrieveFilesFromCloud(borrowersId, activityCycleId)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            if(!task.getResult().isEmpty()){

                                Log.d(TAG, "onComplete: "+task.getResult().size());

                                numberOfDocTextView.setText(String.valueOf(task.getResult().size()));

                                for(DocumentSnapshot doc : task.getResult()){
                                    BorrowerFilesTable borrowerFilesTable = doc.toObject(BorrowerFilesTable.class);
                                    borrowerFilesTable.setFilesId(doc.getId());

                                    borrowerFilesTables.add(borrowerFilesTable);
                                    documentRecyclerAdapter.notifyDataSetChanged();

                                    if(!isDataFromSearch) {
                                        saveFileToStorage(borrowerFilesTable);
                                        saveFileImageToStorage(borrowerFilesTable);
                                    }
                                }
                            }else{
                                Toast.makeText(BorrowerDetailsSingle.this, "No file saved", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(BorrowerDetailsSingle.this, "Unable to retrieve borrower files", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    /**
     * this method get the bitmap of the file uri and them call up the saveImage method
     * @param borrowerFilesTable
     */
    private void saveFileImageToStorage(final BorrowerFilesTable borrowerFilesTable) {
        final BorrowerFilesTable table = borrowerFilesTableQueries.loadSingleBorrowerFile(borrowerFilesTable.getFilesId());

        BitmapUtil.getImageWithGlide(this, borrowerFilesTable.getFileImageUri())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        saveImage(resource, table);
                    }
                });
    }

    /**
     * this method converts the file image into byte[]
     * the byte[] of the bitmap is then saved in the local storage
     * @param resource
     * @param borrowerFilesTable
     */
    private void saveImage(Bitmap resource, BorrowerFilesTable borrowerFilesTable) {
        byte[] bytes = BitmapUtil.getBytesFromBitmapInJPG(resource, 100);
        borrowerFilesTable.setImageByteArray(bytes);
        borrowerFilesTableQueries.updateBorrowerFileDetails(borrowerFilesTable);

        Log.d(TAG, "saveImage: file image byte[] saved");
    }

    /**
     * this saves new borrower document to local storage
     * @param borrowerFilesTable
     */
    private void saveFileToStorage(BorrowerFilesTable borrowerFilesTable) {
        BorrowerFilesTable borrowerFilesTable1 = borrowerFilesTableQueries.loadSingleBorrowerFile(borrowerFilesTable.getFilesId());
        if(borrowerFilesTable1 == null) borrowerFilesTableQueries.insertBorrowersFileToStorage(borrowerFilesTable);
        Log.d(TAG, "saveFileToStorage: files saved");
    }

    /**
     * sets the various borrower details to the UI of the activity
     * this enables the user to view the details
     */
    private void setBorrowerDetailsOnUi() {

        getSupportActionBar().setSubtitle(borrower.getLastName()+" "+borrower.getMiddleName()+" "+borrower.getFirstName());

        if(borrower.getBorrowerImageByteArray() == null) {
            BitmapUtil.getImageAndThumbnailWithGlide(this, borrower.getProfileImageUri(), borrower.getProfileImageThumbUri()).into(profileImageView);
        }else{
            profileImageView.setImageBitmap(BitmapUtil.getBitMapFromBytes(borrower.getBorrowerImageByteArray()));
        }

        nameTextView.setText(borrower.getLastName()+" "+borrower.getMiddleName()+" "+borrower.getFirstName());
        numberTextView.setText(String.valueOf(borrower.getPhoneNumber()));
        emailTextView.setText(borrower.getEmail());
        businessNameTextView.setText(borrower.getBusinessName());
        businessLocationTextView.setText(borrower.getWorkAddress());
        businessDescriptionTextView.setText(borrower.getBusinessDescription());
        genderTextView.setText(borrower.getSex());
        dobTextView.setText(borrower.getDateOfBirth());
        homeAddressTextView.setText(borrower.getHomeAddress());
        countryTextView.setText(borrower.getNationality());
        stateTextView.setText(borrower.getState());
        cityTextView.setText(borrower.getCity());

        getSupportActionBar().setSubtitle(borrower.getLastName()+" "+borrower.getMiddleName()+" "+borrower.getFirstName());


        hideProgressBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
