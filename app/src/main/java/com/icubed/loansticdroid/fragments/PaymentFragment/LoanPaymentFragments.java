package com.icubed.loansticdroid.fragments.PaymentFragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.icubed.loansticdroid.activities.LoanPaymentActivity;
import com.icubed.loansticdroid.activities.LoanRepayment;
import com.icubed.loansticdroid.activities.PaymentDetailsActivity;
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
import com.icubed.loansticdroid.models.PaymentScheduleGenerator;
import com.icubed.loansticdroid.util.DateUtil;

import java.util.List;

import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoanPaymentFragments extends Fragment {

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
    private int count = 0;
    private int docSize;
    private int paymentCount = 1;
    private LinearLayout emptyLayout;
    private TextView totalDueCollected;
    private double totalAmountPaid = 0;

    public LoanPaymentFragments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loan_payment_fragments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.schedule_progress_bar);
        tableLayout = view.findViewById(R.id.table);
        emptyLayout = view.findViewById(R.id.search_empty_layout);
        totalDueCollected = view.findViewById(R.id.total_due_collected_text);

        loan = ((LoanPaymentActivity) getContext()).loan;

        paymentQueries = new PaymentQueries();
        paymentTableQueries = new PaymentTableQueries(getActivity().getApplication());
        paymentModeQueries = new PaymentModeQueries();
        paymentModeTableQueries = new PaymentModeTableQueries(getActivity().getApplication());
        collectionQueries = new CollectionQueries();
        collectionTableQueries = new CollectionTableQueries(getActivity().getApplication());

        createTableHeader();
        List<PaymentTable> paymentTables = paymentTableQueries.loadAllPayments(loan.getLoanId());

        if(paymentTables.isEmpty()){
            retrieveNewPaymentDetails();
        }else{
            loadPaymentsToUi();
            retrieveNewPaymentAndCompareToCloud(paymentTables);
        }
    }

    private void retrieveNewPaymentAndCompareToCloud(final List<PaymentTable> paymentTables) {
        paymentQueries.retrieveAllPaymentForLoan(loan.getLoanId())
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){

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

                                loadAllPayment();

                                progressBar.setVisibility(View.GONE);
                            }else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), "DueCollection is empty", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getContext(), "No payment made yet for loan", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Unable to retrieve payments", Toast.LENGTH_SHORT).show();
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
            if(count == docSize){
                loadPaymentsToUi();
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
                            if(count == docSize){
                                loadPaymentsToUi();
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
        TableRow row = new TableRow(getContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PaymentDetailsActivity.class);
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

        collectionNumberHeader = new TextView(getContext());
        paymentNumber = new TextView(getContext());
        paymentDate = new TextView(getContext());
        paymentAmount = new TextView(getContext());
        paymentMode = new TextView(getContext());

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
        TableRow row = new TableRow(getContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        TextView paymentNumber, paymentDate, collectionNumberHeader, paymentAmount, paymentMode;

        collectionNumberHeader = new TextView(getContext());
        paymentNumber = new TextView(getContext());
        paymentDate = new TextView(getContext());
        paymentAmount = new TextView(getContext());
        paymentMode = new TextView(getContext());

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
        View view = new View(getContext());
        view.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
        view.setBackgroundColor(Color.rgb(50, 50, 50));
        tableLayout.addView(view);
    }

}
