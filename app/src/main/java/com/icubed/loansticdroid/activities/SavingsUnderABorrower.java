package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.util.KeyboardUtil;

public class SavingsUnderABorrower extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings_under_a_borrower);
        Toolbar toolbar = findViewById(R.id.life_goals_setup_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Borrower's Name");
        getSupportActionBar().setSubtitle("Savings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
                startAnotherActivity(SavingsPickPlan.class);
                return true;

            case R.id.search_loan:
                /*searchSavingsEditText.setVisibility(View.VISIBLE);
                searchSavingsEditText.requestFocus();
                KeyboardUtil.showKeyboard(SavingsActivity.this);*/
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
       /* if(searchSavingsEditText.getVisibility() == View.VISIBLE){
            searchSavingsEditText.setVisibility(View.GONE);
            return;
        }*/

        super.onBackPressed();
    }
}
