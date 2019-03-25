package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
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
import com.icubed.loansticdroid.cloudqueries.PaymentModeQueries;
import com.icubed.loansticdroid.cloudqueries.PaymentQueries;
import com.icubed.loansticdroid.localdatabase.CollectionTable;
import com.icubed.loansticdroid.localdatabase.CollectionTableQueries;
import com.icubed.loansticdroid.localdatabase.LoansTable;
import com.icubed.loansticdroid.localdatabase.PaymentModeTable;
import com.icubed.loansticdroid.localdatabase.PaymentModeTableQueries;
import com.icubed.loansticdroid.localdatabase.PaymentTable;
import com.icubed.loansticdroid.localdatabase.PaymentTableQueries;
import com.icubed.loansticdroid.util.DateUtil;

import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class LoanPaymentActivity extends AppCompatActivity {

    public ProgressBar progressBar;
    private TableLayout tableLayout;
    private boolean isGrey = true;
    private PaymentQueries paymentQueries;
    private PaymentTableQueries paymentTableQueries;
    private PaymentModeQueries paymentModeQueries;
    private PaymentModeTableQueries paymentModeTableQueries;
    private CollectionQueries collectionQueries;
    private CollectionTableQueries collectionTableQueries;
    private LoansTable loan;
    private int count;
    private int docSize;
    private int paymentCount;
    private LinearLayout emptyLayout;
    private TextView totalDueCollected;
    private double totalAmountPaid;
    private boolean firstData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_payment);

        Toolbar toolbar = findViewById(R.id.collection_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Loan Repayments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        loan = getIntent().getParcelableExtra("loan");

        progressBar = findViewById(R.id.schedule_progress_bar);
        tableLayout = findViewById(R.id.table);
        emptyLayout = findViewById(R.id.search_empty_layout);
        totalDueCollected = findViewById(R.id.total_due_collected_text);

        paymentCount = 1;
        totalAmountPaid = 0;
        count = 0;

        paymentQueries = new PaymentQueries();
        paymentTableQueries = new PaymentTableQueries(getApplication());
        paymentModeQueries = new PaymentModeQueries();
        paymentModeTableQueries = new PaymentModeTableQueries(getApplication());
        collectionQueries = new CollectionQueries();
        collectionTableQueries = new CollectionTableQueries(getApplication());

        createTableHeader();
        List<PaymentTable> paymentTables = paymentTableQueries.loadAllPayments(loan.getLoanId());

        if(paymentTables.isEmpty()){
            retrieveNewPaymentDetails();
        }else{
            loadPaymentsToUi();
            retrieveNewPaymentAndCompareToCloud(paymentTables);
        }
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

    private void retrieveNewPaymentAndCompareToCloud(final List<PaymentTable> paymentTables) {
        paymentQueries.retrieveAllPaymentForLoan(loan.getLoanId())
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){

                                firstData = false;
                                docSize = task.getResult().size();
                                //get doc size not registered
                                for(DocumentSnapshot documentSnapshot : task.getResult()){
                                    for (PaymentTable table : paymentTables) {
                                        if(table.getPaymentId().equals(documentSnapshot.getId())){
                                            docSize--;
                                            break;
                                        }
                                    }
                                }

                                for(DocumentSnapshot doc : task.getResult()){
                                    PaymentTable paymentTable = doc.toObject(PaymentTable.class);
                                    paymentTable.setPaymentId(doc.getId());

                                    boolean newData = true;
                                    Log.d(TAG, "onComplete: "+paymentTable.toString());
                                    for (PaymentTable table : paymentTables) {
                                        if(table.getPaymentId().equals(paymentTable.getPaymentId())){
                                            newData = false;
                                            break;
                                        }
                                    }

                                    if(newData) {
                                        savePaymentToLocalStorage(paymentTable);
                                        retrievePaymentModeDetails(paymentTable);
                                    }
                                }
                                progressBar.setVisibility(View.GONE);
                            }else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "DueCollection is empty", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            progressBar.setVisibility(View.GONE);
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void loadAllPayment() {
        totalAmountPaid = 0;
        paymentCount = 1;
        tableLayout.removeAllViews();
        createTableHeader();

        List<PaymentTable> paymentTables = paymentTableQueries.loadAllPayments(loan.getLoanId());

        for (PaymentTable paymentTable : paymentTables) {
            PaymentModeTable paymentModeTable = null;
            if(paymentTable.getPaymentModeId() != null) paymentModeTable = paymentModeTableQueries.loadSinglePaymentMode(paymentTable.getPaymentModeId());
            CollectionTable collectionTable = collectionTableQueries.loadSingleCollection(paymentTable.getCollectionId());

            createTableBody(paymentTable, paymentModeTable, collectionTable);
        }

        totalDueCollected.setText(String.valueOf(totalAmountPaid));
    }

    private void retrieveNewPaymentDetails() {
        paymentQueries.retrieveAllPaymentForLoan(loan.getLoanId())
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){

                                docSize = task.getResult().size();
                                for(DocumentSnapshot doc : task.getResult()){
                                    PaymentTable paymentTable = doc.toObject(PaymentTable.class);
                                    paymentTable.setPaymentId(doc.getId());

                                    savePaymentToLocalStorage(paymentTable);
                                    retrievePaymentModeDetails(paymentTable);
                                }

                            }else{
                                progressBar.setVisibility(View.GONE);
                                emptyLayout.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), "No payment made yet for loan", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Unable to retrieve payments", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: "+task.getResult());
                        }
                    }
                });
    }

    private void savePaymentToLocalStorage(PaymentTable paymentTable) {
        PaymentTable paymentTable1 = paymentTableQueries.loadSinglePayment(paymentTable.getPaymentId());
        if(paymentTable1 == null) paymentTableQueries.insertPaymentToStorage(paymentTable);
    }

    private void retrievePaymentModeDetails(PaymentTable paymentTable) {
        if(paymentTable.getPaymentModeId() != null){
            PaymentModeTable paymentModeTable = paymentModeTableQueries.loadSinglePaymentMode(paymentTable.getPaymentModeId());
            if(paymentModeTable == null) getPaymentMode(paymentTable);
            else retrieveCollectionDetails(paymentTable);
        }
    }

    private void getPaymentMode(final PaymentTable paymentTable) {
        paymentModeQueries.retrieveSinglePaymentMode(paymentTable.getPaymentModeId())
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            PaymentModeTable paymentModeTable = task.getResult().toObject(PaymentModeTable.class);
                            paymentModeTable.setPaymentModeId(task.getResult().getId());

                            savePaymentModeToStorage(paymentModeTable);
                            retrieveCollectionDetails(paymentTable);
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void savePaymentModeToStorage(PaymentModeTable paymentModeTable) {
        PaymentModeTable paymentModeTable1 = paymentModeTableQueries.loadSinglePaymentMode(paymentModeTable.getPaymentModeId());
        if(paymentModeTable1 == null) paymentModeTableQueries.insertPaymentModeToStorage(paymentModeTable);
    }

    private void retrieveCollectionDetails(PaymentTable paymentTable) {
        CollectionTable collectionTable = collectionTableQueries.loadSingleCollection(paymentTable.getCollectionId());

        if(collectionTable == null){
            getCollectionDetails(paymentTable.getCollectionId());
        }else{
            count++;
            if(firstData) {
                if (count == docSize) {
                    loadPaymentsToUi();
                }
            }else{
                if (count == docSize) {
                    loadAllPayment();
                }
            }
        }
    }

    private void getCollectionDetails(String collectionId) {
        collectionQueries.retrieveSingleCollection(collectionId)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            CollectionTable collectionTable = task.getResult().toObject(CollectionTable.class);
                            collectionTable.setCollectionId(task.getResult().getId());

                            saveCollectionDetailsToLocalStorage(collectionTable);
                            count++;
                            if(firstData) {
                                if (count == docSize) {
                                    loadPaymentsToUi();
                                }
                            }else{
                                if (count == docSize) {
                                    loadAllPayment();
                                }
                            }
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void saveCollectionDetailsToLocalStorage(CollectionTable collectionTable) {
        CollectionTable collectionTable1 = collectionTableQueries.loadSingleCollection(collectionTable.getCollectionId());
        if(collectionTable1 == null) collectionTableQueries.insertCollectionToStorage(collectionTable);
    }

    private void loadPaymentsToUi() {
        List<PaymentTable> paymentTables = paymentTableQueries.loadAllPayments(loan.getLoanId());

        for (PaymentTable paymentTable : paymentTables) {
            PaymentModeTable paymentModeTable = null;
            if(paymentTable.getPaymentModeId() != null) paymentModeTable = paymentModeTableQueries.loadSinglePaymentMode(paymentTable.getPaymentModeId());
            CollectionTable collectionTable = collectionTableQueries.loadSingleCollection(paymentTable.getCollectionId());

            createTableBody(paymentTable, paymentModeTable, collectionTable);
        }

        totalDueCollected.setText(String.valueOf(totalAmountPaid));
        progressBar.setVisibility(View.GONE);
    }

    private void createTableBody(final PaymentTable paymentTable, final PaymentModeTable paymentModeTable, final CollectionTable collectionTable) {
        TableRow row = new TableRow(getApplicationContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PaymentDetailsActivity.class);
                intent.putExtra("collection", collectionTable);
                intent.putExtra("paymentId", paymentTable.getPaymentId());
                if(paymentTable.getPaymentModeId() != null) intent.putExtra("paymentMode", paymentModeTable.getPaymentMode());
                else intent.putExtra("paymentMode", paymentTable.getPaymentMode());
                intent.putExtra("amountPaid", paymentTable.getAmountPaid());
                intent.putExtra("paymentDate", paymentTable.getPaymentTime());
                startActivity(intent);
            }
        });

        //row color alternates between grey and white
        if(isGrey) {
            row.setBackgroundColor(Color.GRAY);
            isGrey = false;
        }
        else {
            row.setBackgroundColor(Color.WHITE);
            isGrey = true;
        }

        TextView collectionNumberHeader, paymentNumber, paymentDate, paymentAmount, paymentMode;

        collectionNumberHeader = new TextView(getApplicationContext());
        paymentNumber = new TextView(getApplicationContext());
        paymentDate = new TextView(getApplicationContext());
        paymentAmount = new TextView(getApplicationContext());
        paymentMode = new TextView(getApplicationContext());

        collectionNumberHeader.setText(String.valueOf(collectionTable.getCollectionNumber()));
        collectionNumberHeader.setGravity(Gravity.CENTER);
        collectionNumberHeader.setPadding(10,10,10,10);
        paymentNumber.setText(String.valueOf(paymentCount));
        paymentNumber.setGravity(Gravity.CENTER);
        paymentNumber.setPadding(10,10,10,10);
        paymentDate.setText(DateUtil.dateString(paymentTable.getPaymentTime()));
        paymentDate.setGravity(Gravity.CENTER);
        paymentDate.setPadding(10,10,10,10);

        paymentAmount.setText(String.valueOf(paymentTable.getAmountPaid()));
        paymentAmount.setGravity(Gravity.CENTER);
        paymentAmount.setPadding(10,10,10,10);

        if(paymentTable.getPaymentModeId() != null) paymentMode.setText(paymentModeTable.getPaymentMode());
        else paymentMode.setText(paymentTable.getPaymentMode());
        paymentMode.setGravity(Gravity.CENTER);
        paymentMode.setPadding(10,10,10,10);

        row.addView(paymentNumber);
        row.addView(paymentDate);
        row.addView(collectionNumberHeader);
        row.addView(paymentAmount);
        row.addView(paymentMode);

        totalAmountPaid = totalAmountPaid + paymentTable.getAmountPaid();

        tableLayout.addView(row);
        addHorizontalSeparator(tableLayout);
        paymentCount++;
    }

    private void createTableHeader(){
        TableRow row = new TableRow(getApplicationContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        TextView paymentNumber, paymentDate, collectionNumberHeader, paymentAmount, paymentMode;

        collectionNumberHeader = new TextView(getApplicationContext());
        paymentNumber = new TextView(getApplicationContext());
        paymentDate = new TextView(getApplicationContext());
        paymentAmount = new TextView(getApplicationContext());
        paymentMode = new TextView(getApplicationContext());

        collectionNumberHeader.setText("Collection Number");
        collectionNumberHeader.setGravity(Gravity.CENTER);
        collectionNumberHeader.setTextColor(Color.RED);
        collectionNumberHeader.setPadding(10,10,10,10);
        paymentNumber.setText("S/N");
        paymentNumber.setGravity(Gravity.CENTER);
        paymentNumber.setTextColor(Color.RED);
        paymentNumber.setPadding(10,10,10,10);
        paymentDate.setText("Payment Date");
        paymentDate.setGravity(Gravity.CENTER);
        paymentDate.setTextColor(Color.RED);
        paymentDate.setPadding(10,10,10,10);
        paymentAmount.setText("Amount");
        paymentAmount.setGravity(Gravity.CENTER);
        paymentAmount.setTextColor(Color.RED);
        paymentAmount.setPadding(10,10,10,10);
        paymentMode.setText("Payment Means");
        paymentMode.setGravity(Gravity.CENTER);
        paymentMode.setTextColor(Color.RED);
        paymentMode.setPadding(10,10,10,10);

        row.addView(paymentNumber);
        row.addView(paymentDate);
        row.addView(collectionNumberHeader);
        row.addView(paymentAmount);
        row.addView(paymentMode);

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
}
