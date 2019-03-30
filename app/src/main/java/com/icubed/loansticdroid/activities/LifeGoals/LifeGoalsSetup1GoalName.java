package com.icubed.loansticdroid.activities.LifeGoals;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.SavingsPickPlan;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.SavingsPlanTypeTable;
import com.icubed.loansticdroid.localdatabase.SavingsTable;

public class LifeGoalsSetup1GoalName extends AppCompatActivity {

    private TextView goalTitleTextView, goalNameTextView;
    private SavingsTable savingsTable;
    private BorrowersTable borrower;
    private SavingsPlanTypeTable savingsPlanTypeTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_goals_setup1_goal_name);

        Toolbar toolbar = findViewById(R.id.life_goals_setup_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Savings Plan Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        borrower = getIntent().getParcelableExtra("borrower");
        savingsTable = getIntent().getParcelableExtra("savings");
        savingsPlanTypeTable = getIntent().getParcelableExtra("savings_type");

        goalTitleTextView = findViewById(R.id.goal_title);
        goalNameTextView = findViewById(R.id.goal_name);

        goalTitleTextView.setText(savingsTable.getSavingsPlanName());

        if(savingsPlanTypeTable != null){
            goalNameTextView.setEnabled(false);
            goalNameTextView.setText(savingsPlanTypeTable.getSavingsTypeName());
        }
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
                if(savingsTable.getSavingsPlanName().equals(SavingsPickPlan.LIFE_GOALS)) startAnotherActivity(LifeGoalsSetup2TargetAmount.class);
                else if(savingsTable.getSavingsPlanName().equals(SavingsPickPlan.PERIODIC_PLAN)) startAnotherActivity(LifeGoalsSetup3Cycle.class);
                else if(savingsTable.getSavingsPlanName().equals(SavingsPickPlan.FIXED_INVESTMENT)) startAnotherActivity(SavingsGoalFixedAmount.class);
                else if(savingsTable.getSavingsPlanName().equals(SavingsPickPlan.SAVE_AS_YOU_EARN)) startAnotherActivity(LifeGoalsSetup5GoalOptions.class);
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

        savingsTable.setSavingsPlanPurpose(goalNameTextView.getText().toString());

        Intent newActivityIntent = new Intent(this, newActivity);
        newActivityIntent.putExtra("borrower", borrower);
        newActivityIntent.putExtra("savings", savingsTable);
        startActivity(newActivityIntent);
    }
}
