package com.icubed.loansticdroid.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.localdatabase.LoansTable;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;

public class LoanEditPage extends AppCompatActivity {
    private Toolbar toolbar;
    public SegmentedButtonGroup sbg;

    private BorrowersTable borrower;
    private GroupBorrowerTable group;
    private LoansTable loan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_edit_page);

        borrower = getIntent().getParcelableExtra("borrower");
        group = getIntent().getParcelableExtra("group");
        loan = getIntent().getParcelableExtra("loan");

        toolbar = findViewById(R.id.borrower_toolbar);
        setSupportActionBar(toolbar);

        if(borrower != null) getSupportActionBar().setTitle(borrower.getFirstName()+" "+borrower.getLastName());
        else getSupportActionBar().setTitle(group.getGroupName());

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
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
