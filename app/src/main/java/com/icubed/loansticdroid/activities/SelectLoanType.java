package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.icubed.loansticdroid.R;

public class SelectLoanType extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_loan_type);

        toolbar = findViewById(R.id.select_loan_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select loan type");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.select_loan_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.next_to_loan_terms:
                startAnotherActivity(LoanTerms.class);
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
        super.onBackPressed();
    }
}
