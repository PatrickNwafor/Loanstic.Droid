package com.icubed.loansticdroid.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.BorrowerRecyclerAdapter;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.BorrowersTableQueries;
import com.icubed.loansticdroid.models.Borrowers;
import com.icubed.loansticdroid.models.Savings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BorrowerActivity extends AppCompatActivity {

    public RecyclerView borrowerRecyclerView;
    public BorrowerRecyclerAdapter borrowerRecyclerAdapter;
    public ProgressBar borrowerProgressBar;
    private Borrowers borrowers;
    private EditText searchBorrowerEditText;
    public SwipeRefreshLayout swipeRefreshLayout;
    Index index;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrower);

        borrowers = new Borrowers(this);

        //Views
        searchBorrowerEditText = findViewById(R.id.searchEditText);
        borrowerRecyclerView = findViewById(R.id.borrower_list);
        borrowerProgressBar = findViewById(R.id.borrowerProgressBar);

        toolbar = findViewById(R.id.borrower_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Borrowers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //Swipe down refresher initialization
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_dark,
                R.color.colorAccent);
        swipeRefreshListener();

        //Algolia search initiation
        Client client = new Client("HGQ25JRZ8Y", "d4453ddf82775ee2324c47244b30a7c7");
        index = client.getIndex("Borrowers");

        getAllBorrowers();
        searchBorrowerListener();

    }

    //Swipe down refresh lstener
    private void swipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                borrowers.loadAllBorrowersAndCompareToLocal();
            }
        });
    }

    private void getAllBorrowers() {
        if(!borrowers.doesBorrowersTableExistInLocalStorage()){
            borrowers.loadAllBorrowers();
        }else{
            swipeRefreshLayout.setRefreshing(true);
            borrowers.loadBorrowersToUI();
            borrowers.loadAllBorrowersAndCompareToLocal();
        }
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

                        borrowerRecyclerAdapter = new BorrowerRecyclerAdapter(list);
                        borrowerRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        borrowerRecyclerView.setAdapter(borrowerRecyclerAdapter);

                        //This is to check immediately after the search to know if string is empty
                        if(TextUtils.isEmpty(s.toString())){
                            borrowers.loadBorrowersToUI();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else{
            borrowers.loadBorrowersToUI();
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

    public void backButton(View view) {
        finish();
    }

    public void add_borrower(View view) {
        Intent addBorrowerIntent = new Intent(BorrowerActivity.this, SelectAddType.class);
        startActivity(addBorrowerIntent);
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
                showKeyboard();
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

    public void showKeyboard() {
        View focuedView = getCurrentFocus();
        if (focuedView != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.showSoftInput(focuedView, 0);
        }
    }
}
