package com.icubed.loansticdroid.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.icubed.loansticdroid.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class LoanTerms extends AppCompatActivity {
    EditText edittext;
     Spinner spRate;
    Spinner spDuration;

    String selectedRate;
    String selectedDuration;
    private Toolbar toolbar;

    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_terms);

        toolbar = findViewById(R.id.loan_terms_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Loan Terrms");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



//for rate
        spRate = findViewById(R.id.spRate);

        ArrayAdapter<CharSequence> adapterRate;
        String[] RateArr = {"per year", "per month", "per week", "per day"};
        adapterRate = new ArrayAdapter<CharSequence>(this,android.R.layout.simple_spinner_item,RateArr);
        adapterRate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRate.setAdapter(adapterRate);
        selectedRate = spRate.getSelectedItem().toString();

//for duration
        spDuration = findViewById(R.id.spDuration);
        ArrayAdapter<CharSequence> adapterDuration;
        String[] DurationArr = {"year", "month", "week", "day"};
        adapterDuration = new ArrayAdapter<CharSequence>(this,android.R.layout.simple_spinner_item,DurationArr);
        adapterDuration.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDuration.setAdapter(adapterDuration);
        selectedDuration = spDuration.getSelectedItem().toString();


         edittext= (EditText) findViewById(R.id.loan_releae_date);
       final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        edittext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(LoanTerms.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


    }
    private void updateLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edittext.setText(sdf.format(myCalendar.getTime()));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }


}
