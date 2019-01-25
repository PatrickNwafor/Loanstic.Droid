package com.icubed.loansticdroid.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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
import com.icubed.loansticdroid.localdatabase.ActivityCycleTable;
import com.icubed.loansticdroid.localdatabase.BorrowerPhotoValidationTable;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.BorrowersTableQueries;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.localdatabase.GroupPhotoValidationTable;
import com.icubed.loansticdroid.localdatabase.GroupPhotoValidationTableQueries;
import com.icubed.loansticdroid.models.Borrowers;

import java.util.ArrayList;
import java.util.List;

public class BorrowerDetailsGroup extends AppCompatActivity {

    private static final String TAG = ".BorrowerDetailsGroup";
    private TextView groupNameTextView, groupAddressTextView
            ,numberOfGroupMembersTextView, numberOfBusiVerifTextView;
    private RecyclerView businessVerificationRecyclerView, groupMembersRecyclerView;
    private GroupMembersRecyclerAdapter groupMembersRecyclerAdapter;
    private GroupBusinessVerificationRecyclerAdapter groupBusinessVerificationRecyclerAdapter;
    private List<GroupPhotoValidationTable> groupPhotoValidationTables;
    private List<BorrowersTable> borrowersTableList;
    private GroupBorrowerTable group;
    private BorrowersQueries borrowersQueries;
    private BorrowerGroupsQueries borrowerGroupsQueries;
    private BorrowersTableQueries borrowersTableQueries;
    private BorrowerPhotoValidationQueries borrowerPhotoValidationQueries;
    GroupPhotoValidationTableQueries groupPhotoValidationTableQueries;
    private Toolbar toolbar;

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

        group = getIntent().getParcelableExtra("group");
        Log.d(TAG, "onCreate: "+group.toString());

        groupNameTextView = findViewById(R.id.name);
        groupAddressTextView = findViewById(R.id.group_address);
        numberOfGroupMembersTextView = findViewById(R.id.number_of_members);
        numberOfBusiVerifTextView = findViewById(R.id.number_of_biz_verif);
        businessVerificationRecyclerView = findViewById(R.id.documentRecyclerView);
        groupMembersRecyclerView = findViewById(R.id.membersRecyclerView);


        groupPhotoValidationTables = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        businessVerificationRecyclerView.setLayoutManager(layoutManager);
        groupBusinessVerificationRecyclerAdapter = new GroupBusinessVerificationRecyclerAdapter(groupPhotoValidationTables);
        businessVerificationRecyclerView.setAdapter(groupBusinessVerificationRecyclerAdapter);
        getBusinessVerificationPhotos();

        borrowersTableList = new ArrayList<>();
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        groupMembersRecyclerView.setLayoutManager(layoutManager2);
        groupMembersRecyclerAdapter = new GroupMembersRecyclerAdapter(borrowersTableList);
        groupMembersRecyclerView.setAdapter(groupMembersRecyclerAdapter);
        getMembers();

        setGroupDetailsToUI();
    }

    private void getBusinessVerificationPhotos() {

        List<GroupPhotoValidationTable> loadPhotosForGroup = groupPhotoValidationTableQueries.loadPhotosForGroup(group.getGroupId());

        if(loadPhotosForGroup.isEmpty()){
            getNewBusinessVerificationPhotos();
        }else{
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

                                    saveGroupVerificationPhotoToStorage(groupPhotoValidationTable);
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

    private void saveGroupVerificationPhotoToStorage(GroupPhotoValidationTable groupPhotoValidationTable) {
        groupPhotoValidationTableQueries.insertBorrowersToStorage(groupPhotoValidationTable);
    }

    private void setGroupDetailsToUI() {
        groupNameTextView.setText(group.getGroupName());
        groupAddressTextView.setText(group.getMeetingLocation());
        numberOfGroupMembersTextView.setText(String.valueOf(group.getNumberOfGroupMembers()));
    }

    private void getMembers() {
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

                                                    BorrowersTable bow = saveBorrowerToLocalIfNotExist(borrowersTable);

                                                    borrowersTableList.add(bow);
                                                    groupMembersRecyclerAdapter.notifyDataSetChanged();

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
