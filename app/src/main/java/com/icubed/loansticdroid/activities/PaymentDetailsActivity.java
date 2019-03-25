package com.icubed.loansticdroid.activities;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.PaymentPhotoRecyclerAdapter;
import com.icubed.loansticdroid.cloudqueries.PaymentPhotoValidationQueries;
import com.icubed.loansticdroid.localdatabase.CollectionTable;
import com.icubed.loansticdroid.localdatabase.PaymentPhotoVerificationTable;
import com.icubed.loansticdroid.localdatabase.PaymentPhotoVerificationTableQueries;
import com.icubed.loansticdroid.util.BitmapUtil;
import com.icubed.loansticdroid.util.DateUtil;

import java.util.Date;
import java.util.List;

public class PaymentDetailsActivity extends AppCompatActivity {

    private static final String TAG = ".PaymentDetailsActivity";
    CollectionTable collectionTable;
    double amountPaid;
    Date paymentDate;
    String paymentMode;
    String paymentId;

    private Toolbar toolbar;
    private TextView collectionNumberTextView, paymentDateTextView, paymentAmountTextView, amountDueTextView, paymentModeTextView;
    private RecyclerView paymentPictureRecyclerView;
    private PaymentPhotoValidationQueries paymentPhotoValidationQueries;
    private PaymentPhotoVerificationTableQueries paymentPhotoVerificationTableQueries;
    private PaymentPhotoRecyclerAdapter paymentRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        toolbar = findViewById(R.id.collection_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Payment Receipt");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        collectionNumberTextView = findViewById(R.id.collection_number_value);
        paymentDateTextView = findViewById(R.id.collection_date_value);
        paymentAmountTextView = findViewById(R.id.collection_amount_value);
        amountDueTextView = findViewById(R.id.amount_due_value);
        paymentPictureRecyclerView = findViewById(R.id.paymentList);
        paymentModeTextView = findViewById(R.id.payment_mode_value);

        paymentPhotoValidationQueries = new PaymentPhotoValidationQueries();
        paymentPhotoVerificationTableQueries = new PaymentPhotoVerificationTableQueries(getApplication());

        collectionTable = getIntent().getParcelableExtra("collection");
        amountPaid = getIntent().getDoubleExtra("amountPaid", 0.0);
        paymentDate = (Date) getIntent().getSerializableExtra("paymentDate");
        paymentMode = getIntent().getStringExtra("paymentMode");
        paymentId = getIntent().getStringExtra("paymentId");

        collectionNumberTextView.setText(String.valueOf(collectionTable.getCollectionNumber()));
        paymentDateTextView.setText(DateUtil.dateString(paymentDate));
        paymentAmountTextView.setText(String.valueOf(amountPaid));
        paymentModeTextView.setText(paymentMode);

        List<PaymentPhotoVerificationTable> paymentPhotoVerificationTables = paymentPhotoVerificationTableQueries.loadAllPaymentPhotoVerif(paymentId);

        if(paymentPhotoVerificationTables.isEmpty()){
            getPaymentPhoto();
        }else{
            loadPhotoToUI(paymentPhotoVerificationTables);
        }
    }

    private void loadPhotoToUI(List<PaymentPhotoVerificationTable> paymentPhotoVerificationTables) {
        paymentRecyclerAdapter = new PaymentPhotoRecyclerAdapter(paymentPhotoVerificationTables);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        paymentPictureRecyclerView.setLayoutManager(layoutManager);
        paymentPictureRecyclerView.setAdapter(paymentRecyclerAdapter);
    }

    private void getPaymentPhoto() {
        paymentPhotoValidationQueries.retrieveAllPhotoVerifForPayment(paymentId)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){
                                for(DocumentSnapshot doc : task.getResult()){
                                    PaymentPhotoVerificationTable paymentPhotoVerificationTable = doc.toObject(PaymentPhotoVerificationTable.class);
                                    paymentPhotoVerificationTable.setPaymentVerificationPhotoId(doc.getId());

                                    savePhotoVerifToStorage(paymentPhotoVerificationTable);
                                    getPhotoVerifImages(paymentPhotoVerificationTable);
                                }

                                loadPhoto();
                            }
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void loadPhoto() {
        List<PaymentPhotoVerificationTable> paymentPhotoVerificationTables = paymentPhotoVerificationTableQueries.loadAllPaymentPhotoVerif(paymentId);
        paymentRecyclerAdapter = new PaymentPhotoRecyclerAdapter(paymentPhotoVerificationTables);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        paymentPictureRecyclerView.setLayoutManager(layoutManager);
        paymentPictureRecyclerView.setAdapter(paymentRecyclerAdapter);
    }

    private void getPhotoVerifImages(PaymentPhotoVerificationTable paymentPhotoVerificationTable) {
        final PaymentPhotoVerificationTable paymentPhotoTable = paymentPhotoVerificationTableQueries.loadSinglePaymentPhotoVerif(paymentPhotoVerificationTable.getPaymentVerificationPhotoId());
        BitmapUtil.getImageWithGlide(this, paymentPhotoTable.getImageUri())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        saveImage(resource, paymentPhotoTable);
                    }
                });
    }


    private void saveImage(Bitmap resource, PaymentPhotoVerificationTable paymentPhotoTable) {
        byte[] bytes = BitmapUtil.getBytesFromBitmapInJPG(resource, 100);
        paymentPhotoTable.setImageByteArray(bytes);
        paymentPhotoVerificationTableQueries.updatePhotoVerif(paymentPhotoTable);

        Log.d(TAG, "saveImage: payment image byte[] saved");
    }

    private void savePhotoVerifToStorage(PaymentPhotoVerificationTable paymentPhotoVerificationTable) {
        PaymentPhotoVerificationTable paymentPhotoVerificationTable1 = paymentPhotoVerificationTableQueries.loadSinglePaymentPhotoVerif(paymentPhotoVerificationTable.getPaymentVerificationPhotoId());
        if(paymentPhotoVerificationTable1 == null) paymentPhotoVerificationTableQueries.insertPaymentPhotoVerifToStorage(paymentPhotoVerificationTable);
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
