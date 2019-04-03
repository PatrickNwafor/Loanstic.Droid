package com.icubed.loansticdroid.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.cloudqueries.SavingsQueries;
import com.icubed.loansticdroid.cloudqueries.TransactionQueries;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.PaymentModeTable;
import com.icubed.loansticdroid.localdatabase.PaymentModeTableQueries;
import com.icubed.loansticdroid.localdatabase.SavingsTableQueries;
import com.icubed.loansticdroid.localdatabase.TransactionTable;
import com.icubed.loansticdroid.localdatabase.TransactionTableQueries;
import com.icubed.loansticdroid.localdatabase.SavingsPlanTypeTable;
import com.icubed.loansticdroid.localdatabase.SavingsTable;
import com.icubed.loansticdroid.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class SavingsDetailsActivity extends AppCompatActivity {

    private TextView targetAmountTextView, fixedAmountTextView, depositAmountTextView
            ,totalAmountTextView, startDateTextView, maturityDateTextView, interestRateTextView
            , goalTypeTextView, goalPurposeTextView, repaymentTextView;

    private SavingsTable savingsTable;
    private BorrowersTable borrowersTable;
    private SavingsPlanTypeTable savingsPlanTypeTable;

    private TableLayout tableLayout;
    private TransactionQueries transactionQueries;
    private TransactionTableQueries transactionTableQueries;
    private boolean isGrey = true;
    private ProgressBar scheduleProgressBar;
    int count = 1;
    private LinearLayout emptyLayout, trans;
    private Button withdraw, deposit;
    private SavingsTableQueries savingsTableQueries;
    private SavingsQueries savingsQueries;
    boolean shouldExecuteOnResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings_details);

        shouldExecuteOnResume = false;

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

        tableLayout = findViewById(R.id.table);
        scheduleProgressBar = findViewById(R.id.progressBar);
        emptyLayout = findViewById(R.id.search_empty_layout);
        deposit = findViewById(R.id.deposit);
        withdraw = findViewById(R.id.withdraw);
        trans = findViewById(R.id.trans);

        transactionQueries = new TransactionQueries();
        transactionTableQueries = new TransactionTableQueries(getApplication());
        savingsTableQueries = new SavingsTableQueries(getApplication());
        savingsQueries = new SavingsQueries();

        if(savingsTable.getTargetType() == null) {
            deposit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), SavingsTransactionDepositPayment.class);
                    intent.putExtra("savings", savingsTable);
                    startActivity(intent);
                }
            });
        }else if(savingsTable.getTargetType().equals(SavingsTable.TARGET_TYPE_FIXED)) {
            deposit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "No extra deposit can be made for a fixed plan", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            deposit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Please view plan deposit schedule to make a deposit", Toast.LENGTH_SHORT).show();
                }
            });
        }

        createTableHeader();
        List<TransactionTable> collectionTableList = transactionTableQueries.loadAllTransactions(savingsTable.getSavingsId());

        if(collectionTableList == null || collectionTableList.isEmpty()){
            getTransactionFromCloud();
        }else{
            loadTransactionsToUI(collectionTableList);
            getNewTransactionAndCompareToCloud(collectionTableList);
        }

        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(savingsTable.getTargetType() == null){
                    withdrawTask();
                }else if(savingsTable.getTargetType().equals(SavingsTable.TARGET_TYPE_FIXED)){
                    Date now = new Date();
                    if(now.after(savingsTable.getMaturityDate())){
                        withdrawTask();
                    } else
                        Toast.makeText(SavingsDetailsActivity.this, "Savings has not matured yet for withdrawal", Toast.LENGTH_SHORT).show();
                }else if(savingsTable.getTargetType().equals(SavingsTable.TARGET_TYPE_TIME)){
                    if(savingsTable.getAmountSaved() == savingsTable.getTotalExpectedPeriodicAmount()){
                        withdrawTask();
                    } else
                        Toast.makeText(SavingsDetailsActivity.this, "Savings has not matured yet for withdrawal", Toast.LENGTH_SHORT).show();
                }else if(savingsTable.getTargetType().equals(SavingsTable.TARGET_TYPE_MONEY)){
                    if(savingsTable.getAmountSaved() == savingsTable.getAmountTarget()){
                        withdrawTask();
                    } else
                        Toast.makeText(SavingsDetailsActivity.this, "Savings has not matured yet for withdrawal", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fillUIWithSummary();
        loadAllSavingssAndCompareToLocal();
    }

    private void withdrawTask(){
        if(savingsTable.getAmountSaved() > 0) {
            Intent intent = new Intent(getApplicationContext(), SavingsTransactionsWithdrawalPayment.class);
            intent.putExtra("savings", savingsTable);
            startActivity(intent);
        }else{
            Toast.makeText(SavingsDetailsActivity.this, "You do not have money saved in your account to withdraw", Toast.LENGTH_SHORT).show();
        }
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

    private void getNewTransactionAndCompareToCloud(final List<TransactionTable> collectionTableList) {
        transactionQueries.retrieveAllSavingsTransactionsForSavings(savingsTable.getSavingsId())
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){

                                for(DocumentSnapshot doc : task.getResult()){
                                    TransactionTable collectionTable = doc.toObject(TransactionTable.class);
                                    collectionTable.setTransactionId(doc.getId());

                                    boolean newData = true;
                                    Log.d(TAG, "onComplete: "+collectionTable.toString());
                                    for (TransactionTable table : collectionTableList) {
                                        if(table.getTransactionId().equals(collectionTable.getTransactionId())){
                                            newData = false;
                                            break;
                                        }
                                    }

                                    if(newData) saveCollectionToLocalStorage(collectionTable);
                                    updateCollection(collectionTable);
                                }

                                loadAllCollections();

                                scheduleProgressBar.setVisibility(View.GONE);
                            }else {
                                scheduleProgressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "DueCollection is empty", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            scheduleProgressBar.setVisibility(View.GONE);
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void updateCollection(TransactionTable collectionTable) {

        TransactionTable currentlySaved = transactionTableQueries.loadSingleTransaction(collectionTable.getTransactionId());
        collectionTable.setId(currentlySaved.getId());

        if(collectionTable.getLastUpdatedAt().getTime() != currentlySaved.getLastUpdatedAt().getTime()){

            transactionTableQueries.updateTransaction(collectionTable);
            Log.d("Savings Collection", "Detailed updated");

        }
    }

    private void loadAllCollections() {
        count = 1;
        tableLayout.removeAllViews();
        createTableHeader();

        List<TransactionTable> collectionTableList = transactionTableQueries.loadAllTransactions(savingsTable.getSavingsId());

        for (TransactionTable collectionTable : collectionTableList) {
            createTableBody(collectionTable);
        }

    }

    private void loadTransactionsToUI(List<TransactionTable> collectionTableList) {
        for (TransactionTable collectionTable : collectionTableList) {
            createTableBody(collectionTable);
        }

        scheduleProgressBar.setVisibility(View.GONE);
    }

    private void getTransactionFromCloud() {
        transactionQueries.retrieveAllSavingsTransactionsForSavings(savingsTable.getSavingsId())
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){
                                for(DocumentSnapshot doc : task.getResult()){
                                    TransactionTable collectionTable = doc.toObject(TransactionTable.class);
                                    collectionTable.setTransactionId(doc.getId());

                                    Log.d(TAG, "onComplete: "+collectionTable.toString());
                                    saveCollectionToLocalStorage(collectionTable);
                                    createTableBody(collectionTable);
                                }

                                scheduleProgressBar.setVisibility(View.GONE);
                            }else {
                                tableLayout.removeAllViews();
                                emptyLayout.setVisibility(View.VISIBLE);
                                scheduleProgressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "No transaction made yet", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            scheduleProgressBar.setVisibility(View.GONE);
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void saveCollectionToLocalStorage(TransactionTable collectionTable) {
        TransactionTable collectionTable1 = transactionTableQueries.loadSingleTransaction(collectionTable.getTransactionId());
        if(collectionTable1 == null) transactionTableQueries.insertTransactionToStorage(collectionTable);
    }

    public void loadAllSavingssAndCompareToLocal() {

        savingsTable = savingsTableQueries.loadSingleSavings(savingsTable.getSavingsId());

        savingsQueries.retrieveSingleSavings(savingsTable.getSavingsId())
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().exists()){
                                
                                SavingsTable savings = task.getResult().toObject(SavingsTable.class);
                                savings.setSavingsId(task.getResult().getId());
                                
                                if(savings.getLastUpdatedAt().getTime() != savingsTable.getLastUpdatedAt().getTime()){
                                    savings.setId(savingsTable.getId());
                                    savingsTableQueries.updateSavingsDetails(savings);
                                    savingsTable = savings;
                                    fillUIWithSummary();
                                }
                                
                            }else{
                                Toast.makeText(getApplicationContext(), "Document is empty", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Log.d("Savings", "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void createTableBody(final TransactionTable collectionTable) {
        TableRow row = new TableRow(getApplicationContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);

        //row color alternates between grey and white
        if(isGrey) {
            row.setBackgroundColor(Color.GRAY);
            isGrey = false;
        }
        else {
            row.setBackgroundColor(Color.WHITE);
            isGrey = true;
        }

        TextView transactionNumberHeader, transactionDateHeader, transactionTypeHeader, transactionAmountHeader, paymentModeHeader;

        transactionNumberHeader = new TextView(getApplicationContext());
        transactionDateHeader = new TextView(getApplicationContext());
        transactionTypeHeader = new TextView(getApplicationContext());
        transactionAmountHeader = new TextView(getApplicationContext());
        paymentModeHeader = new TextView(getApplicationContext());

        transactionNumberHeader.setText(String.valueOf(count));
        transactionNumberHeader.setGravity(Gravity.CENTER);
        transactionNumberHeader.setPadding(10,10,10,10);
        transactionDateHeader.setText(DateUtil.dateString(collectionTable.getTransactionTime()));
        transactionDateHeader.setGravity(Gravity.CENTER);
        transactionDateHeader.setPadding(10,10,10,10);
        transactionTypeHeader.setText(String.valueOf(collectionTable.getTransactionType()));
        transactionTypeHeader.setGravity(Gravity.CENTER);
        transactionTypeHeader.setPadding(10,10,10,10);

        transactionAmountHeader.setText(String.valueOf(collectionTable.getTransactionAmount()));
        transactionAmountHeader.setGravity(Gravity.CENTER);
        transactionAmountHeader.setPadding(10,10,10,10);

        if(collectionTable.getPaymentModeId() == null) paymentModeHeader.setText(String.valueOf(collectionTable.getPaymentMode()));
        else{
            PaymentModeTableQueries paymentModeTableQueries = new PaymentModeTableQueries(getApplication());
            PaymentModeTable paymentModeTable = paymentModeTableQueries.loadSinglePaymentMode(collectionTable.getPaymentModeId());
            paymentModeHeader.setText(paymentModeTable.getPaymentMode());
        }
        paymentModeHeader.setGravity(Gravity.CENTER);
        paymentModeHeader.setPadding(10,10,10,10);

        row.addView(transactionNumberHeader);
        row.addView(transactionDateHeader);
        row.addView(transactionTypeHeader);
        row.addView(transactionAmountHeader);
        row.addView(paymentModeHeader);

        tableLayout.addView(row);
        count++;
        addHorizontalSeparator(tableLayout);
    }

    private void createTableHeader(){
        TableRow row = new TableRow(getApplicationContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        TextView transactionNumberHeader, transactionDateHeader, transactionTypeHeader, transactionAmountHeader, paymentModeHeader;

        transactionNumberHeader = new TextView(getApplicationContext());
        transactionDateHeader = new TextView(getApplicationContext());
        transactionTypeHeader = new TextView(getApplicationContext());
        transactionAmountHeader = new TextView(getApplicationContext());
        paymentModeHeader = new TextView(getApplicationContext());

        transactionNumberHeader.setText("S/N");
        transactionNumberHeader.setGravity(Gravity.CENTER);
        transactionNumberHeader.setTextColor(Color.RED);
        transactionNumberHeader.setPadding(10,10,10,10);
        transactionDateHeader.setText("Date");
        transactionDateHeader.setGravity(Gravity.CENTER);
        transactionDateHeader.setTextColor(Color.RED);
        transactionDateHeader.setPadding(10,10,10,10);
        transactionTypeHeader.setText("Type");
        transactionTypeHeader.setGravity(Gravity.CENTER);
        transactionTypeHeader.setTextColor(Color.RED);
        transactionTypeHeader.setPadding(10,10,10,10);
        transactionAmountHeader.setText("Amount");
        transactionAmountHeader.setGravity(Gravity.CENTER);
        transactionAmountHeader.setTextColor(Color.RED);
        transactionAmountHeader.setPadding(10,10,10,10);
        paymentModeHeader.setText("Mode of Payment");
        paymentModeHeader.setGravity(Gravity.CENTER);
        paymentModeHeader.setTextColor(Color.RED);
        paymentModeHeader.setPadding(10,10,10,10);

        row.addView(transactionNumberHeader);
        row.addView(transactionDateHeader);
        row.addView(transactionTypeHeader);
        row.addView(transactionAmountHeader);
        row.addView(paymentModeHeader);

        tableLayout.addView(row);
        addHorizontalSeparator(tableLayout);
    }

    private void addHorizontalSeparator(TableLayout tableLayout){
        // Added Horizontal line as
        View view = new View(getApplicationContext());
        view.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
        view.setBackgroundColor(Color.rgb(50, 50, 50));
        tableLayout.addView(view);
    }

    private void startAnotherActivity(Class newActivity){
        Intent newActivityIntent = new Intent(this, newActivity);
        newActivityIntent.putExtra("savings", savingsTable);
        startActivity(newActivityIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(shouldExecuteOnResume){

            loadAllSavingssAndCompareToLocal();

            List<TransactionTable> collectionTableList = transactionTableQueries.loadAllTransactions(savingsTable.getSavingsId());

            count = 0;

            emptyLayout.setVisibility(View.GONE);
            if(collectionTableList == null || collectionTableList.isEmpty()){
                getTransactionFromCloud();
            }else{
                getNewTransactionAndCompareToCloud(collectionTableList);
            }
        } else{
            shouldExecuteOnResume = true;
        }

    }
}
