package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.SavingsPlanTypeTable;
import com.icubed.loansticdroid.localdatabase.SavingsTable;
import com.icubed.loansticdroid.util.DateUtil;
import com.icubed.loansticdroid.util.KeyboardUtil;

public class SavingsDetailsActivity extends AppCompatActivity {

    private TextView targetAmountTextView, fixedAmountTextView, depositAmountTextView
            ,totalAmountTextView, startDateTextView, maturityDateTextView, interestRateTextView
            , goalTypeTextView, goalPurposeTextView, repaymentTextView;

    private SavingsTable savingsTable;
    private BorrowersTable borrowersTable;
    private SavingsPlanTypeTable savingsPlanTypeTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings_details);

        savingsTable = getIntent().getParcelableExtra("savings");
        borrowersTable = getIntent().getParcelableExtra("borrower");
        savingsPlanTypeTable = getIntent().getParcelableExtra("savings_type");

        Toolbar toolbar = findViewById(R.id.pick_plan_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Savings Details");
        getSupportActionBar().setSubtitle(savingsTable.getSavingsNumber());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        targetAmountTextView = findViewById(R.id.target_amount_value);
        fixedAmountTextView = findViewById(R.id.fix_amount_value);
        depositAmountTextView = findViewById(R.id.deposit_amount_value);
        totalAmountTextView = findViewById(R.id.current_total_amount_value);
        startDateTextView = findViewById(R.id.start_date_value);
        maturityDateTextView = findViewById(R.id.maturity_date_value);
        interestRateTextView = findViewById(R.id.interest_value);
        goalTypeTextView = findViewById(R.id.goal_type_value);
        goalPurposeTextView = findViewById(R.id.goal_purpose_value);
        repaymentTextView = findViewById(R.id.payment_value);

        fillUIWithSummary();
    }

    private void fillUIWithSummary() {
        goalTypeTextView.setText(savingsTable.getSavingsPlanName());

        if(savingsTable.getSavingsPlanTypeId() == null){
            if(TextUtils.isEmpty(savingsTable.getSavingsPlanPurpose())) goalPurposeTextView.setText("NIL");
            else goalPurposeTextView.setText(savingsTable.getSavingsPlanPurpose());
        } else {
            goalPurposeTextView.setText(savingsPlanTypeTable.getSavingsTypeName());
        }

        if(savingsTable.getTargetType() == null) targetAmountTextView.setText("NIL");
        else {
            if (savingsTable.getTargetType().equals(SavingsTable.TARGET_TYPE_MONEY)) {
                targetAmountTextView.setText(String.valueOf(savingsTable.getAmountTarget()));
            } else targetAmountTextView.setText("NIL");
        }

        startDateTextView.setText(DateUtil.dateString(savingsTable.getStartDate()));
        maturityDateTextView.setText(DateUtil.dateString(savingsTable.getMaturityDate()));

        if(savingsTable.getIsThereInterest()) {
            interestRateTextView.setText(String.valueOf(savingsTable.getSavingsInterestRate())+"%");
        }
        else {
            interestRateTextView.setText("NIL");
        }

        totalAmountTextView.setText(String.valueOf(savingsTable.getAmountSaved()));

        if(savingsTable.getTargetType() == null) fixedAmountTextView.setText("NIL");
        else {
            if (savingsTable.getTargetType().equals(SavingsTable.TARGET_TYPE_FIXED))
                fixedAmountTextView.setText(String.valueOf(savingsTable.getFixedAmount()));
            else fixedAmountTextView.setText("NIL");
        }

        if(savingsTable.getTargetType() == null) depositAmountTextView.setText("NIL");
        else {
            if (savingsTable.getTargetType().equals(SavingsTable.TARGET_TYPE_FIXED))
                depositAmountTextView.setText("NIL");
            else depositAmountTextView.setText(String.valueOf(savingsTable.getDepositAmount()));
        }

        if(savingsTable.getTargetType() == null) repaymentTextView.setText("NIL");
        else {
            if (savingsTable.getTargetType().equals(SavingsTable.TARGET_TYPE_FIXED))
                repaymentTextView.setText("NIL");
            else {
                if (savingsTable.getSavingsDurationUnit().equals(DateUtil.PER_DAY)) {
                    repaymentTextView.setText(savingsTable.getDepositAmount() + " every " + savingsTable.getSavingsDuration() + " day(s)");
                } else if (savingsTable.getSavingsDurationUnit().equals(DateUtil.PER_WEEK)) {
                    repaymentTextView.setText(savingsTable.getDepositAmount() + " every " + savingsTable.getSavingsDuration() + " week(s)");
                } else if (savingsTable.getSavingsDurationUnit().equals(DateUtil.PER_MONTH)) {
                    repaymentTextView.setText(savingsTable.getDepositAmount() + " every " + savingsTable.getSavingsDuration() + " month(s)");
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.savings_details_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;



            case R.id.view_schedule:
                startAnotherActivity(SavingsSchedule.class);
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
