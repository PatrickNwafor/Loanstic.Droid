package com.icubed.loansticdroid.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.icubed.loansticdroid.R;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;

public class LoanEditPage extends AppCompatActivity {
    private Toolbar toolbar;
    public SegmentedButtonGroup sbg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_edit_page);
        toolbar = findViewById(R.id.borrower_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Emasco");
        getSupportActionBar().setSubtitle("loan #100098");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //segmented control
        sbg = findViewById(R.id.segmentedButtonGroup);
        sbg.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition(){
            @Override
            public void onClickedButtonPosition(int position){

            }
        });
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
}
