package com.icubed.loansticdroid.activities;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.GroupBusinessVerificationRecyclerAdapter;
import com.icubed.loansticdroid.adapters.GroupMembersRecyclerAdapter;
import com.icubed.loansticdroid.cloudqueries.BorrowerGroupsQueries;
import com.icubed.loansticdroid.cloudqueries.BorrowerPhotoValidationQueries;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.cloudqueries.GroupBorrowerQueries;
import com.icubed.loansticdroid.localdatabase.ActivityCycleTable;
import com.icubed.loansticdroid.localdatabase.BorrowerPhotoValidationTable;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.BorrowersTableQueries;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.localdatabase.GroupMembersTable;
import com.icubed.loansticdroid.localdatabase.GroupMembersTableQueries;
import com.icubed.loansticdroid.localdatabase.GroupPhotoValidationTable;
import com.icubed.loansticdroid.localdatabase.GroupPhotoValidationTableQueries;
import com.icubed.loansticdroid.models.Borrowers;
import com.icubed.loansticdroid.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

public class BorrowerDetailsGroup extends AppCompatActivity {

    private static final String TAG = ".BorrowerDetailsGroup";
    private TextView groupNameTextView, groupAddressTextView
            ,numberOfGroupMembersTextView, numberOfBusiVerifTextView;
    private RecyclerView businessVerificationRecyclerView, groupMembersRecyclerView;
    private String groupId;
    private GroupMembersRecyclerAdapter groupMembersRecyclerAdapter;
    private GroupBusinessVerificationRecyclerAdapter groupBusinessVerificationRecyclerAdapter;
    private List<GroupPhotoValidationTable> groupPhotoValidationTables;
    private List<BorrowersTable> borrowersTableList;
    private GroupBorrowerTable group;
    private BorrowersQueries borrowersQueries;
    private BorrowerGroupsQueries borrowerGroupsQueries;
    private BorrowersTableQueries borrowersTableQueries;
    private BorrowerPhotoValidationQueries borrowerPhotoValidationQueries;
    private GroupMembersTableQueries groupMembersTableQueries;
    GroupPhotoValidationTableQueries groupPhotoValidationTableQueries;
    private GroupBorrowerQueries groupBorrowerQueries;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private NestedScrollView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrower_details_group);

        toolbar = findViewById(R.id.group_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Group profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        borrowersQueries = new BorrowersQueries(this);
        borrowerGroupsQueries = new BorrowerGroupsQueries();
        borrowersTableQueries = new BorrowersTableQueries(getApplication());
        borrowerPhotoValidationQueries = new BorrowerPhotoValidationQueries(this);
        groupPhotoValidationTableQueries = new GroupPhotoValidationTableQueries(getApplication());
        groupMembersTableQueries = new GroupMembersTableQueries(getApplication());
        groupBorrowerQueries = new GroupBorrowerQueries();

        group = getIntent().getParcelableExtra("group");
        groupId = getIntent().getStringExtra("groupId");

        groupNameTextView = findViewById(R.id.name);
        groupAddressTextView = findViewById(R.id.group_address);
        numberOfGroupMembersTextView = findViewById(R.id.number_of_members);
        numberOfBusiVerifTextView = findViewById(R.id.number_of_biz_verif);
        businessVerificationRecyclerView = findViewById(R.id.documentRecyclerView);
        groupMembersRecyclerView = findViewById(R.id.membersRecyclerView);
        content = findViewById(R.id.content_frame);
        progressBar = findViewById(R.id.group_progressbar);


        groupPhotoValidationTables = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        businessVerificationRecyclerView.setLayoutManager(layoutManager);
        groupBusinessVerificationRecyclerAdapter = new GroupBusinessVerificationRecyclerAdapter(groupPhotoValidationTables);
        businessVerificationRecyclerView.setAdapter(groupBusinessVerificationRecyclerAdapter);

        borrowersTableList = new ArrayList<>();
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        groupMembersRecyclerView.setLayoutManager(layoutManager2);
        groupMembersRecyclerAdapter = new GroupMembersRecyclerAdapter(borrowersTableList);
        groupMembersRecyclerView.setAdapter(groupMembersRecyclerAdapter);

        if(group != null) {
            getMembers();
            getBusinessVerificationPhotos();
            setGroupDetailsToUI();
        }else{
            getGroupOnline();
        }
    }

    private void hideProgressBar(){
        content.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void getGroupOnline() {
        groupBorrowerQueries.retrieveSingleBorrowerGroup(groupId)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            GroupBorrowerTable groupBorrowerTable = task.getResult().toObject(GroupBorrowerTable.class);
                            groupBorrowerTable.setGroupId(task.getResult().getId());

                            group = groupBorrowerTable;
                            setGroupDetailsToUI();
                            getNewBusinessVerificationPhotos();
                            getGroupMembersFromCloud();

                        }else{
                            hideProgressBar();
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                            Toast.makeText(BorrowerDetailsGroup.this, "Failed to retrieve group details", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getBusinessVerificationPhotos() {

        List<GroupPhotoValidationTable> loadPhotosForGroup = groupPhotoValidationTableQueries.loadPhotosForGroup(group.getGroupId());

        if(loadPhotosForGroup.isEmpty()){
            getNewBusinessVerificationPhotos();
        }else{
            numberOfBusiVerifTextView.setText(String.valueOf(loadPhotosForGroup.size()));
            for (GroupPhotoValidationTable validationTable : loadPhotosForGroup) {
                groupPhotoValidationTables.add(validationTable);
                groupBusinessVerificationRecyclerAdapter.notifyDataSetChanged();
            }

        }


    }

    private void getNewBusinessVerificationPhotos() {
        borrowerPhotoValidationQueries.retrieveAllValidationPhotosForGroup(group.getGroupId())
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){

                                numberOfBusiVerifTextView.setText(String.valueOf(task.getResult().size()));

                                for(DocumentSnapshot doc : task.getResult()){

                                    GroupPhotoValidationTable groupPhotoValidationTable = doc.toObject(GroupPhotoValidationTable.class);
                                    groupPhotoValidationTable.setGroupPhotoValidationId(doc.getId());

                                    groupPhotoValidationTables.add(groupPhotoValidationTable);
                                    groupBusinessVerificationRecyclerAdapter.notifyDataSetChanged();

                                    if(groupId == null) {
                                        saveGroupVerificationPhotoToStorage(groupPhotoValidationTable);
                                        saveGroupVerifPhotoByte(groupPhotoValidationTable);
                                    }
                                }

                            }else{
                                Toast.makeText(BorrowerDetailsGroup.this, "No business verification photo uploaded", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(BorrowerDetailsGroup.this, "Failed to retrieve business verification photos", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void saveGroupVerifPhotoByte(final GroupPhotoValidationTable groupPhotoValidationTable) {
        final GroupPhotoValidationTable table = groupPhotoValidationTableQueries.loadSingleGroupPhoto(groupPhotoValidationTable.getGroupPhotoValidationId());

        BitmapUtil.getImageWithGlide(this, groupPhotoValidationTable.getPhotoUri())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        saveVerifPhoto(resource, table);
                    }
                });
    }

    private void saveVerifPhoto(Bitmap resource, GroupPhotoValidationTable groupPhotoValidationTable) {
        byte[] bytes = BitmapUtil.getBytesFromBitmapInJPG(resource, 100);
        groupPhotoValidationTable.setImageByteArray(bytes);
        groupPhotoValidationTableQueries.updateBorrowerDetails(groupPhotoValidationTable);

        Log.d(TAG, "saveImage: group valid photo image byte[] saved");
    }

    private void saveGroupVerificationPhotoToStorage(GroupPhotoValidationTable groupPhotoValidationTable) {
        groupPhotoValidationTableQueries.insertBorrowersToStorage(groupPhotoValidationTable);
    }

    private void setGroupDetailsToUI() {
        groupNameTextView.setText(group.getGroupName());
        groupAddressTextView.setText(group.getMeetingLocation());
        numberOfGroupMembersTextView.setText(String.valueOf(group.getNumberOfGroupMembers()));

        hideProgressBar();
    }

    private void getGroupMembersFromCloud(){
        borrowersQueries.retrieveBorrowersBelongingToGroup()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            if(!task.getResult().isEmpty()){

                                for (final DocumentSnapshot doc : task.getResult()){

                                    borrowerGroupsQueries.retrieveSingleGroupOfBorrower(
                                            doc.getId(), group.getGroupId()
                                    ).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                            if(task.isSuccessful()){

                                                if(!task.getResult().isEmpty()){

                                                    BorrowersTable borrowersTable = doc.toObject(BorrowersTable.class);
                                                    borrowersTable.setBorrowersId(doc.getId());

                                                    if(groupId == null) {
                                                        BorrowersTable bow = saveBorrowerToLocalIfNotExist(borrowersTable);

                                                        //save borrower byte image
                                                        saveBorrowerImage(bow);

                                                        //saving members to members table
                                                        GroupMembersTable groupMembersTable = task.getResult().getDocuments().get(0).toObject(GroupMembersTable.class);
                                                        groupMembersTable.setGroupMemberId(task.getResult().getDocuments().get(0).getId());
                                                        groupMembersTable.setBorrowerId(doc.getId());
                                                        saveGroupMembersToTable(groupMembersTable);
                                                        borrowersTableList.add(bow);
                                                        groupMembersRecyclerAdapter.notifyDataSetChanged();
                                                    }else{
                                                        borrowersTable.setId((long) 464);
                                                        borrowersTableList.add(borrowersTable);
                                                        groupMembersRecyclerAdapter.notifyDataSetChanged();
                                                    }

                                                }else{
                                                    //Toast.makeText(BorrowerDetailsGroup.this, "No borrower has a group yet", Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "onComplete: no borrower has a group yet");
                                                }

                                            }else{
                                                Toast.makeText(BorrowerDetailsGroup.this, "Failed to retrieve group members", Toast.LENGTH_SHORT).show();
                                                Log.d(TAG, "onComplete: "+task.getException().getMessage());
                                            }

                                        }
                                    });

                                }

                            }else{
                                //Toast.makeText(BorrowerDetailsGroup.this, "No borrower has a group yet", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onComplete: no borrower has a group yet");
                            }

                        }else{
                            Toast.makeText(BorrowerDetailsGroup.this, "Failed to retrieve group members", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void saveGroupMembersToTable(GroupMembersTable groupMembersTable) {
        groupMembersTableQueries.insertGroupMemberToStorage(groupMembersTable);
    }

    private void getMembers() {
        List<GroupMembersTable> groupMembersTables = groupMembersTableQueries.loadGroupMembers(group.getGroupId());

        if(groupMembersTables.isEmpty()){
            getGroupMembersFromCloud();
        }else{
            for (GroupMembersTable membersTable : groupMembersTables) {

                BorrowersTable borrowersTable = borrowersTableQueries.loadSingleBorrower(membersTable.getBorrowerId());

                borrowersTableList.add(borrowersTable);
                groupMembersRecyclerAdapter.notifyDataSetChanged();

            }

        }
    }

    private BorrowersTable saveBorrowerToLocalIfNotExist(BorrowersTable borrowersTable){

        //assigning the ID of the borrower from the local storage to the online file
        BorrowersTable currentlySaved = borrowersTableQueries.loadSingleBorrower(borrowersTable.getBorrowersId());
        if(currentlySaved != null){
            borrowersTable.setId(currentlySaved.getId());
            return borrowersTable;
        }

        borrowersTableQueries.insertBorrowersToStorage(borrowersTable);
        return borrowersTableQueries.loadSingleBorrower(borrowersTable.getBorrowersId());
    }

    private void saveBorrowerImage(final BorrowersTable borrowersTable) {
        if(borrowersTable.getBorrowerImageByteArray() == null) {
            BitmapUtil.getImageWithGlide(this, borrowersTable.getProfileImageUri()).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    saveImage(resource, borrowersTable);
                }
            });
        }
    }

    private void saveImage(Bitmap resource, BorrowersTable borrowersTable) {
        byte[] bytes = BitmapUtil.getBytesFromBitmapInJPG(resource, 100);
        borrowersTable.setBorrowerImageByteArray(bytes);
        borrowersTableQueries.updateBorrowerDetails(borrowersTable);

        Log.d(TAG, "saveImage: borrower image byte[] saved");
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
