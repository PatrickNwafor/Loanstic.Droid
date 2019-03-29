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

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.util.DateUtil;

import java.util.Calendar;

public class LifeGoalsSetup5GoalOptions extends AppCompatActivity {
    final Calendar myCalendar = Calendar.getInstance();
    EditText startDate,maturityDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_goals_setup5_goal_options);

        Toolbar toolbar = findViewById(R.id.life_goals_setup_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Savings Goal Option");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);




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
