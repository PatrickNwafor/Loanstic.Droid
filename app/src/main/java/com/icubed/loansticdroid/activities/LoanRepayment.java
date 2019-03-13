package com.icubed.loansticdroid.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.PaymentRecyclerAdapter;
import com.icubed.loansticdroid.cloudqueries.Account;
import com.icubed.loansticdroid.cloudqueries.CollectionQueries;
import com.icubed.loansticdroid.cloudqueries.LoansQueries;
import com.icubed.loansticdroid.cloudqueries.PaymentPhotoValidationQueries;
import com.icubed.loansticdroid.cloudqueries.PaymentQueries;
import com.icubed.loansticdroid.localdatabase.CollectionTable;
import com.icubed.loansticdroid.localdatabase.LoansTable;
import com.icubed.loansticdroid.localdatabase.PaymentModeTable;
import com.icubed.loansticdroid.localdatabase.PaymentModeTableQueries;
import com.icubed.loansticdroid.models.PaymentScheduleGenerator;
import com.icubed.loansticdroid.util.DateUtil;
import com.icubed.loansticdroid.util.FormUtil;
import com.icubed.loansticdroid.util.LocationProviderUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoanRepayment extends AppCompatActivity {
    private static final String TAG = ".LoanRepayment";
    private Toolbar toolbar;
    private TextView collectionNumberTextView, collectionDateTextView, collectionAmountTextView;
    private EditText amountPaidTextView, amountDueTextView;
    private RecyclerView paymentPictureRecyclerView;
    private LottieAnimationView camera;
    private FormUtil formUtil;
    private CollectionTable collectionTable;
    private RadioButton full, partial;
    private Spinner paymentDrp;
    private String selectedMode;
    private PaymentQueries paymentQueries;
    private LoansQueries loansQueries;
    CollectionQueries collectionQueries;
    private Account account;
    private LocationProviderUtil locationProviderUtil;
    private boolean isLocationGotten = false;
    MenuItem menuItem;
    private final int CAMERA_REQUEST_CODE = 2494;
    private PaymentRecyclerAdapter paymentRecyclerAdapter;
    List<Bitmap> bitmapList;
    private ProgressBar progressBar;
    private int selectedPaymentModePosition;
    private List<PaymentModeTable> paymentModeTables;
    private AlertDialog.Builder builder;
    private AlertDialog.Builder builder2;
    private PaymentPhotoValidationQueries paymentPhotoValidationQueries;
    private double collectionAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_repayment);
        toolbar = findViewById(R.id.repayment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Loan Repayment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        paymentDrp = findViewById(R.id.payment_mode_value);
        collectionNumberTextView = findViewById(R.id.collection_number_value);
        collectionDateTextView = findViewById(R.id.collection_date_value);
        collectionAmountTextView = findViewById(R.id.collection_amount_value);
        amountPaidTextView = findViewById(R.id.amount_paid_value);
        amountDueTextView = findViewById(R.id.amount_due_value);
        paymentPictureRecyclerView = findViewById(R.id.paymentList);
        camera = findViewById(R.id.camera);
        full = findViewById(R.id.full);
        partial = findViewById(R.id.partial);
        progressBar = findViewById(R.id.progressBar);

        PaymentModeTableQueries paymentModeTableQueries = new PaymentModeTableQueries(getApplication());
        paymentModeTables = paymentModeTableQueries.loadAllPaymentModes();

        ArrayAdapter<CharSequence> adapterPaymet;
        String[] paymentArr;
        int count = 0;

        if(!paymentModeTables.isEmpty()) {
            paymentArr = new String[paymentModeTables.size()];
            for (PaymentModeTable paymentModeTable : paymentModeTables) {
                paymentArr[count] = paymentModeTable.getPaymentMode();
                count++;
            }
        }else{
            paymentArr = new String[]{"Cash", "Bank Transfer", "Bank Teller"};
        }

        adapterPaymet = new ArrayAdapter<CharSequence>(getBaseContext(),android.R.layout.simple_spinner_item,paymentArr);
        adapterPaymet.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentDrp.setAdapter(adapterPaymet);
        if(!paymentModeTables.isEmpty()) selectedPaymentModePosition = paymentDrp.getSelectedItemPosition();
        else selectedPaymentModePosition = -1;

        builder = new AlertDialog.Builder(this);
        builder2 = new AlertDialog.Builder(this);

        paymentQueries = new PaymentQueries();
        account = new Account();
        loansQueries = new LoansQueries();
        collectionQueries = new CollectionQueries();
        locationProviderUtil = new LocationProviderUtil(getApplicationContext());
        paymentPhotoValidationQueries = new PaymentPhotoValidationQueries();

        bitmapList = new ArrayList<>();
        paymentRecyclerAdapter = new PaymentRecyclerAdapter(bitmapList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        paymentPictureRecyclerView.setLayoutManager(layoutManager);
        paymentPictureRecyclerView.setAdapter(paymentRecyclerAdapter);

        collectionTable = getIntent().getParcelableExtra("collection");
        Date lastUpdatedAt = (Date) getIntent().getSerializableExtra("lastUpdatedAt");
        Date timestamp = (Date) getIntent().getSerializableExtra("timestamp");
        Date dueDate = (Date) getIntent().getSerializableExtra("dueDate");

        collectionTable.setLastUpdatedAt(lastUpdatedAt);
        collectionTable.setCollectionDueDate(dueDate);
        collectionTable.setTimestamp(timestamp);

        collectionNumberTextView.setText(String.valueOf(collectionTable.getCollectionNumber()));
        collectionDateTextView.setText(DateUtil.dateString(collectionTable.getCollectionDueDate()));
        collectionAmountTextView.setText(String.valueOf(collectionTable.getCollectionDueAmount() - collectionTable.getAmountPaid()));
        amountPaidTextView.setText(String.valueOf(collectionTable.getCollectionDueAmount() - collectionTable.getAmountPaid()));
        collectionAmount = collectionTable.getCollectionDueAmount() - collectionTable.getAmountPaid();

        partial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    amountPaidTextView.setEnabled(true);
                    amountPaidTextView.setText("");
                }
            }
        });

        full.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    amountPaidTextView.setEnabled(false);
                    amountPaidTextView.setText(String.valueOf(collectionTable.getCollectionDueAmount() - collectionTable.getAmountPaid()));
                }
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent(CAMERA_REQUEST_CODE);
            }
        });

        formUtil = new FormUtil();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.loanee_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItem = menu.findItem(R.id.next_to_loan_terms);
        menuItem.setTitle("Finish");
        menuItem.setVisible(true);
        menu.findItem(R.id.search_loan).setVisible(false);

        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                builder.setMessage("Are you sure you want to make payment")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                if(formUtil.isSingleFormEmpty(amountPaidTextView)) {
                                    amountPaidTextView.setError("Please enter amount paid");
                                }else if(bitmapList.isEmpty()){
                                    amountPaidTextView.setError(null);
                                    Toast.makeText(LoanRepayment.this, "Please upload payment pictures", Toast.LENGTH_SHORT).show();
                                }
                                else if(partial.isChecked() &&
                                        Double.parseDouble(amountPaidTextView.getText().toString()) >= collectionAmount){
                                    Toast.makeText(LoanRepayment.this, "Amount to be paid cannot be greater than collection amount", Toast.LENGTH_SHORT).show();
                                }else{
                                    amountPaidTextView.setError(null);
                                    disabledViews();
                                    getCurrentLocation();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                dialog.cancel();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        });

        return true;
    }

    private void disabledViews(){
        full.setEnabled(false);
        partial.setEnabled(false);
        menuItem.setEnabled(false);
        camera.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void enableViews(){
        full.setEnabled(true);
        partial.setEnabled(true);
        menuItem.setEnabled(true);
        camera.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }

    private void submitPayment(Location getLocation) {

        selectedMode = paymentDrp.getSelectedItem().toString();

        Map<String, Object> paymentMap = new HashMap<>();
        paymentMap.put("collectionId", collectionTable.getCollectionId());
        paymentMap.put("loanId", collectionTable.getLoanId());
        paymentMap.put("loanOfficerId", account.getCurrentUserId());
        if(selectedPaymentModePosition != -1) paymentMap.put("paymentModeId", paymentModeTables.get(selectedPaymentModePosition).getPaymentModeId());
        else paymentMap.put("paymentMode", selectedMode);
        paymentMap.put("amountPaid", Double.parseDouble(amountPaidTextView.getText().toString()));
        paymentMap.put("paymentTime", new Date());
        paymentMap.put("lastUpdatedAt", new Date());
        paymentMap.put("photoLatitude", getLocation.getLatitude());
        paymentMap.put("photoLongitude", getLocation.getLongitude());

        paymentQueries.makePayment(paymentMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            updateLoanDetails();
                            updateCollectionDetails();
                            uploadPaymentPhoto(task.getResult().getId());
                            progressBar.setVisibility(View.GONE);
                            builder2.setMessage("Payment Successful!")
                                    .setTitle("Payment")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(final DialogInterface dialog, final int id) {
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                            final AlertDialog alert = builder2.create();
                            alert.show();
                        }else{
                            enableViews();
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                            Toast.makeText(LoanRepayment.this, "Payment failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadPaymentPhoto(final String paymentId) {
        for (final Bitmap bitmap : bitmapList) {
            paymentPhotoValidationQueries.uploadImage(bitmap)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                final String downloadUri = task.getResult().getDownloadUrl().toString();

                                paymentPhotoValidationQueries.uploadImageThumb(bitmap)
                                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    String downloadUriThumb = task.getResult().getDownloadUrl().toString();

                                                    savePaymentPhotoToCloud(paymentId, downloadUri, downloadUriThumb);
                                                }else{
                                                    Log.d(TAG, "onComplete: "+task.getException().getMessage());
                                                }
                                            }
                                        });
                            }else{
                                Log.d(TAG, "onComplete: "+task.getException().getMessage());
                            }
                        }
                    });
        }

    }

    private void savePaymentPhotoToCloud(String paymentId, String downloadUri, String downloadUriThumb) {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("paymentId", paymentId);
        objectMap.put("imageUri", downloadUri);
        objectMap.put("imageUriThumb", downloadUriThumb);
        objectMap.put("timestamp", new Date());

        paymentPhotoValidationQueries.savePaymentPhotoUriToCloud(objectMap);
    }

    private void updateLoanDetails() {
        final Map<String, Object> objectMap = new HashMap<>();

        loansQueries.retrieveSingleLoan(collectionTable.getLoanId())
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            LoansTable loansTable = task.getResult().toObject(LoansTable.class);

                            objectMap.put("repaymentMade", loansTable.getRepaymentMade()+Double.parseDouble(amountPaidTextView.getText().toString()));
                            objectMap.put("lastUpdatedAt", new Date());

                            loansQueries.updateLoanDetails(objectMap, collectionTable.getLoanId());

                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void updateCollectionDetails() {
        final Map<String, Object> objectMap = new HashMap<>();

        collectionQueries.retrieveSingleCollection(collectionTable.getCollectionId())
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            CollectionTable collectionTable = task.getResult().toObject(CollectionTable.class);
                            collectionTable.setCollectionId(task.getResult().getId());

                            double amount = collectionTable.getAmountPaid()+Double.parseDouble(amountPaidTextView.getText().toString());

                            objectMap.put("amountPaid", amount);
                            objectMap.put("lastUpdatedAt", new Date());
                            if(full.isChecked()){
                                objectMap.put("collectionState", PaymentScheduleGenerator.COLLECTION_STATE_FULL);
                                objectMap.put("isDueCollected", true);
                            }
                            else objectMap.put("collectionState", PaymentScheduleGenerator.COLLECTION_STATE_PARTIAL);

                            collectionQueries.updateCollectionDetails(objectMap, collectionTable.getCollectionId());

                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void getCurrentLocation(){
        locationProviderUtil.requestSingleUpdate(new LocationProviderUtil.LocationCallback() {
            @Override
            public void onNewLocationAvailable(LocationProviderUtil.GPSCoordinates location) {
                if(!isLocationGotten){
                    isLocationGotten = true;
                    submitPayment(location.getLocation);
                }
            }
        });
    }

    /***************Calls up Up Phone camera********************/
    private void dispatchTakePictureIntent(int CAMERA_CODE) {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, CAMERA_CODE);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (CAMERA_REQUEST_CODE) : {
                if(resultCode == RESULT_OK){
                    //Borrower profile image result
                    Bundle extras = data.getExtras();
                    //Bitmap returned from camera
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    bitmapList.add(imageBitmap);
                    paymentRecyclerAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
               finish();

            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
