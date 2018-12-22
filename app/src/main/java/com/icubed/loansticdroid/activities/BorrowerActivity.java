package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.icubed.loansticdroid.R;

public class BorrowerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrower);
    }

    public void backButton(View view) {
        finish();
    }

    public void add_borrower(View view) {
        Intent addBorrowerIntent = new Intent(BorrowerActivity.this, AddSingleBorrower.class);
        startActivity(addBorrowerIntent);
    }
}
