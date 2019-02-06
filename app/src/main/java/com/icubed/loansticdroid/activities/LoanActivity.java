package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.UploadTask;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.cloudqueries.LoanTypeQueries;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;

public class LoanActivity extends AppCompatActivity {
    public ProgressBar loanProgressBar;
    private LoanTypeQueries loanTypeQueries;
    private EditText searchLoanEditText;
    public SegmentedButtonGroup sbg;
    Index index;
    Index groupIndex;
    private Toolbar toolbar;
    private int searchPosition = 0;



    public boolean isSearch = false;
    public boolean isGroupSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan);

        searchLoanEditText = findViewById(R.id.searchEditText);

        loanProgressBar = findViewById(R.id.borrowerProgressBar);
        loanTypeQueries = new LoanTypeQueries();

        toolbar = findViewById(R.id.loan_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Loans");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //segmented control
        sbg = findViewById(R.id.segmentedButtonGroup);
        sbg.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition(){
            @Override
            public void onClickedButtonPosition(int position){
                if(position==0) {
                    searchPosition = 0;
                    // startFragment(singleBorrowerFragment, "single");
                }
                else if (position==1) {
                    searchPosition = 1;
                    // startFragment(groupBorrowerFragment, "group");
                }
                else if (position==1) {
                    searchPosition = 2;
                    // startFragment(groupBorrowerFragment, "group");
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startAnotherActivity(Class newActivity){
        Intent newActivityIntent = new Intent(this, newActivity);
        startActivity(newActivityIntent);
    }

    @Override
    public void onBackPressed() {
        /*if(searchBorrowerEditText.getVisibility() == View.VISIBLE){
            searchBorrowerEditText.setVisibility(View.GONE);
            return;
        }*/

        super.onBackPressed();
    }

}
