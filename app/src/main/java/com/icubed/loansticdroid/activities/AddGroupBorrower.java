package com.icubed.loansticdroid.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.icubed.loansticdroid.adapters.BorrowerRecyclerAdapter;
import com.icubed.loansticdroid.adapters.GroupBorrowerListRecyclerAdapter;
import com.icubed.loansticdroid.adapters.SelectedBorrowerForGroupRecyclerAdapter;
import com.icubed.loansticdroid.cloudqueries.BorrowerGroupsQueries;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.cloudqueries.GroupBorrowerQueries;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.BorrowersTableQueries;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.models.Borrowers;
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
import static android.view.View.GONE;

public class AddGroupBorrower extends AppCompatActivity {

    private CustomEditText searchEditText;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView borrowerRecyclerView, selectedBorrowerRecyclerView;
    private ProgressBar progressBar, addBorrowerProg;

    public List<SelectedBorrowerForGroup> selectedBorrowerList;
    private List<BorrowersTable> borrowersTables;
    public List<BorrowersTable> alreadyAddedBorrower;
    public GroupBorrowerListRecyclerAdapter groupBorrowerListRecyclerAdapter;
    public SelectedBorrowerForGroupRecyclerAdapter selectedBorrowerForGroupRecyclerAdapter;
    private BorrowersTableQueries borrowersTableQueries;
    private BorrowersQueries borrowersQueries;
    GroupBorrowerQueries groupBorrowerQueries;
    private Index index;
    private GroupBorrowerTable group;
    private Toolbar toolbar;
    private BorrowerGroupsQueries borrowerGroupsQueries;
    int count = 0;
    private Index groupIndex;
    public boolean isNextVisible = false;
    private MenuItem register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_borrower);

        borrowersTableQueries = new BorrowersTableQueries(getApplication());
        borrowersQueries = new BorrowersQueries(this);
        groupBorrowerQueries = new GroupBorrowerQueries();

        searchEditText = findViewById(R.id.searchEditText);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        borrowerRecyclerView = findViewById(R.id.borrower_list);
        selectedBorrowerRecyclerView = findViewById(R.id.busiVerifRecyclerView);
        progressBar = findViewById(R.id.borrowerProgressBar);
        addBorrowerProg = findViewById(R.id.add_borrower_prog);

        borrowerGroupsQueries = new BorrowerGroupsQueries();

        alreadyAddedBorrower = getIntent().getParcelableArrayListExtra("already_added_borrowers");
        group = getIntent().getParcelableExtra("group");

        toolbar = findViewById(R.id.new_group_toolbar);
        setSupportActionBar(toolbar);

        if(alreadyAddedBorrower != null) getSupportActionBar().setTitle("Select borrower to add");
        else getSupportActionBar().setTitle("New group");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //For horizontal recycler adapter
        selectedBorrowerList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        selectedBorrowerRecyclerView.setLayoutManager(layoutManager);
        selectedBorrowerForGroupRecyclerAdapter = new SelectedBorrowerForGroupRecyclerAdapter(selectedBorrowerList);
        selectedBorrowerRecyclerView.setAdapter(selectedBorrowerForGroupRecyclerAdapter);

        //Swipe down refresher initialization
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_dark,
                R.color.colorAccent);
        swipeRefreshListener();

        //Algolia search initiation
        Client client = new Client("HGQ25JRZ8Y", "d4453ddf82775ee2324c47244b30a7c7");
        index = client.getIndex("Borrowers");

        groupIndex = client.getIndex("BORROWER_GROUP");

        searchBorrowerListener();
        searchDrawableButtonListener();
        getAllBorrowers();
    }

    private void createNewGroup() {
        Intent mainActivityIntent = new Intent(AddGroupBorrower.this, SelectGroupLeader.class);
        mainActivityIntent.putParcelableArrayListExtra("selectedBorrowers", (ArrayList<? extends Parcelable>) selectedBorrowerList);
        startActivity(mainActivityIntent);
    }

    private void updateBorrowerProfile() {

        for(final SelectedBorrowerForGroup borrower : selectedBorrowerList){

            if(borrower.isBelongsToGroup()){
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
                                    register.setEnabled(true);
                                }
                            }
                        });
            }

        }

    }

    private void registerBorrowerGroups(String borrowersId) {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("groupId", group.getGroupId());
        objectMap.put("timestamp", new Date());

        borrowerGroupsQueries.saveNewGroupForBorrower(borrowersId, objectMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            count++;

                            if(count == selectedBorrowerList.size())
                                updateGroupSize();
                        }else{
                            Toast.makeText(getApplicationContext(), "Failed updating borrower details", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                            addBorrowerProg.setVisibility(View.GONE);
                            register.setEnabled(true);
                        }
                    }
                });
    }

    private void updateGroupSize() {
        final int newGroupSize = group.getNumberOfGroupMembers()+selectedBorrowerList.size();

        Date date = new Date();
        group.setLastUpdatedAt(date);
        group.setNumberOfGroupMembers(newGroupSize);

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("numberOfGroupMembers", newGroupSize);
        objectMap.put("lastUpdatedAt", date);

        groupBorrowerQueries.updateGroupAfterAddingNewMembers(group.getGroupId(), objectMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            updateSearch(newGroupSize);
                        }else{
                            addBorrowerProg.setVisibility(View.VISIBLE);
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                            register.setEnabled(true);
                        }
                    }
                });
    }

    private void updateSearch(int newGroupSize){
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("groupMembersCount", newGroupSize);

            groupIndex.partialUpdateObjectAsync(jsonObject, group.getGroupId(), new CompletionHandler() {
                @Override
                public void requestCompleted(@Nullable JSONObject jsonObject, @Nullable AlgoliaException e) {
                    if(e==null){
                        Intent intent = new Intent(getApplicationContext(), BorrowerDetailsGroup.class);
                        intent.putExtra("group", group);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }else{
                        addBorrowerProg.setVisibility(View.VISIBLE);
                        Log.d(TAG, "requestCompleted: "+e.getMessage());
                        register.setEnabled(true);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void searchDrawableButtonListener() {
        searchEditText.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case LEFT:
                        KeyboardUtil.hideKeyboard(AddGroupBorrower.this);
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

    private void searchBorrowerListener() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged( Editable s) {

                searchBorrowers(s);

            }
        });
    }

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

                        borrowersTables = list;

                        groupBorrowerListRecyclerAdapter = new GroupBorrowerListRecyclerAdapter(borrowersTables);
                        borrowerRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        borrowerRecyclerView.setAdapter(groupBorrowerListRecyclerAdapter);

                        //This is to check immediately after the search to know if string is empty
                        if(TextUtils.isEmpty(s.toString())){
                            loadBorrowersToUI();
                        }

                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else{
            loadBorrowersToUI();
        }

    }

    private void swipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                if(AndroidUtils.isMobileDataEnabled(getApplicationContext())) {
                    if(borrowersTables.size() > 0) {
                        loadAllBorrowersAndCompareToLocal();
                    }else{
                        loadAllBorrowers();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Request failed, please try again", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void getAllBorrowers() {
        if (!doesBorrowersTableExistInLocalStorage()) {

            //Checking if cellular is turned on
            if(AndroidUtils.isMobileDataEnabled(this)) {
                loadAllBorrowers();
            }else{
                Toast.makeText(this, "Request failed, please try again", Toast.LENGTH_SHORT).show();
                hideProgressBar();
            }

        } else {
            swipeRefreshLayout.setRefreshing(true);
            loadBorrowersToUI();
            loadAllBorrowersAndCompareToLocal();
        }
    }

    public Boolean doesBorrowersTableExistInLocalStorage(){
        List<BorrowersTable> borrowersTables = borrowersTableQueries.loadAllBorrowers();
        if(borrowersTables.isEmpty()){
            return false;
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.loanee_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        register = menu.findItem(R.id.next_to_loan_terms);

        if(isNextVisible){
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

            case R.id.search_loan:
                searchEditText.setVisibility(View.VISIBLE);
                searchEditText.requestFocus();
                KeyboardUtil.showKeyboard(this);
                return true;

            case R.id.next_to_loan_terms:
                if(alreadyAddedBorrower == null){
                    createNewGroup();
                }else{
                    register.setEnabled(false);
                    addBorrowerProg.setVisibility(View.VISIBLE);
                    updateBorrowerProfile();
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /****
     * @TODO
     * to method borrowersQueries.retrieveAllBorrowers() to borrowersQueries.retrieveAllBorrowersForLoanOfficer()
     * this is necessary so that only the borrowers assigned to a loan officer is seen on his borrowers page.
     * if the officers need to search for a non assigned borrower, he has to use the search field
     */
    public void loadAllBorrowers(){
        borrowersQueries.retrieveAllBorrowers().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().isEmpty()){

                        for(DocumentSnapshot doc : task.getResult().getDocuments()){
                            BorrowersTable borrowersTable = doc.toObject(BorrowersTable.class);
                            borrowersTable.setBorrowersId(doc.getId());

                            saveBorrowersToLocalStorage(borrowersTable);
                        }

                        loadBorrowersToUI();
                    }else{
                        Toast.makeText(getApplicationContext(), "Document is empty", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Log.d(TAG, "onComplete: "+task.getException().getMessage());
                }
            }
        });
    }

    private void saveBorrowersToLocalStorage(BorrowersTable borrowersTable) {
        borrowersTableQueries.insertBorrowersToStorage(borrowersTable);
    }

    public void loadBorrowersToUI(){
        borrowersTables = borrowersTableQueries.loadAllBorrowersOrderByLastName();

        //For vertical recycler Adapter
        List<BorrowersTable> valuesToRemove = new ArrayList<>();
        if(alreadyAddedBorrower != null){
            for (BorrowersTable table : borrowersTables) {
                for (BorrowersTable added : alreadyAddedBorrower) {
                    if(table.getBorrowersId().equals(added.getBorrowersId())){
                        valuesToRemove.add(table);
                        break;
                    }
                }

            }

            if(!valuesToRemove.isEmpty()){
                borrowersTables.removeAll(valuesToRemove);
            }
        }
        groupBorrowerListRecyclerAdapter = new GroupBorrowerListRecyclerAdapter(borrowersTables);
        borrowerRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        borrowerRecyclerView.setAdapter(groupBorrowerListRecyclerAdapter);
        hideProgressBar();
    }

    /****
     * @TODO
     * to method borrowersQueries.retrieveAllBorrowers() to borrowersQueries.retrieveAllBorrowersForLoanOfficer()
     * this is necessary so that only the borrowers assigned to a loan officer is seen on his borrowers page.
     * if the officers need to search for a non assigned borrower, he has to use the search field
     */
    public void loadAllBorrowersAndCompareToLocal() {
        final List<BorrowersTable> borrowerList = borrowersTableQueries.loadAllBorrowers();

        borrowersQueries.retrieveAllBorrowers()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){

                                Boolean isThereNewData = false;
                                List<BorrowersTable> borrowersInStorage = borrowerList;
                                for(DocumentSnapshot doc : task.getResult().getDocuments()) {

                                    Boolean doesDataExist = false;
                                    for (BorrowersTable bowTab : borrowerList) {
                                        if (bowTab.getBorrowersId().equals(doc.getId())) {
                                            doesDataExist = true;
                                            borrowersInStorage.remove(bowTab);
                                            Log.d(TAG, "onComplete: borrower id of " + doc.getId() + " already exist");
                                            break;
                                        }
                                    }

                                    if (!doesDataExist) {
                                        Log.d(TAG, "onComplete: borrower id of " + doc.getId() + " does not exist");

                                        BorrowersTable borrowersTable = doc.toObject(BorrowersTable.class);
                                        borrowersTable.setBorrowersId(doc.getId());
                                        isThereNewData = true;

                                        saveBorrowersToLocalStorage(borrowersTable);
                                    }else{
                                        //this is done if there is change in data
                                        updateTable(doc);
                                    }
                                }

                                //to delete deleted borrower in cloud from storage
                                if(!borrowersInStorage.isEmpty()){
                                    for(BorrowersTable borowTab : borrowersInStorage){
                                        deleteBorrowerFromLocalStorage(borowTab);
                                        Log.d("Delete", "deleted "+borowTab.getBorrowersId()+ " from storage");
                                    }
                                }

                                if(isThereNewData || !borrowersInStorage.isEmpty()) {
                                    loadBorrowersToUI();
                                }
                                removeRefresher();
                            }else{
                                Toast.makeText(getApplicationContext(), "Document is empty", Toast.LENGTH_SHORT).show();
                                removeRefresher();
                            }
                        }else{
                            Log.d("Borrower", "onComplete: "+task.getException().getMessage());
                            removeRefresher();
                        }
                    }
                });
    }

    private void updateTable(DocumentSnapshot doc) {
        BorrowersTable borrowersTable = doc.toObject(BorrowersTable.class);
        borrowersTable.setBorrowersId(doc.getId());

        BorrowersTable currentlySaved = borrowersTableQueries.loadSingleBorrower(doc.getId());
        borrowersTable.setId(currentlySaved.getId());

        if(borrowersTable.getLastUpdatedAt().getTime() != currentlySaved.getLastUpdatedAt().getTime()){

            borrowersTableQueries.updateBorrowerDetails(borrowersTable);
            loadBorrowersToUI();
            Log.d("Borrower", "Borrower Detailed updated");

        }
    }

    private void deleteBorrowerFromLocalStorage(BorrowersTable borowTab) {
        borrowersTableQueries.deleteBorrowersFromStorage(borowTab);
    }

    private void removeRefresher(){
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.destroyDrawingCache();
        swipeRefreshLayout.clearAnimation();
    }

    private void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

    public void search(View view) {
        if( searchEditText.getVisibility()==GONE){
            searchEditText.setVisibility(View.VISIBLE);
            searchEditText.requestFocus();
            KeyboardUtil.showKeyboard(this);
        }else{searchEditText.setVisibility(View.GONE);
            searchEditText.setText("");
        }

    }

    @Override
    public void onBackPressed() {
        if(searchEditText.getVisibility() == View.VISIBLE){
            searchEditText.setVisibility(View.GONE);
            return;
        }

        super.onBackPressed();
    }
}
