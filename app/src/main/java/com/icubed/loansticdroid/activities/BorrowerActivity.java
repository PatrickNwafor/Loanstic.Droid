package com.icubed.loansticdroid.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.BorrowerRecyclerAdapter;
import com.icubed.loansticdroid.adapters.GroupRecyclerAdapter;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.fragments.BorrowersFragment.GroupBorrowerFragment;
import com.icubed.loansticdroid.fragments.BorrowersFragment.SingleBorrowerFragment;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.BorrowersTableQueries;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.models.Borrowers;
import com.icubed.loansticdroid.models.Savings;
import com.icubed.loansticdroid.util.AndroidUtils;
import com.icubed.loansticdroid.util.BitmapUtil;
import com.icubed.loansticdroid.util.EditTextExtension.CustomEditText;
import com.icubed.loansticdroid.util.EditTextExtension.DrawableClickListener;
import com.icubed.loansticdroid.util.KeyboardUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;

public class BorrowerActivity extends AppCompatActivity {


    private static final String TAG = ".BorrowerActivity";
    public ProgressBar borrowerProgressBar;
    private EditText searchBorrowerEditText;
    public SegmentedButtonGroup sbg;
    Index index;
    Index groupIndex;
    private Toolbar toolbar;
    private int searchPosition = 0;
    private BorrowersTableQueries borrowersTableQueries;

    private SingleBorrowerFragment singleBorrowerFragment;
    private GroupBorrowerFragment groupBorrowerFragment;

    public boolean isSearch = false;
    public boolean isGroupSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrower);

        groupBorrowerFragment = new GroupBorrowerFragment();
        singleBorrowerFragment = new SingleBorrowerFragment();

        borrowersTableQueries = new BorrowersTableQueries(getApplication());

        startFragment(singleBorrowerFragment, "single");

        //Views
        searchBorrowerEditText = findViewById(R.id.searchEditText);

        borrowerProgressBar = findViewById(R.id.borrowerProgressBar);

        toolbar = findViewById(R.id.borrower_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Borrowers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Algolia search initiation
        Client client = new Client("HGQ25JRZ8Y", "d4453ddf82775ee2324c47244b30a7c7");
        index = client.getIndex("Borrowers");
        groupIndex = client.getIndex("BORROWER_GROUP");

        searchBorrowerListener();

        //segmented control
        sbg = findViewById(R.id.segmentedButtonGroup);
        sbg.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition(){
            @Override
            public void onClickedButtonPosition(int position){
                if(position==0) {
                    searchPosition = 0;
                    startFragment(singleBorrowerFragment, "single");
                }
                else if (position==1) {
                    searchPosition = 1;
                    startFragment(groupBorrowerFragment, "group");
                }
            }
        });



    }

    /************Instantiate fragment transactions**********/
    public void startFragment(Fragment fragment, String fragmentTag){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, fragment, fragmentTag);
        transaction.commit();
    }

    private void searchBorrowerListener(){
        searchBorrowerEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged( Editable s) {

                if(searchPosition == 0) {
                    searchBorrowers(s);
                }else if(searchPosition == 1){
                    searchGroup(s);
                }

            }
        });
    }

    /**
     * Search for groups
     * @param s
     */
    private void searchGroup(final Editable s) {
        if(!TextUtils.isEmpty(s.toString())) {
            //Search for data from cloud storage
            Query query = new Query(s.toString());
            query.setAttributesToRetrieve("*");
            query.setMinWordSizefor2Typos(3);
            query.setHitsPerPage(50);

            groupIndex.searchAsync(query, new CompletionHandler() {
                @Override
                public void requestCompleted(JSONObject content, AlgoliaException error) {
                    try {
                        JSONArray hits = content.getJSONArray("hits");
                        List<GroupBorrowerTable> list = new ArrayList<>();

                        for (int i = 0; i < hits.length(); i++) {
                            JSONObject jsonObject = hits.getJSONObject(i);
                            String groupName = jsonObject.getString("groupName");
                            int groupMembers = jsonObject.getInt("groupMembersCount");
                            String groupId = jsonObject.getString("objectID");

                            GroupBorrowerTable groupBorrowerTable = new GroupBorrowerTable();
                            groupBorrowerTable.setGroupId(groupId);
                            groupBorrowerTable.setGroupName(groupName);
                            groupBorrowerTable.setNumberOfGroupMembers(groupMembers);
                            list.add(groupBorrowerTable);
                        }

                        isGroupSearch = true;
                        groupBorrowerFragment.groupRecyclerAdapter = new GroupRecyclerAdapter(list);
                        groupBorrowerFragment.groupRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        groupBorrowerFragment.groupRecyclerView.setAdapter(groupBorrowerFragment.groupRecyclerAdapter);

                        //This is to check immediately after the search to know if string is empty
                        if(TextUtils.isEmpty(s.toString())){
                            groupBorrowerFragment.groups.loadGroupsToUI();
                        }

                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else{
            groupBorrowerFragment.groups.loadGroupsToUI();
        }
    }


    /**
     * search for borrowers
     * @param s
     */
    private void searchBorrowers(final Editable s) {
        if(!TextUtils.isEmpty(s.toString())) {
            //Search for data from cloud storage
            Query query = new Query(s.toString());
            query.setAttributesToRetrieve("*");
            query.setMinWordSizefor2Typos(3);
            query.setHitsPerPage(50);

            index.searchAsync(query, new CompletionHandler() {
                @Override
                public void requestCompleted(JSONObject content, AlgoliaException error) {
                    try {
                        JSONArray hits = content.getJSONArray("hits");
                        List<BorrowersTable> list = new ArrayList<>();

                        for (int i = 0; i < hits.length(); i++) {
                            JSONObject jsonObject = hits.getJSONObject(i);
                            String lastName = jsonObject.getString("lastName");
                            String firstName = jsonObject.getString("firstName");
                            String businessName = jsonObject.getString("businessName");
                            String borrowerId = jsonObject.getString("objectID");
                            String profileImageThumbUri = jsonObject.getString("profileImageThumbUri");
                            String profileImageUri = jsonObject.getString("profileImageUri");

                            BorrowersTable borrowersTable = new BorrowersTable();
                            borrowersTable.setBorrowersId(borrowerId);
                            borrowersTable.setLastName(lastName);
                            borrowersTable.setFirstName(firstName);
                            borrowersTable.setBusinessName(businessName);
                            borrowersTable.setProfileImageThumbUri(profileImageThumbUri);
                            borrowersTable.setProfileImageUri(profileImageUri);
                            list.add(borrowersTable);
                        }

                        isSearch = true;
                        singleBorrowerFragment.borrowerRecyclerAdapter = new BorrowerRecyclerAdapter(list);
                        singleBorrowerFragment.borrowerRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        singleBorrowerFragment.borrowerRecyclerView.setAdapter(singleBorrowerFragment.borrowerRecyclerAdapter);

                        //This is to check immediately after the search to know if string is empty
                        if(TextUtils.isEmpty(s.toString())){
                            singleBorrowerFragment.borrowers.loadBorrowersToUI();
                        }

                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else{
            singleBorrowerFragment.borrowers.loadBorrowersToUI();
        }

    }

    public void searchAbleAttributes(){
        JSONObject settings = null;
        try {
            settings = new JSONObject(
                    "{\"searchableAttributes\": [\"lastName\", \"firstName\", \"businessName\"]}"
            );
            index.setSettingsAsync(settings, false, null, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.borrower_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.nav_new_borrower:
                startAnotherActivity(AddSingleBorrower.class);
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_search:
                searchBorrowerEditText.setVisibility(View.VISIBLE);
                searchBorrowerEditText.requestFocus();
                KeyboardUtil.showKeyboard(BorrowerActivity.this);
                return true;

            case R.id.nav_new_group:
                startAnotherActivity(AddGroupBorrower.class);
                return true;

                default:
                 return super.onOptionsItemSelected(item);
        }
    }

    private void startAnotherActivity(Class newActivity){
        Intent newActivityIntent = new Intent(this, newActivity);
        startActivity(newActivityIntent);
    }

    @Override
    public void onBackPressed() {
        if(searchBorrowerEditText.getVisibility() == View.VISIBLE){
            searchBorrowerEditText.setVisibility(View.GONE);
            return;
        }

        super.onBackPressed();
    }

    public void getBorrowerImage(BorrowersTable borrowersTable) {
        List<BorrowersTable> borrowersTableList = borrowersTableQueries.loadAllBorrowers();

        for (final BorrowersTable table : borrowersTableList) {
            if(borrowersTable.getBorrowersId().equals(table.getBorrowersId())
                    && table.getBorrowerImageByteArray() == null){

                BitmapUtil.getImageWithGlide(getApplicationContext(), borrowersTable.getProfileImageUri())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                saveImage(resource, table);
                            }
                        });
                return;

            }else if(borrowersTable.getBorrowersId().equals(table.getBorrowersId())
                    && table.getBorrowerImageByteArray() != null
                    && !borrowersTable.getProfileImageUri().equals(table.getProfileImageUri())){

                BitmapUtil.getImageWithGlide(getApplicationContext(), borrowersTable.getProfileImageUri())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                saveImage(resource, table);
                            }
                        });
                return;

            }
        }

    }

    private void saveImage(Bitmap resource, BorrowersTable borrowersTable) {
        byte[] bytes = BitmapUtil.getBytesFromBitmapInJPG(resource, 100);
        borrowersTable.setBorrowerImageByteArray(bytes);
        borrowersTableQueries.updateBorrowerDetails(borrowersTable);

        Log.d(TAG, "saveImage: borrower image byte[] saved");
    }
}
