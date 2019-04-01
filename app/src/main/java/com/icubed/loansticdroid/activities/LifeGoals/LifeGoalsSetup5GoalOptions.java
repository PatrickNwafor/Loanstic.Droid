package com.icubed.loansticdroid.activities.LifeGoals;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.SavingsTable;
import com.icubed.loansticdroid.models.Savings;
import com.icubed.loansticdroid.util.DateUtil;
import com.icubed.loansticdroid.util.FormUtil;

import java.util.Calendar;
import java.util.Date;

public class LifeGoalsSetup5GoalOptions extends AppCompatActivity {

    private SavingsTable savingsTable;
    private BorrowersTable borrowersTable;

    final Calendar myCalendar = Calendar.getInstance();
    EditText startDate,maturityDate;
    private Switch interestRateToggle;
    private FormUtil formUtil;
    private Date start, maturity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_goals_setup5_goal_options);

        Toolbar toolbar = findViewById(R.id.life_goals_setup_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Savings Goal Option");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        savingsTable = getIntent().getParcelableExtra("savings");
        borrowersTable = getIntent().getParcelableExtra("borrower");

        formUtil = new FormUtil();

        interestRateToggle = findViewById(R.id.interestRateToggle);
        startDate= findViewById(R.id.start_date);
        maturityDate= findViewById(R.id.maturity_date);

        final DatePickerDialog.OnDateSetListener startdate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                startDate.setError(null);
                startDate.setText(DateUtil.dateString(myCalendar.getTime()));
                start = myCalendar.getTime();
            }

        };

        startDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(LifeGoalsSetup5GoalOptions.this, startdate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final DatePickerDialog.OnDateSetListener maturitydate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                maturityDate.setError(null);
                maturityDate.setText(DateUtil.dateString(myCalendar.getTime()));
                maturity = myCalendar.getTime();
            }

        };
        maturityDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(LifeGoalsSetup5GoalOptions.this, maturitydate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        if(savingsTable.getTargetType() == null) interestRateToggle.setEnabled(false);
        else if(!savingsTable.getTargetType().equals(SavingsTable.TARGET_TYPE_MONEY)) interestRateToggle.setEnabled(false);
        else if(!savingsTable.getTargetType().equals(SavingsTable.TARGET_TYPE_TIME)) interestRateToggle.setEnabled(false);
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

        EditText[] editTexts = new EditText[]{startDate, maturityDate};

        boolean isAnyFormEmpty = isAnyFormEmpty(editTexts);
        if (isAnyFormEmpty) {
            return;
        }

        savingsTable.setStartDate(start);
        savingsTable.setMaturityDate(maturity);

        if(interestRateToggle.isChecked()) {
            savingsTable.setSavingsInterestRate(15.0);
            savingsTable.setSavingsInterestRateUnit(DateUtil.PER_YEAR);
            savingsTable.setIsThereInterest(true);
        } else savingsTable.setIsThereInterest(false);

        Intent newActivityIntent = new Intent(this, newActivity);
        newActivityIntent.putExtra("borrower", borrowersTable);
        newActivityIntent.putExtra("savings", savingsTable);
        startActivity(newActivityIntent);
    }

    public Boolean isAnyFormEmpty(EditText[] forms){
        Boolean isFormEmpty = false;
        boolean[] listOfFormsEmpty = formUtil.isListOfFormsEmpty(forms);

        for(int i = 0; i < forms.length; i++){
            if(listOfFormsEmpty[i]){
                forms[i].setError("Field is required");

                if(!isFormEmpty) {
                    forms[i].requestFocus();
                }

                isFormEmpty = true;
            }else{
                forms[i].setError(null);
            }
        }

        return isFormEmpty;
    }
}
