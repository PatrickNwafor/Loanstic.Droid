package com.icubed.loansticdroid.activities;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.BorrowerRecyclerAdapter;
import com.icubed.loansticdroid.adapters.GroupBorrowerListRecyclerAdapter;
import com.icubed.loansticdroid.adapters.SelectedBorrowerForGroupRecyclerAdapter;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.BorrowersTableQueries;
import com.icubed.loansticdroid.models.Borrowers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class AddGroupBorrower extends AppCompatActivity {

    private EditText searchEditText;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView borrowerRecyclerView, selectedBorrowerRecyclerView;
    private ProgressBar progressBar;

    public List<BorrowersTable> selectedBorrowerList;
    private GroupBorrowerListRecyclerAdapter groupBorrowerListRecyclerAdapter;
    public SelectedBorrowerForGroupRecyclerAdapter selectedBorrowerForGroupRecyclerAdapter;
    private BorrowersTableQueries borrowersTableQueries;
    private BorrowersQueries borrowersQueries;
    private Index index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_borrower);

        borrowersTableQueries = new BorrowersTableQueries(getApplication());
        borrowersQueries = new BorrowersQueries(this);

        searchEditText = findViewById(R.id.searchEditText);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        borrowerRecyclerView = findViewById(R.id.borrower_list);
        selectedBorrowerRecyclerView = findViewById(R.id.busiVerifRecyclerView);
        progressBar = findViewById(R.id.borrowerProgressBar);

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

        searchBorrowerListener();
        getAllBorrowers();
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

                        groupBorrowerListRecyclerAdapter = new GroupBorrowerListRecyclerAdapter(list);
                        borrowerRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        borrowerRecyclerView.setAdapter(groupBorrowerListRecyclerAdapter);

                        //This is to check immediately after the search to know if string is empty
                        if(TextUtils.isEmpty(s.toString())){
                            loadBorrowersToUI();
                        }

                    } catch (JSONException e) {
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
                loadAllBorrowersAndCompareToLocal();
            }
        });
    }

    private void getAllBorrowers() {
        if(!doesBorrowersTableExistInLocalStorage()){
            loadAllBorrowers();
        }else{
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
        List<BorrowersTable> borrowersTables = borrowersTableQueries.loadAllBorrowersOrderByLastName();

        //For vertical recycler Adapter
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

    public void backButton(View view) {
        finish();
    }

    public void search(View view) {

    }
}
