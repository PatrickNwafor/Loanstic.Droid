package com.icubed.loansticdroid.activities.LifeGoals;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.SavingsActivity;
import com.icubed.loansticdroid.cloudqueries.SavingsNumberGeneratorQueries;
import com.icubed.loansticdroid.cloudqueries.SavingsQueries;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.SavingsPlanTypeTable;
import com.icubed.loansticdroid.localdatabase.SavingsPlanTypeTableQueries;
import com.icubed.loansticdroid.localdatabase.SavingsTable;
import com.icubed.loansticdroid.models.SavingsNumberGenerator;
import com.icubed.loansticdroid.models.SavingsScheduleGenerator;
import com.icubed.loansticdroid.util.DateUtil;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LifeGoalsSetup6GoalSummary extends AppCompatActivity {

    MenuItem register;

    private static final String TAG =".GoalsSummary";
    private SavingsTable savingsTable;
    private SavingsQueries savingsQueries;
    private SavingsNumberGeneratorQueries savingsNumberGeneratorQueries;
    private TextView goalTypeTextView, goalPurposeTextView, startDateTextView, targetAmountTextView
            , maturityDateTextView, interestStatusTextView, interestValueTextView, depositCycleTextView;

    private SavingsPlanTypeTableQueries savingsPlanTypeTableQueries;
    SavingsScheduleGenerator savingsScheduleGenerator;
    private ProgressBar progressBar;
    private AlertDialog.Builder builder;
    private Index index;
    private BorrowersTable borrower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_goals_setup6_goal_summary);

        Toolbar toolbar = findViewById(R.id.life_goals_setup_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Savings Goal Summary");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        savingsTable = getIntent().getParcelableExtra("savings");
        borrower = getIntent().getParcelableExtra("borrower");
        savingsQueries = new SavingsQueries();
        savingsNumberGeneratorQueries = new SavingsNumberGeneratorQueries();
        savingsPlanTypeTableQueries = new SavingsPlanTypeTableQueries(getApplication());
        savingsScheduleGenerator = new SavingsScheduleGenerator();

        goalTypeTextView = findViewById(R.id.goal_type);
        goalPurposeTextView = findViewById(R.id.goal_purpose);
        startDateTextView = findViewById(R.id.start_date);
        maturityDateTextView = findViewById(R.id.maturity_date);
        targetAmountTextView = findViewById(R.id.target_amount2);
        interestStatusTextView = findViewById(R.id.enabled);
        interestValueTextView = findViewById(R.id.interest_rate);
        depositCycleTextView = findViewById(R.id.repayment_message);
        progressBar = findViewById(R.id.progressBar);

        builder = new AlertDialog.Builder(this);

        fillUIWithSummary();

        //Algolia search initiation
        Client client = new Client("HGQ25JRZ8Y", "d4453ddf82775ee2324c47244b30a7c7");
        index = client.getIndex("Savings");

        Log.d(TAG, "onCreate: "+savingsTable.toString());
    }

    private void fillUIWithSummary() {
        goalTypeTextView.setText(savingsTable.getSavingsPlanName());

        if(savingsTable.getSavingsPlanTypeId() == null){
            if(TextUtils.isEmpty(savingsTable.getSavingsPlanPurpose())) goalPurposeTextView.setText("NIL");
            else goalPurposeTextView.setText(savingsTable.getSavingsPlanPurpose());
        } else {
            SavingsPlanTypeTable savingsPlanTypeTable = savingsPlanTypeTableQueries.loadSingleSavingsPlanType(savingsTable.getSavingsPlanTypeId());
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
            interestStatusTextView.setText("Activated");
            interestValueTextView.setText(String.valueOf(savingsTable.getSavingsInterestRate())+"%");
        }
        else {
            interestStatusTextView.setText("Not Activated");
            interestValueTextView.setText("NIL");
        }

        if(savingsTable.getTargetType() == null || savingsTable.getTargetType().equals(SavingsTable.TARGET_TYPE_FIXED)) depositCycleTextView.setText("NIL");
        else{
            if(savingsTable.getSavingsDurationUnit().equals(DateUtil.PER_DAY)){
                depositCycleTextView.setText(savingsTable.getDepositAmount() + " every "+ savingsTable.getSavingsDuration() +" day(s)");
            }else if(savingsTable.getSavingsDurationUnit().equals(DateUtil.PER_WEEK)){
                depositCycleTextView.setText(savingsTable.getDepositAmount() + " every "+ savingsTable.getSavingsDuration() +" week(s)");
            }else if(savingsTable.getSavingsDurationUnit().equals(DateUtil.PER_MONTH)){
                depositCycleTextView.setText(savingsTable.getDepositAmount() + " every "+ savingsTable.getSavingsDuration() +" month(s)");
            }
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
                startSubmission();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startSubmission() {
        builder.setMessage("Are you sure you want to proceed?")
                .setTitle("Submit")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        progressBar.setVisibility(View.VISIBLE);
                        register.setEnabled(false);
                        validateSavingsNumber();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void validateSavingsNumber() {

        final String savingsNumber = SavingsNumberGenerator.generateLoanNumber();

        savingsNumberGeneratorQueries.validateLoanNumber(savingsNumber)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            if(task.getResult().isEmpty()) registerSavings(savingsNumber);
                            else validateSavingsNumber();

                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                            removeProgress();
                        }
                    }
                });
    }

    private void registerSavings(String savingsNumber) {
        savingsTable.setSavingsNumber(savingsNumber);

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("createdAt", new Date());
        objectMap.put("lastUpdatedAt", new Date());
        objectMap.put("startDate", savingsTable.getStartDate());
        objectMap.put("maturityDate", savingsTable.getMaturityDate());
        objectMap.put("savingsNumber", savingsTable.getSavingsNumber());
        if(savingsTable.getTargetType().equals(SavingsTable.TARGET_TYPE_FIXED)) objectMap.put("amountSaved", savingsTable.getFixedAmount());
        else objectMap.put("amountSaved", savingsTable.getAmountSaved());
        objectMap.put("borrowerId", savingsTable.getBorrowerId());
        objectMap.put("isThereInterest", savingsTable.getIsThereInterest());
        objectMap.put("amountTarget", savingsTable.getAmountTarget());
        objectMap.put("fixedAmount", savingsTable.getFixedAmount());
        objectMap.put("depositAmount", savingsTable.getDepositAmount());
        objectMap.put("targetType", savingsTable.getTargetType());
        objectMap.put("savingsPlanName", savingsTable.getSavingsPlanName());
        objectMap.put("savingsPlanTypeId", savingsTable.getSavingsPlanTypeId());
        objectMap.put("savingsInterestRate", savingsTable.getSavingsInterestRate());
        objectMap.put("savingsDuration", savingsTable.getSavingsDuration());
        objectMap.put("loanOfficerId", savingsTable.getLoanOfficerId());
        objectMap.put("savingsInterestRateUnit", savingsTable.getSavingsInterestRateUnit());
        objectMap.put("savingsPlanPurpose", savingsTable.getSavingsPlanPurpose());
        objectMap.put("savingsDurationUnit", savingsTable.getSavingsDurationUnit());
        objectMap.put("schedulePaid", 0);
        objectMap.put("isSavingsPlanCompleted", false);
        objectMap.put("minimumMaturityDate", new Date());

        savingsQueries.createSavings(objectMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){

                            savingsTable.setSavingsId(task.getResult().getId());
                            generateSavingsCollection();
                            registerForSearch();

                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                            removeProgress();
                        }
                    }
                });
    }

    private void generateSavingsCollection() {
        if(savingsTable.getTargetType() != null) {
            if (savingsTable.getTargetType().equals(SavingsTable.TARGET_TYPE_MONEY)) {
                savingsScheduleGenerator.generateMoneyTargetSchedule(savingsTable);
            } else if (savingsTable.getTargetType().equals(SavingsTable.TARGET_TYPE_TIME)) {
                savingsScheduleGenerator.generateTimeTargetSchedule(savingsTable);
            }
        }
    }

    private void registerForSearch() {
        Map<String, Object> searchMap = new HashMap<>();
        searchMap.put("savingsNumber", savingsTable.getSavingsNumber());
        searchMap.put("name", borrower.getFirstName()+" "+borrower.getLastName());

        JSONObject object = new JSONObject(searchMap);
        index.addObjectAsync(object, savingsTable.getSavingsId(), new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject jsonObject, AlgoliaException e) {
                if(e == null) {
                    Toast.makeText(LifeGoalsSetup6GoalSummary.this, "Savings created", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LifeGoalsSetup6GoalSummary.this, SavingsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Failed to register savings for search", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "requestCompleted: "+e.getMessage());
                }
            }
        });
    }

    private void removeProgress(){
        progressBar.setVisibility(View.GONE);
        register.setEnabled(true);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        register = menu.findItem(R.id.next_to_loan_terms);
        register.setTitle("Submit");
        register.setVisible(true);
        return true;
    }
}
