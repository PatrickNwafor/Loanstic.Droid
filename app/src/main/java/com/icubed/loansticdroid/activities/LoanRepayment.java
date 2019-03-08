package com.icubed.loansticdroid.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.icubed.loansticdroid.R;

public class LoanRepayment extends AppCompatActivity {
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_repayment);
        toolbar = findViewById(R.id.repayment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Loan Repayment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
