package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.GroupLoanRecyclerAdapter;
import com.icubed.loansticdroid.adapters.SelectGroupToAddBorrowerRecyclerAdapter;
import com.icubed.loansticdroid.cloudqueries.BorrowerGroupsQueries;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.cloudqueries.GroupBorrowerQueries;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTableQueries;
import com.icubed.loansticdroid.models.SelectedBorrowerForGroup;
import com.icubed.loansticdroid.util.AndroidUtils;
import com.icubed.loansticdroid.util.EditTextExtension.CustomEditText;
import com.icubed.loansticdroid.util.EditTextExtension.DrawableClickListener;
import com.icubed.loansticdroid.util.KeyboardUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class AddBorrowerToExistingGroupActivity extends AppCompatActivity {
    
    private BorrowersTable borrower;
    private Toolbar toolbar;
    private CustomEditText searchEditText;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView groupRecyclerView;
    private ProgressBar progressBar; 
    
    public GroupBorrowerTable selectedGroup = null;
    public ImageView lastChecked = null;
    private GroupBorrowerTableQueries groupBorrowerTableQueries;
    private GroupBorrowerQueries groupBorrowerQueries;
    private SelectGroupToAddBorrowerRecyclerAdapter selectGroupToAddBorrowerRecyclerAdapter;
    private Index groupIndex;
    private BorrowerGroupsQueries borrowerGroupsQueries;
    private List<String> groupIds;
    private BorrowersQueries borrowersQueries;
    private ProgressBar addBorrowerProg;
    private LinearLayout searchEmptyLayout;
    private TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_borrower_to_existing_group);

        toolbar = findViewById(R.id.new_group_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select Group");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        searchEditText = findViewById(R.id.searchEditText);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        groupRecyclerView = findViewById(R.id.group_list);
        progressBar = findViewById(R.id.groupProgressBar);
        addBorrowerProg = findViewById(R.id.add_borrower_prog);
        searchEmptyLayout = findViewById(R.id.search_empty_layout);
        errorTextView = findViewById(R.id.errorTextView);

        searchDrawableButtonListener();
        searchGroupListener();

        borrowersQueries = new BorrowersQueries(this);

        groupIds = new ArrayList<>();

        groupBorrowerQueries = new GroupBorrowerQueries();
        groupBorrowerTableQueries = new GroupBorrowerTableQueries(getApplication());
        borrowerGroupsQueries = new BorrowerGroupsQueries();
        
        borrower = getIntent().getParcelableExtra("borrower");
        Log.d("ddd", borrower.toString());

        //Algolia search initiation
        Client client = new Client("HGQ25JRZ8Y", "d4453ddf82775ee2324c47244b30a7c7");
        groupIndex = client.getIndex("BORROWER_GROUP");
        
        swipeRefreshListener();
        getBorrowerGroup();
    }

    private void getBorrowerGroup() {
        if(borrower.getBelongsToGroup()){
            borrowerGroupsQueries.retrieveGroupsOfBorrower(borrower.getBorrowersId())
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                if(!task.getResult().isEmpty()){
                                    for(DocumentSnapshot doc: task.getResult()){
                                        groupIds.add(doc.getString("groupId"));
                                    }

                                    getAllGroups();
                                }
                            }else{
                                progressBar.setVisibility(View.GONE);
                                Log.d("Failed", "failed to retrieve borrower group");
                            }
                        }
                    });
        }else{
            getAllGroups();
        }
    }

    private void searchGroupListener() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchGroup(s);
            }
        });
    }

    /**
     * Search for groups
     *
     * @param s
     */
    private void searchGroup(final Editable s) {
        if (!TextUtils.isEmpty(s.toString())) {
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

                        if(list.isEmpty()){
                            errorTextView.setText("Sorry, cannot find group");
                            searchEmptyLayout.setVisibility(View.VISIBLE);
                            return;
                        }

                        List<GroupBorrowerTable> groupToRemove = new ArrayList<>();

                        if(!groupIds.isEmpty()){
                            for (GroupBorrowerTable table : list) {
                                for (String groupId : groupIds) {
                                    if(table.getGroupId().equals(groupId)){
                                        groupToRemove.add(table);
                                        break;
                                    }
                                }
                            }

                            list.removeAll(groupToRemove);
                        }

                        selectGroupToAddBorrowerRecyclerAdapter = new SelectGroupToAddBorrowerRecyclerAdapter(list);
                        groupRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        groupRecyclerView.setAdapter((selectGroupToAddBorrowerRecyclerAdapter));

                        if(list.isEmpty()){
                            searchEmptyLayout.setVisibility(View.VISIBLE);
                        }

                        //This is to check immediately after the search to know if string is empty
                        if (TextUtils.isEmpty(s.toString())) {
                            loadGroupsToUI();
                        }

                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            loadGroupsToUI();
        }
    }

    private void searchDrawableButtonListener() {
        searchEditText.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case LEFT:
                        KeyboardUtil.hideKeyboard(AddBorrowerToExistingGroupActivity.this);
                        searchEditText.setVisibility(View.GONE);
                        break;

                    case RIGHT:
                        searchEditText.setText("");
                        break;

                    default:
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.select_loan_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem register = menu.findItem(R.id.next_to_loan_terms);
        register.setTitle("Submit");

        if(selectedGroup != null){
            register.setVisible(true);
        }else{
            register.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.next_to_loan_terms:
                submitButtonListener();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void submitButtonListener() {
        addBorrowerProg.setVisibility(View.VISIBLE);
        updateBorrowerProfile();
    }

    private void updateBorrowerProfile() {

        if(borrower.getBelongsToGroup()){
            registerBorrowerGroups(borrower.getBorrowersId());
        }else{
            borrowersQueries.updateBorrowerWhenAddedToGroup(borrower.getBorrowersId())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                registerBorrowerGroups(borrower.getBorrowersId());
                            }else{
                                Toast.makeText(getApplicationContext(), "Failed updating borrower details", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onComplete: "+task.getException().getMessage());
                                addBorrowerProg.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    private void registerBorrowerGroups(String borrowersId) {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("groupId", selectedGroup.getGroupId());
        objectMap.put("timestamp", new Date());

        borrowerGroupsQueries.saveNewGroupForBorrower(borrowersId, objectMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            updateGroupSize();
                        }else{
                            Toast.makeText(getApplicationContext(), "Failed updating borrower details", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                            addBorrowerProg.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void updateGroupSize() {
        int newGroupSize = selectedGroup.getNumberOfGroupMembers();
        newGroupSize++;

        Date date = new Date();
        selectedGroup.setLastUpdatedAt(date);
        selectedGroup.setNumberOfGroupMembers(newGroupSize);

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("numberOfGroupMembers", newGroupSize);
        objectMap.put("lastUpdatedAt", date);

        final int finalNewGroupSize = newGroupSize;
        groupBorrowerQueries.updateGroupAfterAddingNewMembers(selectedGroup.getGroupId(), objectMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            updateSearch(finalNewGroupSize);
                        }else{
                            addBorrowerProg.setVisibility(View.VISIBLE);
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void updateSearch(int newGroupSize){
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("groupMembersCount", newGroupSize);

            groupIndex.partialUpdateObjectAsync(jsonObject, selectedGroup.getGroupId(), new CompletionHandler() {
                @Override
                public void requestCompleted(@Nullable JSONObject jsonObject, @Nullable AlgoliaException e) {
                    if(e==null){
                        Intent intent = new Intent(getApplicationContext(), BorrowerDetailsGroup.class);
                        intent.putExtra("group", selectedGroup);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }else{
                        addBorrowerProg.setVisibility(View.VISIBLE);
                        Log.d(TAG, "requestCompleted: "+e.getMessage());
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //Swipe down refresh listener
    private void swipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(AndroidUtils.isMobileDataEnabled(getApplicationContext())) {
                    swipeRefreshLayout.setRefreshing(true);
                    loadAllGroupsAndCompareToLocal();
                }else{
                    Toast.makeText(getApplicationContext(), "Request failed, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getAllGroups() {
        if(!doesGroupTableExistInLocalStorage()){
            loadAllGroups();
        }else{
            swipeRefreshLayout.setRefreshing(true);
            loadGroupsToUI();
            loadAllGroupsAndCompareToLocal();
        }
    }

    public Boolean doesGroupTableExistInLocalStorage(){
        List<GroupBorrowerTable> borrowersTables = groupBorrowerTableQueries.loadAllGroups();
        return !borrowersTables.isEmpty();
    }

    /****
     * @TODO
     * to method borrowersQueries.retrieveAllBorrowers() to borrowersQueries.retrieveAllBorrowersForLoanOfficer()
     * this is necessary so that only the borrowers assigned to a loan officer is seen on his borrowers page.
     * if the officers need to search for a non assigned borrower, he has to use the search field
     */
    public void loadAllGroups(){

        groupBorrowerQueries.retrieveAllBorrowersGroup().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().isEmpty()){

                        for(DocumentSnapshot doc : task.getResult().getDocuments()){
                            GroupBorrowerTable groupBorrowerTable = doc.toObject(GroupBorrowerTable.class);
                            groupBorrowerTable.setGroupId(doc.getId());

                            saveBorrowersToLocalStorage(groupBorrowerTable);
                        }

                        loadGroupsToUI();
                    }else{
                        removeRefresher();
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Document is empty", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Log.d(TAG, "onComplete: "+task.getException().getMessage());
                }
            }
        });
    }

    private void saveBorrowersToLocalStorage(GroupBorrowerTable groupBorrowerTable) {
        groupBorrowerTableQueries.insertGroupToStorage(groupBorrowerTable);
    }

    public void loadGroupsToUI(){
        List<GroupBorrowerTable> groupBorrowerTables = groupBorrowerTableQueries.loadAllGroupsOrderByLastName();

        List<GroupBorrowerTable> groupToRemove = new ArrayList<>();

        if(!groupIds.isEmpty()){
            for (GroupBorrowerTable table : groupBorrowerTables) {
                for (String groupId : groupIds) {
                    if(table.getGroupId().equals(groupId)){
                        groupToRemove.add(table);
                        break;
                    }
                }
            }

            groupBorrowerTables.removeAll(groupToRemove);
        }

        selectGroupToAddBorrowerRecyclerAdapter = new SelectGroupToAddBorrowerRecyclerAdapter(groupBorrowerTables);
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        groupRecyclerView.setAdapter((selectGroupToAddBorrowerRecyclerAdapter));

        if(groupBorrowerTables.isEmpty()){
            searchEmptyLayout.setVisibility(View.VISIBLE);
        }

        progressBar.setVisibility(View.GONE);
    }

    /****
     * @TODO
     * to method borrowersQueries.retrieveAllBorrowers() to borrowersQueries.retrieveAllBorrowersForLoanOfficer()
     * this is necessary so that only the borrowers assigned to a loan officer is seen on his borrowers page.
     * if the officers need to search for a non assigned borrower, he has to use the search field
     */
    public void loadAllGroupsAndCompareToLocal() {
        final List<GroupBorrowerTable> allGroups = groupBorrowerTableQueries.loadAllGroups();

        groupBorrowerQueries.retrieveAllBorrowersGroup()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){

                                Boolean isThereNewData = false;
                                List<GroupBorrowerTable> groupsInStorage = allGroups;
                                for(DocumentSnapshot doc : task.getResult().getDocuments()) {

                                    Boolean doesDataExist = false;
                                    for (GroupBorrowerTable bowTab : allGroups) {
                                        if (bowTab.getGroupId().equals(doc.getId())) {
                                            doesDataExist = true;
                                            groupsInStorage.remove(bowTab);
                                            Log.d(TAG, "onComplete: Group id of " + doc.getId() + " already exist");
                                            break;
                                        }
                                    }

                                    if (!doesDataExist) {
                                        Log.d(TAG, "onComplete: group id of " + doc.getId() + " does not exist");

                                        GroupBorrowerTable groupBorrowerTable = doc.toObject(GroupBorrowerTable.class);
                                        groupBorrowerTable.setGroupId(doc.getId());
                                        isThereNewData = true;

                                        saveBorrowersToLocalStorage(groupBorrowerTable);
                                    }else{
                                        //Update local table if any changes
                                        updateTable(doc);
                                    }
                                }

                                //to delete deleted borrower in cloud from storage
                                if(!groupsInStorage.isEmpty()){
                                    for(GroupBorrowerTable borowTab : groupsInStorage){
                                        deleteGroupFromLocalStorage(borowTab);
                                        Log.d("Delete", "deleted "+borowTab.getGroupId()+ " from storage");
                                    }
                                }

                                if(isThereNewData || !groupsInStorage.isEmpty()) {
                                    loadGroupsToUI();
                                }
                                removeRefresher();
                            }else{
                                Toast.makeText(getApplicationContext(), "Document is empty", Toast.LENGTH_SHORT).show();
                                removeRefresher();
                            }
                        }else{
                            Log.d("Group", "onComplete: "+task.getException().getMessage());
                            removeRefresher();
                        }
                    }
                });
    }

    private void deleteGroupFromLocalStorage(GroupBorrowerTable borowTab) {
        groupBorrowerTableQueries.deleteGroupFromStorage(borowTab);
    }

    private void removeRefresher(){
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.destroyDrawingCache();
        swipeRefreshLayout.clearAnimation();
    }

    private void updateTable(DocumentSnapshot doc) {
        GroupBorrowerTable groupBorrowerTable = doc.toObject(GroupBorrowerTable.class);
        groupBorrowerTable.setGroupId(doc.getId());

        GroupBorrowerTable currentlySaved = groupBorrowerTableQueries.loadSingleBorrowerGroup(doc.getId());
        groupBorrowerTable.setId(currentlySaved.getId());

        if(groupBorrowerTable.getLastUpdatedAt().getTime() != currentlySaved.getLastUpdatedAt().getTime()){

            groupBorrowerTableQueries.updateGroupDetails(groupBorrowerTable);
            loadGroupsToUI();
            Log.d("Group", "Group Detailed updated");

        }
    }
}
