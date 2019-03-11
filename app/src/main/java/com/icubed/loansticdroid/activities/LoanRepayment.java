package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.loanee_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.next_to_loan_terms);
        menuItem.setTitle("Finish");
        menuItem.setVisible(true);
        menu.findItem(R.id.search_loan).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.next_to_loan_terms:
               // Intent intent = new Intent(this, BorrowerActivity.class);
               // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               // startActivity(intent);

            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
