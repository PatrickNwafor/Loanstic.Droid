package com.icubed.loansticdroid.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.GroupLeaderRecyclerAdapter;
import com.icubed.loansticdroid.models.SelectedBorrowerForGroup;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class SelectGroupLeader extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText searchEditText;
    private List<SelectedBorrowerForGroup> groupList;
    private RecyclerView borrowerRecyclerView;
    private GroupLeaderRecyclerAdapter groupLeaderRecyclerAdapter;
    public Button proceedBtn;
    public ImageView lastChecked;
    public SelectedBorrowerForGroup selectedGroupLeader;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group_leader);

        searchEditText = findViewById(R.id.searchEditText);
        borrowerRecyclerView = findViewById(R.id.borrower_list);
        progressBar = findViewById(R.id.borrowerProgressBar);
        proceedBtn = findViewById(R.id.proceed);

        searchEditTextTextChangeListener();

        toolbar = findViewById(R.id.select_group_leader_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select group leader");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        groupList = getIntent().getParcelableArrayListExtra("selectedBorrowers");
        Log.i("Borrowers", groupList.toString());

        groupLeaderRecyclerAdapter = new GroupLeaderRecyclerAdapter(groupList);
        borrowerRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        borrowerRecyclerView.setAdapter(groupLeaderRecyclerAdapter);
        hideProgressBar();
        proceedBtnClickListener();
    }

    private void proceedBtnClickListener() {
        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivityIntent = new Intent(SelectGroupLeader.this, GroupDetailsActivity.class);
                mainActivityIntent.putParcelableArrayListExtra("selectedBorrowers", (ArrayList<? extends Parcelable>) groupList);
                mainActivityIntent.putExtra("groupLeaderId", selectedGroupLeader.getBorrowersId());
                startActivity(mainActivityIntent);
            }
        });
    }

    private void searchEditTextTextChangeListener() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals("")){
                    searchBorrower(s);
                }else {
                    groupLeaderRecyclerAdapter = new GroupLeaderRecyclerAdapter(groupList);
                    borrowerRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    borrowerRecyclerView.setAdapter(groupLeaderRecyclerAdapter);
                }
            }
        });
    }

    private void searchBorrower(Editable s) {
        String searchString = s.toString();
        List<SelectedBorrowerForGroup> borrowersTables = new ArrayList<>();

        for(SelectedBorrowerForGroup bt : groupList) {

            Boolean lastNameCheck = bt.getLastName().toLowerCase().contains(searchString.toLowerCase());
            Boolean firstNameCheck = bt.getFirstName().toLowerCase().contains(searchString.toLowerCase());
            Boolean businessNameCheck = bt.getBusinessName().toLowerCase().contains(searchString.toLowerCase());

            if (lastNameCheck || firstNameCheck || businessNameCheck) {
                borrowersTables.add(bt);
            }
        }

        groupLeaderRecyclerAdapter = new GroupLeaderRecyclerAdapter(borrowersTables);
        borrowerRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        borrowerRecyclerView.setAdapter(groupLeaderRecyclerAdapter);

        if(s.toString().equals("")){
            groupLeaderRecyclerAdapter = new GroupLeaderRecyclerAdapter(groupList);
            borrowerRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            borrowerRecyclerView.setAdapter(groupLeaderRecyclerAdapter);
        }
    }

    private void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

    public void collectionLocation(View view) {
        Intent mainActivityIntent = new Intent(SelectGroupLeader.this, SelectCollectionLocation.class);
        startActivity(mainActivityIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.new_group_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_search:
                searchEditText.setVisibility(View.VISIBLE);
                searchEditText.requestFocus();
                showKeyboard();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void search(View view) {
        if( searchEditText.getVisibility()==GONE){
            searchEditText.setVisibility(View.VISIBLE);
            searchEditText.requestFocus();
            showKeyboard();
        }else{searchEditText.setVisibility(View.GONE);
            searchEditText.setText("");
        }

    }

    public void showKeyboard() {
        View focuedView = getCurrentFocus();
        if (focuedView != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.showSoftInput(focuedView, 0);
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
