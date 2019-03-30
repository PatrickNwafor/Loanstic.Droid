package com.icubed.loansticdroid.activities.LifeGoals;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.SavingsPickPlan;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.SavingsPlanTypeTable;
import com.icubed.loansticdroid.localdatabase.SavingsTable;
import com.icubed.loansticdroid.util.DateUtil;
import com.icubed.loansticdroid.util.FormUtil;

public class LifeGoalsSetup3Cycle extends AppCompatActivity {

    private SavingsTable savingsTable;
    private BorrowersTable borrowersTable;

    private RadioButton perDay, perWeek, perMonth;
    private EditText lenghtText;
    private TextView messageTextView;
    private FormUtil formUtil;
    private String selectedCycle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_goals_setup3_cycle);

        Toolbar toolbar = findViewById(R.id.life_goals_setup_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Savings Deposit Cycle");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        savingsTable = getIntent().getParcelableExtra("savings");
        borrowersTable = getIntent().getParcelableExtra("borrower");

        formUtil = new FormUtil();

        perDay = findViewById(R.id.per_day);
        perWeek = findViewById(R.id.per_week);
        perMonth = findViewById(R.id.per_month);
        lenghtText = findViewById(R.id.lenght);
        messageTextView = findViewById(R.id.message);

        selectedCycle = DateUtil.PER_DAY;

        perDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    selectedCycle = DateUtil.PER_DAY;
                    if(!TextUtils.isEmpty(lenghtText.getText().toString())) updateMessage(lenghtText.getText().toString());
                }
            }
        });

        perWeek.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    selectedCycle = DateUtil.PER_WEEK;
                    if(!TextUtils.isEmpty(lenghtText.getText().toString())) updateMessage(lenghtText.getText().toString());
                }
            }
        });

        perMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    selectedCycle = DateUtil.PER_MONTH;
                    if(!TextUtils.isEmpty(lenghtText.getText().toString())) updateMessage(lenghtText.getText().toString());
                }
            }
        });

        lenghtText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateMessage(s.toString());
            }
        });
    }

    private void updateMessage(String s) {
        if(perDay.isChecked()){
            messageTextView.setText("Payment once every "+s+" day(s)");
        }else if(perWeek.isChecked()){
            messageTextView.setText("Payment once every "+s+" week(s)");
        }else if(perMonth.isChecked()){
            messageTextView.setText("Payment once every "+s+" month(s)");
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
                if(formUtil.isSingleFormEmpty(lenghtText)) lenghtText.setError("Please fill a cycle lenght");
                else {
                    lenghtText.setError(null);
                    startAnotherActivity(LifeGoalsSetup4DepositAmount.class);
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

        savingsTable.setSavingsDuration(Integer.parseInt(lenghtText.getText().toString()));
        savingsTable.setSavingsDurationUnit(selectedCycle);

        if(savingsTable.getSavingsPlanName().equals(SavingsPickPlan.PERIODIC_PLAN)) savingsTable.setTargetType(SavingsTable.TARGET_TYPE_TIME);

        Intent newActivityIntent = new Intent(this, newActivity);
        newActivityIntent.putExtra("borrower", borrowersTable);
        newActivityIntent.putExtra("savings", savingsTable);
        startActivity(newActivityIntent);
    }
}
