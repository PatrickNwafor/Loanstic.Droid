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
import com.icubed.loansticdroid.util.AndroidUtils;
import com.icubed.loansticdroid.util.EditTextExtension.CustomEditText;
import com.icubed.loansticdroid.util.EditTextExtension.DrawableClickListener;
import com.icubed.loansticdroid.util.KeyboardUtil;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class SelectGroupLeader extends AppCompatActivity {

    private Toolbar toolbar;
    private CustomEditText searchEditText;
    private List<SelectedBorrowerForGroup> groupList;
    private RecyclerView borrowerRecyclerView;
    private GroupLeaderRecyclerAdapter groupLeaderRecyclerAdapter;
    public ImageView lastChecked;
    public SelectedBorrowerForGroup selectedGroupLeader;
    private ProgressBar progressBar;
    public boolean isNextVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group_leader);

        searchEditText = findViewById(R.id.searchEditText);
        borrowerRecyclerView = findViewById(R.id.borrower_list);
        progressBar = findViewById(R.id.borrowerProgressBar);

        searchEditTextTextChangeListener();
        searchDrawableButtonListener();

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
    }

    private void searchDrawableButtonListener() {
        searchEditText.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case LEFT:
                        KeyboardUtil.hideKeyboard(SelectGroupLeader.this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.loanee_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem register = menu.findItem(R.id.next_to_loan_terms);

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
                Intent mainActivityIntent = new Intent(SelectGroupLeader.this, GroupDetailsActivity.class);
                mainActivityIntent.putParcelableArrayListExtra("selectedBorrowers", (ArrayList<? extends Parcelable>) groupList);
                mainActivityIntent.putExtra("groupLeaderId", selectedGroupLeader.getBorrowersId());
                startActivity(mainActivityIntent);

            default:
                return super.onOptionsItemSelected(item);
        }
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
