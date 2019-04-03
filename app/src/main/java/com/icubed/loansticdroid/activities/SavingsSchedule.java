package com.icubed.loansticdroid.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
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
import com.icubed.loansticdroid.cloudqueries.CollectionQueries;
import com.icubed.loansticdroid.cloudqueries.SavingsPlanCollectionQueries;
import com.icubed.loansticdroid.localdatabase.CollectionTable;
import com.icubed.loansticdroid.localdatabase.CollectionTableQueries;
import com.icubed.loansticdroid.localdatabase.LoansTable;
import com.icubed.loansticdroid.localdatabase.SavingsPlanCollectionTable;
import com.icubed.loansticdroid.localdatabase.SavingsPlanCollectionTableQueries;
import com.icubed.loansticdroid.localdatabase.SavingsTable;
import com.icubed.loansticdroid.localdatabase.TransactionTable;
import com.icubed.loansticdroid.models.PaymentScheduleGenerator;
import com.icubed.loansticdroid.models.SavingsScheduleGenerator;
import com.icubed.loansticdroid.util.DateUtil;

import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class SavingsSchedule extends AppCompatActivity {

    private SavingsTable savingsTable;
    private TableLayout tableLayout;
    private SavingsPlanCollectionQueries savingsPlanCollectionQueries;
    private SavingsPlanCollectionTableQueries savingsPlanCollectionTableQueries;
    private boolean isGrey = true;
    private ProgressBar scheduleProgressBar;
    private AlertDialog.Builder builder;
    private LinearLayout emptyLayout;
    boolean shouldExecuteOnResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings_schedule);

        shouldExecuteOnResume = false;

        savingsTable = getIntent().getParcelableExtra("savings");

        Toolbar toolbar = findViewById(R.id.pick_plan_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Savings Collections");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        builder = new AlertDialog.Builder(this);

        tableLayout = findViewById(R.id.table);
        scheduleProgressBar = findViewById(R.id.progressBar);
        emptyLayout = findViewById(R.id.search_empty_layout);

        savingsPlanCollectionQueries = new SavingsPlanCollectionQueries();
        savingsPlanCollectionTableQueries = new SavingsPlanCollectionTableQueries(getApplication());

        createTableHeader();
        List<SavingsPlanCollectionTable> collectionTableList = savingsPlanCollectionTableQueries.loadCollectionsForSavingsSchedule(savingsTable.getSavingsId());

        if(collectionTableList == null || collectionTableList.isEmpty()){
            getCollectionFromCloud();
        }else{
            loadCollectionsToUI(collectionTableList);
            getNewCollectionAndCompareToCloud(collectionTableList);
        }
    }

    private void getNewCollectionAndCompareToCloud(final List<SavingsPlanCollectionTable> collectionTableList) {
        savingsPlanCollectionQueries.retrieveSavingsPlanCollectionDataForSavings(savingsTable.getSavingsId())
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){

                                for(DocumentSnapshot doc : task.getResult()){
                                    SavingsPlanCollectionTable collectionTable = doc.toObject(SavingsPlanCollectionTable.class);
                                    collectionTable.setSavingsPlanCollectionId(doc.getId());

                                    boolean newData = true;
                                    Log.d(TAG, "onComplete: "+collectionTable.toString());
                                    for (SavingsPlanCollectionTable table : collectionTableList) {
                                        if(table.getSavingsPlanCollectionId().equals(collectionTable.getSavingsPlanCollectionId())){
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

    private void updateCollection(SavingsPlanCollectionTable collectionTable) {

        SavingsPlanCollectionTable currentlySaved = savingsPlanCollectionTableQueries.loadSingleCollection(collectionTable.getSavingsPlanCollectionId());
        collectionTable.setId(currentlySaved.getId());

        if(collectionTable.getLastUpdatedAt().getTime() != currentlySaved.getLastUpdatedAt().getTime()){

            savingsPlanCollectionTableQueries.updateCollection(collectionTable);
            Log.d("Savings Collection", "Detailed updated");

        }
    }

    private void loadAllCollections() {
        tableLayout.removeAllViews();
        createTableHeader();

        List<SavingsPlanCollectionTable> collectionTableList = savingsPlanCollectionTableQueries.loadCollectionsForSavingsSchedule(savingsTable.getSavingsId());

        for (SavingsPlanCollectionTable collectionTable : collectionTableList) {
            createTableBody(collectionTable);
        }

    }

    private void loadCollectionsToUI(List<SavingsPlanCollectionTable> collectionTableList) {
        for (SavingsPlanCollectionTable collectionTable : collectionTableList) {
            createTableBody(collectionTable);
        }

        scheduleProgressBar.setVisibility(View.GONE);
    }

    private void getCollectionFromCloud() {
        savingsPlanCollectionQueries.retrieveSavingsSchedulesCollectionDataForASavingsAcending(savingsTable.getSavingsId())
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){
                                for(DocumentSnapshot doc : task.getResult()){
                                    SavingsPlanCollectionTable collectionTable = doc.toObject(SavingsPlanCollectionTable.class);
                                    collectionTable.setSavingsPlanCollectionId(doc.getId());

                                    Log.d(TAG, "onComplete: "+collectionTable.toString());
                                    saveCollectionToLocalStorage(collectionTable);
                                    createTableBody(collectionTable);
                                }

                                scheduleProgressBar.setVisibility(View.GONE);
                            }else {
                                tableLayout.removeAllViews();
                                emptyLayout.setVisibility(View.VISIBLE);
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

    private void saveCollectionToLocalStorage(SavingsPlanCollectionTable collectionTable) {
        SavingsPlanCollectionTable collectionTable1 = savingsPlanCollectionTableQueries.loadSingleCollection(collectionTable.getSavingsPlanCollectionId());
        if(collectionTable1 == null) savingsPlanCollectionTableQueries.insertCollectionToStorage(collectionTable);
    }

    private void createTableBody(final SavingsPlanCollectionTable collectionTable) {
        TableRow row = new TableRow(getApplicationContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        row.setLayoutParams(lp);

        if(!collectionTable.getIsSavingsCollected() && !collectionTable.getIsSavingsCollected().equals(SavingsScheduleGenerator.COLLECTION_STATE_FULL)) {
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.setTitle("Payment")
                            .setMessage("Do you want to make payment for collection number " + collectionTable.getSavingsCollectionNumber())
                            .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            Intent intent = new Intent(getApplicationContext(), SavingsTransactionDepositPayment.class);
                            intent.putExtra("collection", collectionTable);
                            intent.putExtra("savings", savingsTable);
                            startActivity(intent);
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                        }
                    });
                    final AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        }
        //row color alternates between grey and white
        if(isGrey) {
            row.setBackgroundColor(Color.GRAY);
            isGrey = false;
        }
        else {
            row.setBackgroundColor(Color.WHITE);
            isGrey = true;
        }

        TextView collectionNumberHeader, collectionDateHeader, dueAmountHeader, amountPaidHeader, balanceHeader, collectionStateHeader;

        collectionNumberHeader = new TextView(getApplicationContext());
        collectionDateHeader = new TextView(getApplicationContext());
        dueAmountHeader = new TextView(getApplicationContext());
        amountPaidHeader = new TextView(getApplicationContext());
        collectionStateHeader = new TextView(getApplicationContext());
        balanceHeader = new TextView(getApplicationContext());

        collectionNumberHeader.setText(String.valueOf(collectionTable.getSavingsCollectionNumber()));
        collectionNumberHeader.setGravity(Gravity.CENTER);
        collectionNumberHeader.setPadding(10,10,10,10);
        collectionDateHeader.setText(DateUtil.dateString(collectionTable.getSavingsCollectionDueDate()));
        collectionDateHeader.setGravity(Gravity.CENTER);
        collectionDateHeader.setPadding(10,10,10,10);
        dueAmountHeader.setText(String.valueOf(collectionTable.getSavingsCollectionAmount()));
        dueAmountHeader.setGravity(Gravity.CENTER);
        dueAmountHeader.setPadding(10,10,10,10);

        amountPaidHeader.setText(String.valueOf(collectionTable.getAmountPaid()));
        amountPaidHeader.setGravity(Gravity.CENTER);
        amountPaidHeader.setPadding(10,10,10,10);

        collectionStateHeader.setText(collectionTable.getCollectionState());
        collectionStateHeader.setGravity(Gravity.CENTER);
        collectionStateHeader.setPadding(10,10,10,10);

        balanceHeader.setText(String.valueOf(collectionTable.getSavingsCollectionAmount() - collectionTable.getAmountPaid()));
        balanceHeader.setGravity(Gravity.CENTER);
        balanceHeader.setPadding(10,10,10,10);

        row.addView(collectionNumberHeader);
        row.addView(collectionDateHeader);
        row.addView(dueAmountHeader);
        row.addView(amountPaidHeader);
        row.addView(balanceHeader);
        row.addView(collectionStateHeader);

        tableLayout.addView(row);
        addHorizontalSeparator(tableLayout);
    }

    private void createTableHeader(){
        TableRow row = new TableRow(getApplicationContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        row.setLayoutParams(lp);
        TextView collectionNumberHeader, collectionDateHeader, dueAmountHeader, amountPaidHeader, balanceHeader, collectionStateHeader;

        collectionNumberHeader = new TextView(getApplicationContext());
        collectionDateHeader = new TextView(getApplicationContext());
        dueAmountHeader = new TextView(getApplicationContext());
        amountPaidHeader = new TextView(getApplicationContext());
        collectionStateHeader = new TextView(getApplicationContext());
        balanceHeader = new TextView(getApplicationContext());

        collectionNumberHeader.setText("S/N");
        collectionNumberHeader.setGravity(Gravity.CENTER);
        collectionNumberHeader.setTextColor(Color.RED);
        collectionNumberHeader.setPadding(10,10,10,10);
        collectionDateHeader.setText("Collection Date");
        collectionDateHeader.setGravity(Gravity.CENTER);
        collectionDateHeader.setTextColor(Color.RED);
        collectionDateHeader.setPadding(10,10,10,10);
        dueAmountHeader.setText("Deposit Amount");
        dueAmountHeader.setGravity(Gravity.CENTER);
        dueAmountHeader.setTextColor(Color.RED);
        dueAmountHeader.setPadding(10,10,10,10);
        amountPaidHeader.setText("Amount Paid");
        amountPaidHeader.setGravity(Gravity.CENTER);
        amountPaidHeader.setTextColor(Color.RED);
        amountPaidHeader.setPadding(10,10,10,10);
        collectionStateHeader.setText("Collection State");
        collectionStateHeader.setGravity(Gravity.CENTER);
        collectionStateHeader.setTextColor(Color.RED);
        collectionStateHeader.setPadding(10,10,10,10);
        balanceHeader.setText("Balance");
        balanceHeader.setGravity(Gravity.CENTER);
        balanceHeader.setTextColor(Color.RED);
        balanceHeader.setPadding(10,10,10,10);

        row.addView(collectionNumberHeader);
        row.addView(collectionDateHeader);
        row.addView(dueAmountHeader);
        row.addView(amountPaidHeader);
        row.addView(balanceHeader);
        row.addView(collectionStateHeader);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(shouldExecuteOnResume){

            List<SavingsPlanCollectionTable> collectionTableList = savingsPlanCollectionTableQueries.loadCollectionsForSavingsSchedule(savingsTable.getSavingsId());

            emptyLayout.setVisibility(View.GONE);
            if(collectionTableList == null || collectionTableList.isEmpty()){
                getCollectionFromCloud();
            }else{
                getNewCollectionAndCompareToCloud(collectionTableList);
            }
        } else{
            shouldExecuteOnResume = true;
        }
    }
}
