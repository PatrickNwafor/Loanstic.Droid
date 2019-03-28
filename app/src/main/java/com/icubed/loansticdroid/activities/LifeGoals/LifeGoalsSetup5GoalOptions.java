package com.icubed.loansticdroid.activities.LifeGoals;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.icubed.loansticdroid.R;

public class LifeGoalsSetup5GoalOptions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_goals_setup5_goal_options);

        Toolbar toolbar = findViewById(R.id.life_goals_setup_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Savings Goal Option");
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
                finish();
                return true;

            case R.id.next_to_loan_terms:
                startAnotherActivity(LifeGoalsSetup6GoalSummary.class);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem register = menu.findItem(R.id.next_to_loan_terms);
        register.setVisible(true);
        return true;
    }

    private void startAnotherActivity(Class newActivity){
        Intent newActivityIntent = new Intent(this, newActivity);
        //newActivityIntent.putExtra("savings_type", selectedSavingsPlanTypeTable);
        //newActivityIntent.putExtra("borrower", borrower);
        //newActivityIntent.putExtra("savings", savingsTable);
        startActivity(newActivityIntent);
    }
}
