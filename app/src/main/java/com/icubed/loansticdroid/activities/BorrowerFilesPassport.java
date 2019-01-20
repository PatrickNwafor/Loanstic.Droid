package com.icubed.loansticdroid.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.icubed.loansticdroid.R;

public class BorrowerFilesPassport extends AppCompatActivity {
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrower_files_passport);
        toolbar = findViewById(R.id.ID_document_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Passport");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
