package com.icubed.loansticdroid.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.cloudqueries.Account;
import com.icubed.loansticdroid.cloudqueries.LoansQueries;
import com.icubed.loansticdroid.cloudqueries.OtherLoanTypeQueries;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.localdatabase.LoanTypeTable;
import com.icubed.loansticdroid.notification.LoanRequestNotificationQueries;
import com.icubed.loansticdroid.notification.LoanRequestNotificationTable;
import com.icubed.loansticdroid.util.FormUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class LoanTerms extends AppCompatActivity {
    private static final String TAG = ".LoanTerms";
    EditText loanReleaseDateEditText;
     Spinner spRate;
    Spinner spDuration, spCycle;

    String selectedRate;
    String selectedDuration, selectedCycle;
    private Toolbar toolbar;
    private LoanTypeTable loanTypeTable;
    private GroupBorrowerTable group;
    private BorrowersTable borrower;

    private LoansQueries loansQueries;
    private FormUtil formUtil;
    private Account account;
    private OtherLoanTypeQueries otherLoanTypeQueries;
    
    private EditText loanTypeNameEditText, principlaAmountEditText, loanInterestEditText
            , loanDurationTextView, repaymentCycleEditText, loanFeesEditText, loanTypeDescEditText;
    private CardView otherLoanCardView;
    private Button submitBtn;
    private ProgressBar progressBar;

    private LoanRequestNotificationQueries loanRequestNotificationQueries;

    final Calendar myCalendar = Calendar.getInstance();
    private Index index;
    private Date creationDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_terms);

        toolbar = findViewById(R.id.loan_terms_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Loan Terms");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loanTypeTable = getIntent().getParcelableExtra("loan_type");
        borrower = getIntent().getParcelableExtra("borrower");
        group = getIntent().getParcelableExtra("group");
        
        loanTypeNameEditText = findViewById(R.id.loan_type_name);
        principlaAmountEditText = findViewById(R.id.principal_amount);
        loanReleaseDateEditText= findViewById(R.id.loan_releae_date);
        loanInterestEditText = findViewById(R.id.loan_interest);
        loanDurationTextView = findViewById(R.id.loan_duration);
        repaymentCycleEditText = findViewById(R.id.repayment_cycle);
        loanFeesEditText = findViewById(R.id.loan_fees);
        loanTypeDescEditText = findViewById(R.id.loan_type_desc);
        otherLoanCardView = findViewById(R.id.other_loan_card);
        submitBtn = findViewById(R.id.proceed);
        progressBar = findViewById(R.id.progressBar);

        loansQueries = new LoansQueries();
        formUtil = new FormUtil();
        account = new Account();
        otherLoanTypeQueries = new OtherLoanTypeQueries();
        loanRequestNotificationQueries = new LoanRequestNotificationQueries();

        //Algolia search initiation
        Client client = new Client("HGQ25JRZ8Y", "d4453ddf82775ee2324c47244b30a7c7");
        index = client.getIndex("Loan");

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitButtonListener();
            }
        });

        //checking if the type of loan is a registered loan or other loan
        if(loanTypeTable == null){
            otherLoanCardView.setVisibility(View.VISIBLE);
            loanTypeNameEditText.setEnabled(true);
        }else{
            loanTypeNameEditText.setText(loanTypeTable.getLoanTypeName());
            loanTypeDescEditText.setText(loanTypeTable.getLoanTypeDescription());
        }

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

        //for duration
        spCycle = findViewById(R.id.spCycle);
        ArrayAdapter<CharSequence> adapterCycle;
        String[] CycleArr = {"per year", "per month", "per week", "per day"};
        adapterCycle = new ArrayAdapter<CharSequence>(this,android.R.layout.simple_spinner_item,CycleArr);
        adapterCycle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCycle.setAdapter(adapterCycle);
        selectedCycle = spCycle.getSelectedItem().toString();



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

        loanReleaseDateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(LoanTerms.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void submitButtonListener(){
        creationDate = new Date();
        if(loanTypeTable == null){
            createLoanTypeId();
        }else {
            submitLoan(false);
        }
    }

    private void createLoanTypeId(){

        if(!checkForm()) return;

        progressBar.setVisibility(View.VISIBLE);

        Map<String, Object> loanTypeMap = new HashMap<>();
        loanTypeMap.put("branchId", "2s6biiTANBZ4VqTUDrtEsdwgc822");
        loanTypeMap.put("otherLoanTypeName", loanTypeNameEditText.getText().toString());
        loanTypeMap.put("otherLoanTypeDescription", loanTypeDescEditText.getText().toString());
        loanTypeMap.put("timestamp", new Date());

        otherLoanTypeQueries.saveOtherLoanType(loanTypeMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            loanTypeTable = new LoanTypeTable();
                            loanTypeTable.setLoanTypeId(task.getResult().getId());
                            loanTypeTable.setLoanTypeName(loanTypeNameEditText.getText().toString());
                            submitLoan(true);
                        }else{
                            progressBar.setVisibility(View.GONE);
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                            Toast.makeText(LoanTerms.this, "Could not create loan type", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void submitLoan(Boolean isOtherLoanType){
        if(!checkForm()) return;

        progressBar.setVisibility(View.VISIBLE);

        Map<String, Object> loanMap = new HashMap<>();

        if(borrower != null)
            loanMap.put("borrowerId", borrower.getBorrowersId());
        else loanMap.put("groupId", group.getGroupId());

        loanMap.put("isOtherLoanType", isOtherLoanType);
        loanMap.put("loanOfficerId", account.getCurrentUserId());
        loanMap.put("loanTypeId", loanTypeTable.getLoanTypeId());
        loanMap.put("loanCreationDate",  creationDate);
        loanMap.put("loanAmount", Double.parseDouble(principlaAmountEditText.getText().toString()));
        loanMap.put("loanInterestRate", Double.parseDouble(loanInterestEditText.getText().toString()));
        loanMap.put("isLoanApproved", false);
        loanMap.put("loanReleaseDate", myCalendar.getTime());
        loanMap.put("loanDuration", Integer.parseInt(loanDurationTextView.getText().toString()));
        loanMap.put("loanApprovedDate", null);
        loanMap.put("loanInterestRateUnit", selectedRate);
        loanMap.put("loanDurationUnit", selectedDuration);
        loanMap.put("repaymentAmount", Double.parseDouble(repaymentCycleEditText.getText().toString()));
        loanMap.put("repaymentAmountUnit", selectedCycle);

        if(!formUtil.isSingleFormEmpty(loanFeesEditText) && formUtil.doesFormContainNumbersOnly(loanFeesEditText))
            loanMap.put("loanFees", Double.parseDouble(loanFeesEditText.getText().toString()));

        loansQueries.createLoan(loanMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){

                            registerLoanForSearch(task.getResult().getId());
                            sendNotification(task.getResult().getId());

                        }else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoanTerms.this, "Failed to create loan", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void sendNotification(String loanId){
        LoanRequestNotificationTable loanRequestNotificationTable = new LoanRequestNotificationTable();
        loanRequestNotificationTable.setLoanId(loanId);
        loanRequestNotificationTable.setTimestamp(creationDate);

        loanRequestNotificationQueries.sendNotification(loanRequestNotificationTable, account.getCurrentUserId());
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        loanReleaseDateEditText.setError(null);
        loanReleaseDateEditText.setText(sdf.format(myCalendar.getTime()));
    }

    private boolean checkForm(){
        EditText[] forms = new EditText[]{loanTypeNameEditText, principlaAmountEditText, loanInterestEditText
                , loanDurationTextView, loanReleaseDateEditText, repaymentCycleEditText, loanTypeDescEditText};
        EditText[] numbers = new EditText[]{principlaAmountEditText, loanInterestEditText, loanDurationTextView
                ,repaymentCycleEditText};

        if(isAnyFormEmpty(forms))return false;
        if(!doesFieldContainNumberOnly(numbers))return false;
        if(!formUtil.doesFormContainIntegersOnly(loanDurationTextView)) return false;
        if(!formUtil.isSingleFormEmpty(loanFeesEditText)){
            return formUtil.doesFormContainDoublesOnly(loanFeesEditText);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    public Boolean isAnyFormEmpty(EditText[] forms){
        Boolean isFormEmpty = false;
        boolean[] listOfFormsEmpty = formUtil.isListOfFormsEmpty(forms);

        for(int i = 0; i < forms.length; i++){
            if(listOfFormsEmpty[i]){
                forms[i].setError("Field is required");

                if(!isFormEmpty) {
                    forms[i].requestFocus();
                }

                isFormEmpty = true;
            }else{
                forms[i].setError(null);
            }
        }

        return isFormEmpty;
    }

    private void registerLoanForSearch(final String loanId) {
        Map<String, Object> searchMap = new HashMap<>();

        searchMap.put("loanTypeName", loanTypeTable.getLoanTypeName());
        if(borrower != null) searchMap.put("name", borrower.getFirstName()+" "+borrower.getLastName());
        else searchMap.put("name", group.getGroupName());

        JSONObject object = new JSONObject(searchMap);
        index.addObjectAsync(object, loanId, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject jsonObject, AlgoliaException e) {

                if(e == null) {
                    Toast.makeText(LoanTerms.this, "Loan created", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoanTerms.this, LoanActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Failed to register loan for search", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "requestCompleted: "+e.getMessage());
                }
            }
        });
    }

    public Boolean doesFieldContainNumberOnly(EditText[] editText){

        for (EditText text : editText) {
            if(!formUtil.doesFormContainDoublesOnly(text)){
                text.setError("Only numbers are allowed");
                return false;
            }else {
                text.setError(null);
            }
        }
        return true;
    }


}
