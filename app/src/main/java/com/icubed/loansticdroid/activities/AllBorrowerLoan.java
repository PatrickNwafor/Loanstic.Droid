package com.icubed.loansticdroid.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.cloudqueries.LoansQueries;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.localdatabase.LoanTableQueries;
import com.icubed.loansticdroid.localdatabase.LoansTable;
import com.icubed.loansticdroid.util.BitmapUtil;
import com.icubed.loansticdroid.util.DateUtil;
import com.icubed.loansticdroid.util.KeyboardUtil;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllBorrowerLoan extends AppCompatActivity {
    private static final String TAG = ".AllBorrowerLoan";
    private Toolbar toolbar;
    private TableLayout tableLayout;
    private CircleImageView profileImage;
    Button ProfileButton;

    private BorrowersTable borrower;
    private GroupBorrowerTable group;
    private LoansQueries loansQueries;
    private LoanTableQueries loanTableQueries;
    private int tableRowIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_borrower_loan);

        toolbar = findViewById(R.id.borrower_toolbar);
        tableLayout = findViewById(R.id.table);
        profileImage = findViewById(R.id.profile_Image);

        ProfileButton = findViewById(R.id.profile_button);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Loans");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loansQueries = new LoansQueries();
        loanTableQueries = new LoanTableQueries(getApplication());

        borrower = getIntent().getParcelableExtra("borrower");
        group = getIntent().getParcelableExtra("group");

        if(borrower != null){
            setProfileImage();
            getLoanForBorrower();
        }
        else getLoanForGroup();

        createTableHeader();
    }

    private void setProfileImage() {
        if(borrower.getBorrowerImageByteArray() == null){
            BitmapUtil.getImageAndThumbnailWithGlide(
                    this,
                    borrower.getProfileImageUri(),
                    borrower.getProfileImageThumbUri()
            ).into(profileImage);
        }else profileImage.setImageBitmap(BitmapUtil.getBitMapFromBytes(borrower.getBorrowerImageByteArray()));
    }

    private void getLoanForBorrower() {
        loansQueries.retrieveLoanForBorrower(borrower.getBorrowersId())
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){
                                for(DocumentSnapshot doc : task.getResult()){
                                    LoansTable loansTable = doc.toObject(LoansTable.class);
                                    loansTable.setLoanId(doc.getId());

                                    createTableBody(loansTable);
                                }
                            }
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void getLoanForGroup() {
        loansQueries.retrieveLoanForGroup(group.getGroupId())
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){
                                for(DocumentSnapshot doc : task.getResult()){
                                    LoansTable loansTable = doc.toObject(LoansTable.class);
                                    loansTable.setLoanId(doc.getId());

                                    createTableBody(loansTable);
                                }
                            }
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void createTableBody(LoansTable loansTable){
        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        TextView loanNumberHeader, releasedHeader, maturityHeader, repaymentHeader, principalHeader, paidHeader, dueHeader, balanceHeader, feesHeader, penaltyHeader, statusHeader;

        loanNumberHeader = new TextView(this);
        releasedHeader = new TextView(this);
        maturityHeader = new TextView(this);
        repaymentHeader = new TextView(this);
        principalHeader = new TextView(this);
        paidHeader = new TextView(this);
        dueHeader = new TextView(this);
        balanceHeader = new TextView(this);
        feesHeader = new TextView(this);
        penaltyHeader = new TextView(this);
        statusHeader = new TextView(this);

        loanNumberHeader.setText(String.valueOf(tableRowIndex));
        loanNumberHeader.setGravity(Gravity.CENTER);
        releasedHeader.setText(DateUtil.dateString(loansTable.getLoanReleaseDate()));
        releasedHeader.setGravity(Gravity.CENTER);
        maturityHeader.setText(getMaturityDate(loansTable));
        maturityHeader.setGravity(Gravity.CENTER);
        repaymentHeader.setText(loansTable.getRepaymentAmountUnit());
        repaymentHeader.setGravity(Gravity.CENTER);
        principalHeader.setText(String.valueOf(loansTable.getLoanAmount()));
        principalHeader.setGravity(Gravity.CENTER);
        paidHeader.setText("230");
        paidHeader.setGravity(Gravity.CENTER);
        dueHeader.setText("Due");
        dueHeader.setGravity(Gravity.CENTER);
        balanceHeader.setText("10000");
        balanceHeader.setGravity(Gravity.CENTER);
        feesHeader.setText(String.valueOf(loansTable.getLoanFees()));
        feesHeader.setGravity(Gravity.CENTER);
        penaltyHeader.setText("-");
        penaltyHeader.setGravity(Gravity.CENTER);
        statusHeader.setText("Active");
        statusHeader.setGravity(Gravity.CENTER);

        row.addView(loanNumberHeader,100,70);
        row.addView(releasedHeader,200,70);
        row.addView(maturityHeader,200,70);
        row.addView(repaymentHeader,200,70);
        row.addView(principalHeader,200,70);
        row.addView(paidHeader,200,70);
        row.addView(dueHeader,200,70);
        row.addView(balanceHeader,200,70);
        row.addView(feesHeader,200,70);
        row.addView(penaltyHeader,200,70);
        row.addView(statusHeader,200,70);
        tableLayout.addView(row, tableRowIndex);
        tableRowIndex++;
    }

    private void createTableHeader(){
        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        TextView loanNumberHeader, releasedHeader, maturityHeader, repaymentHeader, principalHeader, paidHeader, dueHeader, balanceHeader, feesHeader, penaltyHeader, statusHeader;

        loanNumberHeader = new TextView(this);
        releasedHeader = new TextView(this);
        maturityHeader = new TextView(this);
        repaymentHeader = new TextView(this);
        principalHeader = new TextView(this);
        paidHeader = new TextView(this);
        dueHeader = new TextView(this);
        balanceHeader = new TextView(this);
        feesHeader = new TextView(this);
        penaltyHeader = new TextView(this);
        statusHeader = new TextView(this);

        loanNumberHeader.setText("S/N");
        loanNumberHeader.setGravity(Gravity.CENTER);
        releasedHeader.setText("Released");
        releasedHeader.setGravity(Gravity.CENTER);
        maturityHeader.setText("Maturity");
        maturityHeader.setGravity(Gravity.CENTER);
        repaymentHeader.setText("Repayment");
        repaymentHeader.setGravity(Gravity.CENTER);
        principalHeader.setText("Principal");
        principalHeader.setGravity(Gravity.CENTER);
        paidHeader.setText("Paid");
        paidHeader.setGravity(Gravity.CENTER);
        dueHeader.setText("Due");
        dueHeader.setGravity(Gravity.CENTER);
        balanceHeader.setText("Balance");
        balanceHeader.setGravity(Gravity.CENTER);
        feesHeader.setText("Fees");
        feesHeader.setGravity(Gravity.CENTER);
        penaltyHeader.setText("Penalty");
        penaltyHeader.setGravity(Gravity.CENTER);
        statusHeader.setText("Status");
        statusHeader.setGravity(Gravity.CENTER);

        row.addView(loanNumberHeader,100,70);
        row.addView(releasedHeader,200,70);
        row.addView(maturityHeader,200,70);
        row.addView(repaymentHeader,200,70);
        row.addView(principalHeader,200,70);
        row.addView(paidHeader,200,70);
        row.addView(dueHeader,200,70);
        row.addView(balanceHeader,200,70);
        row.addView(feesHeader,200,70);
        row.addView(penaltyHeader,200,70);
        row.addView(statusHeader,200,70);
        tableLayout.addView(row, 0);
    }

    private String getMaturityDate(LoansTable loansTable) {
        if(loansTable.getLoanDurationUnit().equals("year")){
            Date date = DateUtil.addYear(loansTable.getLoanCreationDate(), loansTable.getLoanDuration());
            return DateUtil.dateString(date);
        }else if(loansTable.getLoanDurationUnit().equals("month")){
            Date date = DateUtil.addMonth(loansTable.getLoanCreationDate(), loansTable.getLoanDuration());
            return DateUtil.dateString(date);
        }else if(loansTable.getLoanDurationUnit().equals("week")){
            Date date = DateUtil.addDay(loansTable.getLoanCreationDate(), loansTable.getLoanDuration()*7);
            return DateUtil.dateString(date);
        }else{
            Date date = DateUtil.addDay(loansTable.getLoanCreationDate(), loansTable.getLoanDuration());
            return DateUtil.dateString(date);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void viewProfile(View view) {
        Intent i = new Intent(AllBorrowerLoan.this, BorrowerDetailsSingle.class);


        Button sharedView = ProfileButton;
        String transitionName = getString(R.string.blue_name);

        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(AllBorrowerLoan.this, sharedView, transitionName);
        startActivity(i, transitionActivityOptions.toBundle());
    }
}
