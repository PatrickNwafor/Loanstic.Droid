package com.icubed.loansticdroid.activities;

import android.app.ActivityOptions;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.cloudqueries.LoanTypeQueries;
import com.icubed.loansticdroid.cloudqueries.LoansQueries;
import com.icubed.loansticdroid.cloudqueries.OtherLoanTypeQueries;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.localdatabase.LoanTableQueries;
import com.icubed.loansticdroid.localdatabase.LoanTypeTable;
import com.icubed.loansticdroid.localdatabase.LoanTypeTableQueries;
import com.icubed.loansticdroid.localdatabase.LoansTable;
import com.icubed.loansticdroid.localdatabase.OtherLoanTypesTable;
import com.icubed.loansticdroid.localdatabase.OtherLoanTypesTableQueries;
import com.icubed.loansticdroid.util.BitmapUtil;
import com.icubed.loansticdroid.util.DateUtil;
import com.icubed.loansticdroid.util.KeyboardUtil;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.constraint.Constraints.TAG;

public class AllBorrowerLoan extends AppCompatActivity {
    private static final String TAG = ".AllBorrowerLoan";
    private Toolbar toolbar;
    private TableLayout tableLayout;
    private CircleImageView profileImage;
    Button ProfileButton;

    private LoanTypeQueries loanTypeQueries;
    private OtherLoanTypeQueries otherLoanTypeQueries;
    private LoanTypeTableQueries loanTypeTableQueries;
    private OtherLoanTypesTableQueries otherLoanTypesTableQueries;
    private BorrowersTable borrower;
    private GroupBorrowerTable group;
    private LoansQueries loansQueries;
    private LoanTableQueries loanTableQueries;
    private boolean isGrey = true;

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
        loanTypeQueries = new LoanTypeQueries();
        loanTypeTableQueries = new LoanTypeTableQueries(getApplication());
        otherLoanTypesTableQueries = new OtherLoanTypesTableQueries(getApplication());
        otherLoanTypeQueries = new OtherLoanTypeQueries();

        borrower = getIntent().getParcelableExtra("borrower");
        group = getIntent().getParcelableExtra("group");

        createTableHeader();

        if(borrower != null){
            setProfileImage();
            List<LoansTable> loansTables = loanTableQueries.loadLoansForBorrowerOrderByCreationDate(borrower.getBorrowersId());
            if(loansTables.isEmpty()){
                getLoanForBorrower();
            }else{
                loadLoanFromStorage(loansTables);
                getLoanForBorrowerAndCompareToCloud(loansTables);
            }
        }
        else {
            List<LoansTable> loansTables = loanTableQueries.loadLoansForGroupOrderByCreationDate(group.getGroupId());
            if(loansTables.isEmpty()){
                getLoanForGroup();
            }else{
                loadLoanFromStorage(loansTables);
                getLoanForGroupAndCompareToCloud(loansTables);
            }
        }
    }

    private void getLoanForGroupAndCompareToCloud(final List<LoansTable> loansTables) {
        loansQueries.retrieveLoanForGroup(group.getGroupId())
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){

                                List<LoansTable> loansInStorage = loansTables;
                                for(DocumentSnapshot doc : task.getResult().getDocuments()) {

                                    Boolean doesDataExist = false;
                                    for (LoansTable loanTab : loansTables) {
                                        if (loanTab.getLoanId().equals(doc.getId())) {
                                            doesDataExist = true;
                                            loansInStorage.remove(loanTab);
                                            Log.d(TAG, "onComplete: loan id of " + doc.getId() + " already exist");
                                            break;
                                        }
                                    }

                                    if (!doesDataExist) {
                                        Log.d(TAG, "onComplete: loan id of " + doc.getId() + " does not exist");

                                        LoansTable loansTable = doc.toObject(LoansTable.class);
                                        loansTable.setLoanId(doc.getId());

                                        saveLoanToLocalStorage(loansTable);
                                        getLoanType(loansTable);
                                    } else {
                                        //Update local table if any changes
                                        updateTable(doc);
                                    }
                                }

                                //to delete deleted borrower in cloud from storage
                                if(!loansInStorage.isEmpty()){
                                    for(LoansTable loanTab : loansInStorage){
                                        deleteLoanFromLocalStorage(loanTab);
                                        Log.d("Delete", "deleted "+loanTab.getLoanId()+ " from storage");
                                    }
                                }

                            }else{
                                Toast.makeText(getApplicationContext(), "Document is empty", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Log.d("Loan", "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void getLoanForBorrowerAndCompareToCloud(final List<LoansTable> loansTables) {
        loansQueries.retrieveLoanForBorrower(borrower.getBorrowersId())
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){

                                List<LoansTable> loansInStorage = loansTables;
                                for(DocumentSnapshot doc : task.getResult().getDocuments()) {

                                    Boolean doesDataExist = false;
                                    for (LoansTable loanTab : loansTables) {
                                        if (loanTab.getLoanId().equals(doc.getId())) {
                                            doesDataExist = true;
                                            loansInStorage.remove(loanTab);
                                            Log.d(TAG, "onComplete: loan id of " + doc.getId() + " already exist");
                                            break;
                                        }
                                    }

                                    if (!doesDataExist) {
                                        Log.d(TAG, "onComplete: loan id of " + doc.getId() + " does not exist");

                                        LoansTable loansTable = doc.toObject(LoansTable.class);
                                        loansTable.setLoanId(doc.getId());

                                        saveLoanToLocalStorage(loansTable);
                                        getLoanType(loansTable);
                                    } else {
                                        //Update local table if any changes
                                        updateTable(doc);
                                    }
                                }

                                //to delete deleted borrower in cloud from storage
                                if(!loansInStorage.isEmpty()){
                                    for(LoansTable loanTab : loansInStorage){
                                        deleteLoanFromLocalStorage(loanTab);
                                        Log.d("Delete", "deleted "+loanTab.getLoanId()+ " from storage");
                                    }
                                }

                            }else{
                                Toast.makeText(getApplicationContext(), "Document is empty", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Log.d("Loan", "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void saveLoanToLocalStorage(LoansTable loansTable) {
        LoansTable loansTable1 = loanTableQueries.loadSingleLoan(loansTable.getLoanId());
        if(loansTable1 == null) loanTableQueries.insertLoanToStorage(loansTable);
    }

    /**
     * this method deletes a borrower from local storage
     * @param loansTable
     */
    private void deleteLoanFromLocalStorage(LoansTable loansTable) {
        loanTableQueries.deleteLoan(loansTable);
    }

    /**
     * updates any changes in the loan details from cloud in local storage
     * @param doc
     */
    private void updateTable(DocumentSnapshot doc) {
        LoansTable loansTable = doc.toObject(LoansTable.class);
        loansTable.setLoanId(doc.getId());

        LoansTable currentlySaved = loanTableQueries.loadSingleLoan(doc.getId());
        loansTable.setId(currentlySaved.getId());

        if(loansTable.getLastUpdatedAt().getTime() != currentlySaved.getLastUpdatedAt().getTime()){

            loanTableQueries.updateLoanDetails(loansTable);
            Log.d("Loan", "Loan Detailed updated");

        }
    }

    private void loadLoanFromStorage(List<LoansTable> loansTables) {
        for (LoansTable loansTable : loansTables) {
            if(loansTable.getIsOtherLoanType()){
                OtherLoanTypesTable otherLoanTypesTable = otherLoanTypesTableQueries.loadSingleLoanType(loansTable.getLoanTypeId());
                createTableBody(loansTable, null, otherLoanTypesTable);
            }else{
                LoanTypeTable loanTypeTable = loanTypeTableQueries.loadSingleLoanType(loansTable.getLoanTypeId());
                createTableBody(loansTable, loanTypeTable, null);
            }
        }

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

                                    getLoanType(loansTable);
                                }
                            }
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    /**
     * this methods helps to decide if the loan is of a type registered initially by the branch manager
     * or the loan type is a custom one created during the registration of the loan
     * also it also checks if the loan type exist in local storage
     * if it doesnt exist it calls up either  getOtherLoanType(loansTable) or getNormalLoanType(loansTable)
     * the above depends on whether the loan type is registered or custom
     * @param loansTable
     */
    private void getLoanType(LoansTable loansTable) {

        if(loansTable.getIsOtherLoanType()) {
            OtherLoanTypesTable otherLoanTypesTable = otherLoanTypesTableQueries.loadSingleLoanType(loansTable.getLoanTypeId());

            if(otherLoanTypesTable != null) createTableBody(loansTable, null, otherLoanTypesTable);
            else getOtherLoanType(loansTable);
        }
        else{
            LoanTypeTable loanTypeTable = loanTypeTableQueries.loadSingleLoanType(loansTable.getLoanTypeId());

            if(loanTypeTable != null) createTableBody(loansTable, loanTypeTable, null);
            else getNormalLoanType(loansTable);
        }
    }

    /**
     * gets registered loan type from firebase firestore
     * @param loansTable
     */
    private void getNormalLoanType(final LoansTable loansTable) {
        loanTypeQueries.retrieveSingleLoanType(loansTable.getLoanTypeId())
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            LoanTypeTable loanTypeTable = task.getResult().toObject(LoanTypeTable.class);
                            loanTypeTable.setLoanTypeId(task.getResult().getId());

                            saveLoanTypeToLocalStorage(loanTypeTable);
                            createTableBody(loansTable, loanTypeTable, null);
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    /**
     * saves registered loan type to local storage
     * @param loanTypeTable
     */
    private void saveLoanTypeToLocalStorage(LoanTypeTable loanTypeTable) {
        LoanTypeTable loanTypeTableList = loanTypeTableQueries.loadSingleLoanType(loanTypeTable.getLoanTypeId());
        if(loanTypeTableList == null) loanTypeTableQueries.insertLoanTypeToStorage(loanTypeTable);
    }


    /**
     * gets custom loan type created during loan registration from firebase firestore
     * @param loansTable
     */
    private void getOtherLoanType(final LoansTable loansTable) {
        otherLoanTypeQueries.retrieveSingleOtherLoanType(loansTable.getLoanTypeId())
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            OtherLoanTypesTable otherLoanTypesTable = task.getResult().toObject(OtherLoanTypesTable.class);
                            otherLoanTypesTable.setOtherLoanTypeId(task.getResult().getId());

                            saveOtherLoanTypeToLocalStorage(otherLoanTypesTable);

                            createTableBody(loansTable, null, otherLoanTypesTable);
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    /**
     * saves custom loan type to local storage
     * @param otherLoanTypesTable
     */
    private void saveOtherLoanTypeToLocalStorage(OtherLoanTypesTable otherLoanTypesTable) {
        OtherLoanTypesTable otherLoanTypesTableList = otherLoanTypesTableQueries.loadSingleLoanType(otherLoanTypesTable.getOtherLoanTypeId());
        if(otherLoanTypesTableList == null) otherLoanTypesTableQueries.insertLoanTypeToStorage(otherLoanTypesTable);
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

                                    getLoanType(loansTable);
                                }
                            }
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void createTableBody(final LoansTable loansTable, final LoanTypeTable loanTypeTable, final OtherLoanTypesTable otherLoanTypesTable){

        TableRow row = new TableRow(this);
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

        TextView releasedHeader, maturityHeader, repaymentHeader, principalHeader, paidHeader, dueHeader, balanceHeader, feesHeader, penaltyHeader, statusHeader;

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoanEditPage.class);
                intent.putExtra("loan", loansTable);
                intent.putExtra("update", loansTable.getLastUpdatedAt());
                intent.putExtra("create", loansTable.getLoanCreationDate());
                intent.putExtra("approved", loansTable.getLoanApprovedDate());
                intent.putExtra("release", loansTable.getLoanReleaseDate());
                intent.putExtra("borrower", borrower);
                intent.putExtra("group", group);
                intent.putExtra("loanType", loanTypeTable);
                intent.putExtra("otherLoanType", otherLoanTypesTable);
                startActivity(intent);
            }
        });

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

        tableLayout.addView(row);
        addHorizontalSeparator(tableLayout);
    }

    private void createTableHeader(){
        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        TextView releasedHeader, maturityHeader, repaymentHeader, principalHeader, paidHeader, dueHeader, balanceHeader, feesHeader, penaltyHeader, statusHeader;

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

        tableLayout.addView(row);
        addHorizontalSeparator(tableLayout);
    }

    private void addVerticalSeparator(TableRow row){
        // Add vertical separator
        View v = new View(this);
        v.setLayoutParams(new TableRow.LayoutParams(1, TableRow.LayoutParams.MATCH_PARENT));
        v.setBackgroundColor(Color.rgb(50, 50, 50));
        row.addView(v);
    }

    private void addHorizontalSeparator(TableLayout tableLayout){
        // Added Horizontal line as
        View view = new View(this);
        view.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
        view.setBackgroundColor(Color.rgb(50, 50, 50));
        tableLayout.addView(view);
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
