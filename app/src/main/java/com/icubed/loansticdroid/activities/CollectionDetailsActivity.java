package com.icubed.loansticdroid.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.models.DueCollectionDetails;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CollectionDetailsActivity extends AppCompatActivity {
    Animation bounce;
    CardView CD1,CD2,CD3,CD4,CD5,CD6;

    private TextView borrowerUsernameView, businessNameView,
            collectionDueDateView, collectionAmountView, collectionNumberView,
            collectionAddressView, collectionStatusView;

    private ImageView borrowerImageView;

    private String borrowerName, businessName, collectionDueDate;
    private double collectionAmount;
    private int colelctionNumber;
    private Boolean collectionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_details);
        bounce = AnimationUtils.loadAnimation( CollectionDetailsActivity.this,R.anim.bounce);
        CD1 = findViewById(R.id.CD1);
        CD2 = findViewById(R.id.CD2);
        CD3 = findViewById(R.id.CD3);
        CD4 = findViewById(R.id.CD4);
        CD5 = findViewById(R.id.CD5);
        CD6 = findViewById(R.id.CD6);

        CD1.setAnimation(bounce);
        CD2.setAnimation(bounce);
        CD3.setAnimation(bounce);
        CD4.setAnimation(bounce);
        CD5.setAnimation(bounce);
        CD6.setAnimation(bounce);

        //TextViews
        borrowerUsernameView = findViewById(R.id.borrower_username);
        businessNameView = findViewById(R.id.business_name);
        collectionDueDateView = findViewById(R.id.collection_due_date);
        collectionAmountView = findViewById(R.id.collection_amount);
        collectionNumberView = findViewById(R.id.collection_number);
        collectionAddressView = findViewById(R.id.collection_address);
        collectionStatusView = findViewById(R.id.collection_status);
        borrowerImageView = findViewById(R.id.borrower_image);

        //getting collection details value
        borrowerName = getIntent().getStringExtra("borrowerName");
        businessName = getIntent().getStringExtra("borrowerJob");
        collectionAmount = getIntent().getDoubleExtra("collectionAmount", 0.0);
        collectionStatus = getIntent().getBooleanExtra("isDueCollected", false);
        collectionDueDate = getIntent().getStringExtra("collectionDueDate");
        colelctionNumber = getIntent().getIntExtra("collectionNumber", 0);


        //Passing them to an object
        DueCollectionDetails dueCollectionDetails = new DueCollectionDetails(borrowerName,businessName,
                collectionDueDate, colelctionNumber,collectionAmount,collectionStatus);

        //UpdatesUI
        updateUI(dueCollectionDetails);

    }

    private void updateUI(DueCollectionDetails dueCollectionDetails) {
        borrowerUsernameView.setText(dueCollectionDetails.getBorrowerName());
        businessNameView.setText(dueCollectionDetails.getBorrowerJob());
        collectionNumberView.setText(String.valueOf(dueCollectionDetails.getCollectionNumber()));

        if(dueCollectionDetails.getDueCollected()){
            collectionStatusView.setText("Collected");
        }else{
            collectionStatusView.setText("Not Collected");
        }

        collectionAmountView.setText(String.valueOf(dueCollectionDetails.getDueAmount()));

        String dueDate = monthYearDate(dueCollectionDetails.getDueCollectionDate());
        collectionDueDateView.setText(dueDate);
    }

    public void backButton(View view) {
        finish();
    }

    /****************Convert date to string format*****************/
    private String monthYearDate(String date){

        SimpleDateFormat month_date = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date newDate = null;
        try {
            newDate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String month_name = month_date.format(newDate);

        return month_name;
    }

    private void loadProfileImage(String profileImageUri){
        Glide.with(this).load(profileImageUri).into(borrowerImageView);
    }
}
