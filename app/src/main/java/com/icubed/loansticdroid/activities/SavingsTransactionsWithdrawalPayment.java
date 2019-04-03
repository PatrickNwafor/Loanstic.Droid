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
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.icubed.loansticdroid.cloudqueries.SavingsPlanCollectionQueries;
import com.icubed.loansticdroid.cloudqueries.SavingsQueries;
import com.icubed.loansticdroid.cloudqueries.TransactionPhotoValidationQueries;
import com.icubed.loansticdroid.cloudqueries.TransactionQueries;
import com.icubed.loansticdroid.localdatabase.PaymentModeTable;
import com.icubed.loansticdroid.localdatabase.SavingsPlanCollectionTable;
import com.icubed.loansticdroid.localdatabase.SavingsTable;
import com.icubed.loansticdroid.localdatabase.TransactionTable;
import com.icubed.loansticdroid.models.PaymentScheduleGenerator;
import com.icubed.loansticdroid.util.CustomDialogBox.PaymentDialogBox;
import com.icubed.loansticdroid.util.DateUtil;
import com.icubed.loansticdroid.util.FormUtil;
import com.icubed.loansticdroid.util.LocationProviderUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SavingsTransactionsWithdrawalPayment extends AppCompatActivity {
    
    private TextView balanceTextView;
    private EditText withdrawEditText;
    private SavingsTable savingsTable;

    private static final String TAG = ".WithdrawTrans";
    private RecyclerView paymentPictureRecyclerView;
    private LottieAnimationView camera;
    private FormUtil formUtil;
    private TransactionQueries transactionQueries;
    private SavingsQueries savingsQueries;
    private Account account;
    private LocationProviderUtil locationProviderUtil;
    private boolean isLocationGotten = false;
    MenuItem menuItem;
    private final int CAMERA_REQUEST_CODE = 2494;
    private PaymentRecyclerAdapter paymentRecyclerAdapter;
    List<Bitmap> bitmapList;
    private ProgressBar progressBar;
    private AlertDialog.Builder builder2;
    private TransactionPhotoValidationQueries transactionPhotoValidationQueries;
    private double collectionAmount;
    private PaymentDialogBox paymentDialogBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings_transactions_withdrawal_payment);

        Toolbar toolbar = findViewById(R.id.collection_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Withdrawal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        
        savingsTable = getIntent().getParcelableExtra("savings");
        
        balanceTextView = findViewById(R.id.balance_amount);
        withdrawEditText = findViewById(R.id.withdraw_amount);
        paymentPictureRecyclerView = findViewById(R.id.paymentList);
        camera = findViewById(R.id.camera);
        progressBar = findViewById(R.id.progressBar);
        
        balanceTextView.setText(String.valueOf(savingsTable.getAmountSaved()));

        builder2 = new AlertDialog.Builder(this);

        transactionQueries = new TransactionQueries();
        account = new Account();
        savingsQueries = new SavingsQueries();
        locationProviderUtil = new LocationProviderUtil(getApplicationContext());
        transactionPhotoValidationQueries = new TransactionPhotoValidationQueries();

        paymentDialogBox = new PaymentDialogBox(this);

        bitmapList = new ArrayList<>();
        paymentRecyclerAdapter = new PaymentRecyclerAdapter(bitmapList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        paymentPictureRecyclerView.setLayoutManager(layoutManager);
        paymentPictureRecyclerView.setAdapter(paymentRecyclerAdapter);

        savingsTable = getIntent().getParcelableExtra("savings");
        
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

                paymentDialogBox.setOnYesClicked(new PaymentDialogBox.OnButtonClick() {
                    @Override
                    public void onYesButtonClick() {
                        if (formUtil.isSingleFormEmpty(withdrawEditText)) {
                            withdrawEditText.setError("Please enter amount to withdraw");
                        } else if(Double.parseDouble(withdrawEditText.getText().toString()) > savingsTable.getAmountSaved()){
                            withdrawEditText.setError("You cannot withdraw more than what you have");
                        } else if(Double.parseDouble(withdrawEditText.getText().toString()) < 0){
                            withdrawEditText.setError("Negative value is not allowed");
                        }
                        else if (bitmapList.isEmpty()) {
                            withdrawEditText.setError(null);
                            Toast.makeText(SavingsTransactionsWithdrawalPayment.this, "Please upload payment pictures", Toast.LENGTH_SHORT).show();
                        } else {
                            withdrawEditText.setError(null);
                            disabledViews();
                            getCurrentLocation();
                        }
                    }
                });

                paymentDialogBox.show();
                return true;
            }
        });

        return true;
    }

    private void disabledViews(){
        menuItem.setEnabled(false);
        camera.setEnabled(false);
        withdrawEditText.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void enableViews(){
        menuItem.setEnabled(true);
        camera.setEnabled(true);
        withdrawEditText.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }

    private void submitPayment(Location getLocation) {

        collectionAmount = Double.parseDouble(withdrawEditText.getText().toString());

        Map<String, Object> paymentMap = new HashMap<>();

        paymentMap.put("savingsId", savingsTable.getSavingsId());
        paymentMap.put("isDepositFromRegular", false);

        paymentMap.put("loanOfficerId", account.getCurrentUserId());
        paymentMap.put("paymentMode", "Cash");
        paymentMap.put("transactionAmount", Double.parseDouble(withdrawEditText.getText().toString()));
        paymentMap.put("transactionTime", new Date());
        paymentMap.put("lastUpdatedAt", new Date());
        paymentMap.put("transactionType", TransactionTable.TYPE_DEBIT);
        paymentMap.put("photoLatitude", getLocation.getLatitude());
        paymentMap.put("photoLongitude", getLocation.getLongitude());

        transactionQueries.createSavingsTransactions(paymentMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){

                            updateSavingsDetails();
                            uploadPaymentPhoto(task.getResult().getId());
                            progressBar.setVisibility(View.GONE);
                            builder2.setMessage("Transaction Successful!")
                                    .setTitle("Transaction")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(final DialogInterface dialog, final int id) {
                                            finish();
                                        }
                                    });
                            final AlertDialog alert = builder2.create();
                            alert.show();
                        }else{
                            enableViews();
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                            Toast.makeText(SavingsTransactionsWithdrawalPayment.this, "Payment failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadPaymentPhoto(final String paymentId) {
        for (final Bitmap bitmap : bitmapList) {
            transactionPhotoValidationQueries.uploadImage(bitmap)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                final String downloadUri = task.getResult().getDownloadUrl().toString();

                                transactionPhotoValidationQueries.uploadImageThumb(bitmap)
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
        objectMap.put("transactionId", paymentId);
        objectMap.put("imageUri", downloadUri);
        objectMap.put("imageUriThumb", downloadUriThumb);
        objectMap.put("timestamp", new Date());

        transactionPhotoValidationQueries.savePhotoUriToCloud(objectMap);
    }

    private void updateSavingsDetails() {
        final Map<String, Object> objectMap = new HashMap<>();

        savingsQueries.retrieveSingleSavings(savingsTable.getSavingsId())
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            SavingsTable savingsTable = task.getResult().toObject(SavingsTable.class);

                            objectMap.put("amountSaved", savingsTable.getAmountSaved() - Double.parseDouble(withdrawEditText.getText().toString()));
                            objectMap.put("lastUpdatedAt", new Date());

                            savingsQueries.updateSavingsDetails(objectMap, task.getResult().getId());

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
