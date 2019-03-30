package com.icubed.loansticdroid.activities.LifeGoals;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.SavingsTable;
import com.icubed.loansticdroid.util.FormUtil;

public class LifeGoalsSetup2TargetAmount extends AppCompatActivity {

    private SavingsTable savingsTable;
    private BorrowersTable borrowersTable;
    private EditText targetEditText;
    private FormUtil formUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_goals_setup2_target_amount);

        Toolbar toolbar = findViewById(R.id.life_goals_setup_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Savings Target Amount");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        savingsTable = getIntent().getParcelableExtra("savings");
        borrowersTable = getIntent().getParcelableExtra("borrower");

        formUtil = new FormUtil();

        targetEditText = findViewById(R.id.target_amount);
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
                if(formUtil.isSingleFormEmpty(targetEditText)) targetEditText.setError("Please fill target amount");
                else{
                    targetEditText.setError(null);
                    startAnotherActivity(LifeGoalsSetup3Cycle.class);
                }
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

        savingsTable.setAmountTarget(Double.parseDouble(targetEditText.getText().toString()));
        savingsTable.setTargetType(SavingsTable.TARGET_TYPE_MONEY);

        Intent newActivityIntent = new Intent(this, newActivity);
        newActivityIntent.putExtra("borrower", borrowersTable);
        newActivityIntent.putExtra("savings", savingsTable);
        startActivity(newActivityIntent);
    }
}
