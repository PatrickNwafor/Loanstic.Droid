package com.icubed.loansticdroid.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.UploadTask;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.LoanRecyclerAdapter;
import com.icubed.loansticdroid.util.AndroidUtils;
import com.ramotion.foldingcell.FoldingCell;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;

public class LoanActivity extends AppCompatActivity {
    public ProgressBar loanProgressBar;
    private EditText searchLoanEditText;
    Index index;
    private Toolbar toolbar;



    public RecyclerView loanRecyclerView;
    public LoanRecyclerAdapter loanRecyclerAdapter;
    public SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan);

        searchLoanEditText = findViewById(R.id.searchEditText);
        loanProgressBar = findViewById(R.id.borrowerProgressBar);
        loanRecyclerView = findViewById(R.id.loan_list);

        //Swipe down refresher initialization
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_dark,
                R.color.colorAccent);
        swipeRefreshListener();

        toolbar = findViewById(R.id.loan_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Loans");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

// get our folding cell
        final FoldingCell fc = (FoldingCell) findViewById(R.id.folding_cell);

        // attach click listener to folding cell
        fc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fc.toggle(false);
            }
        });


    }

    //Swipe down refresh lstener
    private void swipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(AndroidUtils.isMobileDataEnabled(getApplicationContext())) {
                    swipeRefreshLayout.setRefreshing(true);
                }else {
                    Toast.makeText(getApplicationContext(), "Request failed, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.loans_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.add_loan:
                startAnotherActivity(NewLoanWizard.class);
                return true;

            case R.id.search_loan:
                searchLoanEditText.setVisibility(View.VISIBLE);
                searchLoanEditText.requestFocus();
                showKeyboard();
                return true;

            default:
                return super.onOptionsItemSelected(item);
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

    private void startAnotherActivity(Class newActivity){
        Intent newActivityIntent = new Intent(this, newActivity);
        startActivity(newActivityIntent);
    }

    @Override
    public void onBackPressed() {
        if(searchLoanEditText.getVisibility() == View.VISIBLE){
            searchLoanEditText.setVisibility(View.GONE);
            return;
        }

        super.onBackPressed();
    }

}
