package com.icubed.loansticdroid.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.util.KeyboardUtil;

public class AllBorrowerLoan extends AppCompatActivity {
    private Toolbar toolbar;

    Button ProfileButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_borrower_loan);

        toolbar = findViewById(R.id.borrower_toolbar);

        ProfileButton = findViewById(R.id.profile_button);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Loans");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {


            case android.R.id.home:
                onBackPressed();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
    }


    public void viewProfile(View view) {
        Intent i = new Intent(AllBorrowerLoan.this, BorrowerDetailsSingle.class);


        Button sharedView = ProfileButton;
        String transitionName = getString(R.string.blue_name);

        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(AllBorrowerLoan.this, sharedView, transitionName);
        startActivity(i, transitionActivityOptions.toBundle());
    }
}
