package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.cloudqueries.Account;
import com.icubed.loansticdroid.cloudqueries.BorrowerGroupsQueries;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.cloudqueries.GroupBorrowerQueries;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.models.GroupBorrower;
import com.icubed.loansticdroid.models.SelectedBorrowerForGroup;
import com.icubed.loansticdroid.notification.GroupNotificationQueries;
import com.icubed.loansticdroid.notification.GroupNotificationTable;
import com.icubed.loansticdroid.util.AndroidUtils;
import com.icubed.loansticdroid.util.FormUtil;
import com.icubed.loansticdroid.util.LocationProviderUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class GroupDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private List<SelectedBorrowerForGroup> groupList;
    private String groupLeaderId;
    private LocationProviderUtil locationProviderUtil;
    private GroupBorrowerQueries groupBorrowerQueries;
    private BorrowersQueries borrowersQueries;
    private EditText groupNameEditText, meetingLocationEditText;
    private BorrowerGroupsQueries borrowerGroupsQueries;
    private Account account;
    private Location local;
    private Index index;
    private Button submitBtn;
    private FormUtil formUtil;
    private ProgressBar groupProgressBar;
    private boolean gottenLocation = false;
    private GroupNotificationQueries groupNotificationQueries;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        groupNameEditText = findViewById(R.id.groupName);
        meetingLocationEditText = findViewById(R.id.meetingLocation);
        submitBtn = findViewById(R.id.submit_group);
        groupProgressBar = findViewById(R.id.group_progressbar);

        submitBtnClickListener();

        toolbar = findViewById(R.id.group_details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Group Registration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        groupLeaderId = getIntent().getStringExtra("groupLeaderId");
        groupList = getIntent().getParcelableArrayListExtra("selectedBorrowers");
        Log.d(TAG, "onCreate: "+groupList.toString());

        locationProviderUtil = new LocationProviderUtil(this);
        groupBorrowerQueries = new GroupBorrowerQueries();
        account = new Account();
        borrowersQueries = new BorrowersQueries(this);
        formUtil = new FormUtil();
        groupNotificationQueries = new GroupNotificationQueries();
        borrowerGroupsQueries = new BorrowerGroupsQueries();

        //Algolia search initiation
        Client client = new Client("HGQ25JRZ8Y", "d4453ddf82775ee2324c47244b30a7c7");
        index = client.getIndex("BORROWER_GROUP");
    }

    private void submitBtnClickListener() {
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText[] forms = new EditText[]{groupNameEditText, meetingLocationEditText};
                if(isAnyFormEmpty(forms))
                    return;

                if(AndroidUtils.isMobileDataEnabled(getApplicationContext())) {
                    showProgressBar();
                    getCurrentLocation();
                }else{
                    Toast.makeText(getApplicationContext(), "Request failed, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getCurrentLocation(){
        locationProviderUtil.requestSingleUpdate(new LocationProviderUtil.LocationCallback() {
            @Override
            public void onNewLocationAvailable(LocationProviderUtil.GPSCoordinates location) {
                if(!gottenLocation) {
                    local = location.getLocation;
                    createGroup();
                    gottenLocation = true;
                }
            }
        });
    }

    public void searchAbleAttributes(){
        JSONObject settings = null;
        try {
            settings = new JSONObject(
                    "{\"searchableAttributes\": [\"groupName\"]}"
            );
            index.setSettingsAsync(settings, false, null, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void createGroup(){

        Map<String, Object> groupMap = new HashMap<>();
        groupMap.put("groupName", groupNameEditText.getText().toString());
        groupMap.put("groupLeaderId", groupLeaderId);
        groupMap.put("numberOfGroupMembers", groupList.size());
        groupMap.put("loanOfficerId", account.getCurrentUserId());
        groupMap.put("isGroupApproved", false);
        groupMap.put("approvedBy", "");
        groupMap.put("assignedBy", "");
        groupMap.put("meetingLocation",meetingLocationEditText.getText().toString());
        groupMap.put("groupLocationLatitude", local.getLatitude());
        groupMap.put("groupLocationLongitude", local.getLongitude());
        groupMap.put("timestamp", new Date());
        groupMap.put("lastUpdatedAt", new Date());

        groupBorrowerQueries.createBorrowersGroup(groupMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            //Send notification
                            sendNotification(task.getResult().getId());
                            //update borrower details
                            updateBorrowerProfile(task.getResult().getId());
                        }else{
                            hideProgressBar();
                            Toast.makeText(GroupDetailsActivity.this, "Failed to register group", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateBorrowerProfile(final String groupId) {

        for(final SelectedBorrowerForGroup borrower : groupList){

            if(borrower.isBelongsToGroup()){
                registerBorrowerGroups(borrower.getBorrowersId(), groupId);
            }else{
                borrowersQueries.updateBorrowerWhenAddedToGroup(borrower.getBorrowersId())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    registerBorrowerGroups(borrower.getBorrowersId(), groupId);

                                }else{
                                    Toast.makeText(GroupDetailsActivity.this, "Failed updating borrower details", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onComplete: "+task.getException().getMessage());
                                    hideProgressBar();
                                }
                            }
                        });
            }

        }

    }

    private void registerBorrowerGroups(String borrowersId, final String groupId) {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("groupId", groupId);
        objectMap.put("timestamp", new Date());

        borrowerGroupsQueries.saveNewGroupForBorrower(borrowersId, objectMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            count++;

                            if(count == groupList.size())
                                registerGroupForSearch(groupId);
                        }else{
                            Toast.makeText(GroupDetailsActivity.this, "Failed updating borrower details", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                            hideProgressBar();
                        }
                    }
                });
    }

    private void registerGroupForSearch(final String groupId) {
        Map<String, Object> searchMap = new HashMap<>();
        searchMap.put("groupName", groupNameEditText.getText().toString());
        searchMap.put("groupMembersCount", groupList.size());

        JSONObject object = new JSONObject(searchMap);
        index.addObjectAsync(object, groupId, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject jsonObject, AlgoliaException e) {

                if(e == null) {
                    Toast.makeText(getApplicationContext(), "New Group Added Successfully", Toast.LENGTH_SHORT).show();

                    //Going to business verification page to verify business location
                    Intent businessVerificationIntent = new Intent(getApplicationContext(), BusinessVerification.class);
                    businessVerificationIntent.putExtra("groupId", groupId);
                    startActivity(businessVerificationIntent);
                    finish();
                }else{
                    hideProgressBar();
                    Toast.makeText(getApplicationContext(), "Failed to register group for search", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "requestCompleted: "+e.getMessage());
                }
            }
        });
    }

    private void sendNotification(String groupId){
        GroupNotificationTable groupNotificationTable = new GroupNotificationTable();
        groupNotificationTable.setGroupId(groupId);
        groupNotificationTable.setTimestamp(new Date());

        groupNotificationQueries.sendNotification(groupNotificationTable, account.getCurrentUserId())
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "onSuccess: notification sent");
                    }
                });
    }

    private void showProgressBar(){
        groupProgressBar.setVisibility(View.VISIBLE);
        submitBtn.setEnabled(false);
    }

    private void hideProgressBar(){
        groupProgressBar.setVisibility(View.GONE);
        submitBtn.setEnabled(true);
    }

    public Boolean isAnyFormEmpty(EditText[] forms){
        Boolean isFormEmpty = false;
        boolean[] listOfFormsEmpty = formUtil.isListOfFormsEmpty(forms);

        for(int i = 0; i < forms.length; i++){
            if(listOfFormsEmpty[i]){
                forms[i].setError("Field is required");

                if(!isFormEmpty) {
                    forms[i].requestFocus();
                }

                isFormEmpty = true;
            }else{
                forms[i].setError(null);
            }
        }

        return isFormEmpty;
    }
}
