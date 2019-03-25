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
import com.icubed.loansticdroid.localdatabase.CollectionTable;
import com.icubed.loansticdroid.localdatabase.CollectionTableQueries;
import com.icubed.loansticdroid.localdatabase.LoansTable;
import com.icubed.loansticdroid.models.PaymentScheduleGenerator;
import com.icubed.loansticdroid.util.DateUtil;

import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class ViewCollection extends AppCompatActivity {

    private TableLayout tableLayout;
    private CollectionQueries collectionQueries;
    private CollectionTableQueries collectionTableQueries;
    private LoansTable loan;
    private boolean isGrey = true;
    private double currentBalance = 0;
    private ProgressBar scheduleProgressBar;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_collection);

        Toolbar toolbar = findViewById(R.id.view_collection_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Collections");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loan = getIntent().getParcelableExtra("loan");

        builder = new AlertDialog.Builder(this);

        tableLayout = findViewById(R.id.table);
        scheduleProgressBar = findViewById(R.id.schedule_progress_bar);

        collectionQueries = new CollectionQueries();
        collectionTableQueries = new CollectionTableQueries(getApplication());

        createTableHeader();
        List<CollectionTable> collectionTableList = collectionTableQueries.loadCollectionsForLoan(loan.getLoanId());

        if(collectionTableList == null || collectionTableList.isEmpty()){
            getCollectionFromCloud();
        }else{
            loadCollectionsToUI(collectionTableList);
            getNewCollectionAndCompareToCloud(collectionTableList);
        }
    }

    private void getNewCollectionAndCompareToCloud(final List<CollectionTable> collectionTableList) {
        collectionQueries.retrieveCollectionsDataForALoan(loan.getLoanId())
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){

                                for(DocumentSnapshot doc : task.getResult()){
                                    CollectionTable collectionTable = doc.toObject(CollectionTable.class);
                                    collectionTable.setCollectionId(doc.getId());

                                    boolean newData = true;
                                    Log.d(TAG, "onComplete: "+collectionTable.toString());
                                    for (CollectionTable table : collectionTableList) {
                                        if(table.getCollectionId().equals(collectionTable.getCollectionId())){
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

    private void updateCollection(CollectionTable collectionTable) {

        CollectionTable currentlySaved = collectionTableQueries.loadSingleCollection(collectionTable.getCollectionId());
        collectionTable.setId(currentlySaved.getId());

        if(collectionTable.getLastUpdatedAt().getTime() != currentlySaved.getLastUpdatedAt().getTime()){

            collectionTableQueries.updateCollection(collectionTable);
            Log.d("Borrower", "Borrower Detailed updated");

        }
    }

    private void loadAllCollections() {
        tableLayout.removeAllViews();
        createTableHeader();

        List<CollectionTable> collectionTableList = collectionTableQueries.loadCollectionsForLoan(loan.getLoanId());

        for (CollectionTable collectionTable : collectionTableList) {
            createTableBody(collectionTable);
        }

    }

    private void loadCollectionsToUI(List<CollectionTable> collectionTableList) {
        for (CollectionTable collectionTable : collectionTableList) {
            createTableBody(collectionTable);
        }

        scheduleProgressBar.setVisibility(View.GONE);
    }

    private void getCollectionFromCloud() {
        collectionQueries.retrieveCollectionsDataForALoanAcending(loan.getLoanId())
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){
                                for(DocumentSnapshot doc : task.getResult()){
                                    CollectionTable collectionTable = doc.toObject(CollectionTable.class);
                                    collectionTable.setCollectionId(doc.getId());

                                    Log.d(TAG, "onComplete: "+collectionTable.toString());
                                    saveCollectionToLocalStorage(collectionTable);
                                    createTableBody(collectionTable);
                                }

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

    private void saveCollectionToLocalStorage(CollectionTable collectionTable) {
        CollectionTable collectionTable1 = collectionTableQueries.loadSingleCollection(collectionTable.getCollectionId());
        if(collectionTable1 == null) collectionTableQueries.insertCollectionToStorage(collectionTable);
    }

    private void createTableBody(final CollectionTable collectionTable) {
        TableRow row = new TableRow(getApplicationContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);

        if(!collectionTable.getIsDueCollected() && !collectionTable.getCollectionState().equals(PaymentScheduleGenerator.COLLECTION_STATE_FULL)) {
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.setMessage("Do you want to make payment for collection number " + collectionTable.getCollectionNumber()).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            Intent intent = new Intent(getApplicationContext(), LoanRepayment.class);
                            intent.putExtra("collection", collectionTable);
                            intent.putExtra("lastUpdatedAt", collectionTable.getLastUpdatedAt());
                            intent.putExtra("dueDate", collectionTable.getCollectionDueDate());
                            intent.putExtra("timestamp", collectionTable.getTimestamp());
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

        TextView collectionNumberHeader, collectionDateHeader, dueAmountHeader, totalBalanceHeader, penaltyHeader, isCollectedHeader;

        collectionNumberHeader = new TextView(getApplicationContext());
        collectionDateHeader = new TextView(getApplicationContext());
        dueAmountHeader = new TextView(getApplicationContext());
        totalBalanceHeader = new TextView(getApplicationContext());
        isCollectedHeader = new TextView(getApplicationContext());
        penaltyHeader = new TextView(getApplicationContext());

        collectionNumberHeader.setText(String.valueOf(collectionTable.getCollectionNumber()));
        collectionNumberHeader.setGravity(Gravity.CENTER);
        collectionNumberHeader.setPadding(10,10,10,10);
        collectionDateHeader.setText(DateUtil.dateString(collectionTable.getCollectionDueDate()));
        collectionDateHeader.setGravity(Gravity.CENTER);
        collectionDateHeader.setPadding(10,10,10,10);
        dueAmountHeader.setText(String.valueOf(collectionTable.getCollectionDueAmount()));
        dueAmountHeader.setGravity(Gravity.CENTER);
        dueAmountHeader.setPadding(10,10,10,10);

        totalBalanceHeader.setText(String.valueOf(collectionTable.getAmountPaid()));
        totalBalanceHeader.setGravity(Gravity.CENTER);
        totalBalanceHeader.setPadding(10,10,10,10);

        isCollectedHeader.setText(collectionTable.getCollectionState());
        isCollectedHeader.setGravity(Gravity.CENTER);
        isCollectedHeader.setPadding(10,10,10,10);
        penaltyHeader.setText(String.valueOf(collectionTable.getPenalty()));
        penaltyHeader.setGravity(Gravity.CENTER);
        penaltyHeader.setPadding(10,10,10,10);

        row.addView(collectionNumberHeader);
        row.addView(collectionDateHeader);
        row.addView(dueAmountHeader);
        row.addView(totalBalanceHeader);
        row.addView(isCollectedHeader);
        row.addView(penaltyHeader);

        tableLayout.addView(row);
        addHorizontalSeparator(tableLayout);
    }

    private void createTableHeader(){
        TableRow row = new TableRow(getApplicationContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        TextView collectionNumberHeader, collectionDateHeader, dueAmountHeader, totalBalanceHeader, penaltyHeader, isCollectedHeader;

        collectionNumberHeader = new TextView(getApplicationContext());
        collectionDateHeader = new TextView(getApplicationContext());
        dueAmountHeader = new TextView(getApplicationContext());
        totalBalanceHeader = new TextView(getApplicationContext());
        isCollectedHeader = new TextView(getApplicationContext());
        penaltyHeader = new TextView(getApplicationContext());

        collectionNumberHeader.setText("DueCollection Number");
        collectionNumberHeader.setGravity(Gravity.CENTER);
        collectionNumberHeader.setTextColor(Color.RED);
        collectionNumberHeader.setPadding(10,10,10,10);
        collectionDateHeader.setText("DueCollection Date");
        collectionDateHeader.setGravity(Gravity.CENTER);
        collectionDateHeader.setTextColor(Color.RED);
        collectionDateHeader.setPadding(10,10,10,10);
        dueAmountHeader.setText("DueCollection Amount");
        dueAmountHeader.setGravity(Gravity.CENTER);
        dueAmountHeader.setTextColor(Color.RED);
        dueAmountHeader.setPadding(10,10,10,10);
        totalBalanceHeader.setText("Balance");
        totalBalanceHeader.setGravity(Gravity.CENTER);
        totalBalanceHeader.setTextColor(Color.RED);
        totalBalanceHeader.setPadding(10,10,10,10);
        isCollectedHeader.setText("DueCollection State");
        isCollectedHeader.setGravity(Gravity.CENTER);
        isCollectedHeader.setTextColor(Color.RED);
        isCollectedHeader.setPadding(10,10,10,10);
        penaltyHeader.setText("Penalty");
        penaltyHeader.setGravity(Gravity.CENTER);
        penaltyHeader.setTextColor(Color.RED);
        penaltyHeader.setPadding(10,10,10,10);

        row.addView(collectionNumberHeader);
        row.addView(collectionDateHeader);
        row.addView(dueAmountHeader);
        row.addView(totalBalanceHeader);
        row.addView(isCollectedHeader);
        row.addView(penaltyHeader);

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

        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
