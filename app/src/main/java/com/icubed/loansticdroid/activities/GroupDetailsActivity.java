package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.cloudqueries.Account;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.cloudqueries.GroupBorrowerQueries;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.models.GroupBorrower;
import com.icubed.loansticdroid.models.SelectedBorrowerForGroup;
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
    private Account account;
    private Location local;
    private Index index;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        toolbar = findViewById(R.id.group_details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Group Registration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        groupLeaderId = getIntent().getStringExtra("groupLeaderId");
        groupList = getIntent().getParcelableArrayListExtra("selectedBorrowers");

        locationProviderUtil = new LocationProviderUtil(this);
        groupBorrowerQueries = new GroupBorrowerQueries();
        account = new Account();
        borrowersQueries = new BorrowersQueries(this);

        //Algolia search initiation
        Client client = new Client("HGQ25JRZ8Y", "d4453ddf82775ee2324c47244b30a7c7");
        index = client.getIndex("BORROWER_GROUP");
    }

    private void getCurrentLocation(){
        locationProviderUtil.requestSingleUpdate(new LocationProviderUtil.LocationCallback() {
            @Override
            public void onNewLocationAvailable(LocationProviderUtil.GPSCoordinates location) {
                local = location.getLocation;
                createBorrower();
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

    private void createBorrower(){

        Map<String, Object> groupMap = new HashMap<>();
        //groupMap.put("groupName", );
        groupMap.put("groupLeaderId", groupLeaderId);
        groupMap.put("numberOfGroupMembers", groupList.size());
        groupMap.put("loanOfficerId", account.getCurrentUserId());
        groupMap.put("isGroupApproved", false);
        //groupMap.put("meetingLocation",);
        groupMap.put("groupLocationLatitude", local.getLatitude());
        groupMap.put("groupLocationLongitude", local.getLongitude());
        groupMap.put("timestamp", new Date());

        groupBorrowerQueries.createBorrowersGroup(groupMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            updateBorrowerProfile(task.getResult().getId());
                        }else{
                            Toast.makeText(GroupDetailsActivity.this, "Failed to register group", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateBorrowerProfile(final String groupId) {

        for(SelectedBorrowerForGroup borrower : groupList){
            borrowersQueries.updateBorrowerWhenAddedToGroup(groupId, borrower.getBorrowersId())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                if(count == groupList.size()-1)
                                    registerGroupForSearch(groupId);
                            }else{
                                Toast.makeText(GroupDetailsActivity.this, "Failed updating borrower details", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onComplete: "+task.getException().getMessage());
                            }
                        }
                    });

            count++;
        }

    }

    private void registerGroupForSearch(final String groupId) {
        Map<String, Object> searchMap = new HashMap<>();
        //searchMap.put("groupName", );

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
                    Toast.makeText(getApplicationContext(), "Failed to register group for search", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "requestCompleted: "+e.getMessage());
                }
            }
        });
    }
}
