package com.icubed.loansticdroid.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.BorrowerRecyclerAdapter;
import com.icubed.loansticdroid.adapters.GroupLoanRecyclerAdapter;
import com.icubed.loansticdroid.adapters.GroupRecyclerAdapter;
import com.icubed.loansticdroid.adapters.SingleLoanRecyclerAdapter;
import com.icubed.loansticdroid.fragments.BorrowersFragment.GroupBorrowerFragment;
import com.icubed.loansticdroid.fragments.BorrowersFragment.SingleBorrowerFragment;
import com.icubed.loansticdroid.fragments.SelectLoanUser.GroupLoanFragment;
import com.icubed.loansticdroid.fragments.SelectLoanUser.SingleLoanFragment;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.util.AndroidUtils;
import com.icubed.loansticdroid.util.EditTextExtension.CustomEditText;
import com.icubed.loansticdroid.util.EditTextExtension.DrawableClickListener;
import com.icubed.loansticdroid.util.KeyboardUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;

public class NewLoanWizard extends AppCompatActivity {

    private Toolbar toolbar;

    public ProgressBar borrowerProgressBar;
    private CustomEditText searchBorrowerEditText;
    public SegmentedButtonGroup sbg;
    Index index;
    Index groupIndex;
    private int searchPosition = 0;
    public ImageView lastChecked = null;
    public BorrowersTable selectedBorrower = null;
    public GroupBorrowerTable selectedGroup = null;

    private SingleLoanFragment singleLoanFragment;
    private GroupLoanFragment groupLoanFragment;

    public boolean isSearch = false;
    public boolean isGroupSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_loan_wizard);

        toolbar = findViewById(R.id.select_loan_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select Loanee");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        groupLoanFragment = new GroupLoanFragment();
        singleLoanFragment = new SingleLoanFragment();

        startFragment(singleLoanFragment, "single");

        //Views
        searchBorrowerEditText = findViewById(R.id.searchEditText);
        borrowerProgressBar = findViewById(R.id.borrowerProgressBar);

        //Algolia search initiation
        Client client = new Client("HGQ25JRZ8Y", "d4453ddf82775ee2324c47244b30a7c7");
        index = client.getIndex("Borrowers");
        groupIndex = client.getIndex("BORROWER_GROUP");

        searchBorrowerListener();
        searchDrawableButtonListener();

        //segmented control
        sbg = findViewById(R.id.segmentedButtonGroup);
        sbg.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition() {
            @Override
            public void onClickedButtonPosition(int position) {
                if (position == 0) {
                    searchPosition = 0;
                    startFragment(singleLoanFragment, "single");
                } else if (position == 1) {
                    searchPosition = 1;
                    startFragment(groupLoanFragment, "group");
                }
            }
        });
    }

    private void searchDrawableButtonListener() {
        searchBorrowerEditText.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case LEFT:
                        KeyboardUtil.hideKeyboard(NewLoanWizard.this);
                        searchBorrowerEditText.setVisibility(View.GONE);
                        break;

                    case RIGHT:
                        searchBorrowerEditText.setText("");
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void startAnotherActivity(Class newActivity) {
        Intent newActivityIntent = new Intent(this, newActivity);
        newActivityIntent.putExtra("borrower", selectedBorrower);
        newActivityIntent.putExtra("group", selectedGroup);
        startActivity(newActivityIntent);
    }

    /************Instantiate fragment transactions**********/
    public void startFragment(Fragment fragment, String fragmentTag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, fragment, fragmentTag);
        transaction.commit();
    }

    private void searchBorrowerListener() {
        searchBorrowerEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (searchPosition == 0) {
                    searchBorrowers(s);
                } else if (searchPosition == 1) {
                    searchGroup(s);
                }

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

                        isGroupSearch = true;
                        groupLoanFragment.groupLoanRecyclerAdapter = new GroupLoanRecyclerAdapter(list);
                        groupLoanFragment.groupRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        groupLoanFragment.groupRecyclerView.setAdapter(groupLoanFragment.groupLoanRecyclerAdapter);

                        //This is to check immediately after the search to know if string is empty
                        if (TextUtils.isEmpty(s.toString())) {
                            groupLoanFragment.groups.loadGroupsToUI();
                        }

                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            groupLoanFragment.groups.loadGroupsToUI();
        }
    }


    /**
     * search for borrowers
     *
     * @param s
     */
    private void searchBorrowers(final Editable s) {
        if (!TextUtils.isEmpty(s.toString())) {
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
                        singleLoanFragment.singleLoanRecyclerAdapter = new SingleLoanRecyclerAdapter(list);
                        singleLoanFragment.borrowerRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        singleLoanFragment.borrowerRecyclerView.setAdapter(singleLoanFragment.singleLoanRecyclerAdapter);

                        //This is to check immediately after the search to know if string is empty
                        if (TextUtils.isEmpty(s.toString())) {
                            singleLoanFragment.singleLoan.loadBorrowersToUI();
                        }

                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            singleLoanFragment.singleLoan.loadBorrowersToUI();
        }

    }

    public void searchAbleAttributes() {
        JSONObject settings = null;
        try {
            settings = new JSONObject("{\"searchableAttributes\": [\"lastName\", \"firstName\", \"businessName\"]}");
            index.setSettingsAsync(settings, false, null, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.loanee_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem register = menu.findItem(R.id.next_to_loan_terms);

        if(selectedBorrower != null || selectedGroup !=  null || lastChecked != null){
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
                onBackPressed();
                return true;

            case R.id.action_search:
                searchBorrowerEditText.setVisibility(View.VISIBLE);
                searchBorrowerEditText.requestFocus();
                KeyboardUtil.showKeyboard(this);
                return true;

            case R.id.next_to_loan_terms:
                startAnotherActivity(SelectLoanType.class);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (searchBorrowerEditText.getVisibility() == View.VISIBLE) {
            searchBorrowerEditText.setVisibility(View.GONE);
            return;
        }

        super.onBackPressed();
    }
}
