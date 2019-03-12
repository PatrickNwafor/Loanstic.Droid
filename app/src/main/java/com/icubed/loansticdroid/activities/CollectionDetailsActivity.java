package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.localdatabase.CollectionTable;
import com.icubed.loansticdroid.models.DueCollectionDetails;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CollectionDetailsActivity extends AppCompatActivity {
    private static final String TAG = ".CollectionDetActivity";
    Animation bounce;
    CardView CD1,CD2,CD3,CD4,CD5,CD6;

    private TextView businessNameView,
            collectionDueDateView, collectionAmountView, collectionNumberView,
            collectionAddressView, collectionStatusView;


    private ImageView iconUser;
    private TextView userNameTextView;
    private CollectionTable collectionTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_details);

        Toolbar toolbar = findViewById(R.id.collection_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("DueCollection Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        bounce = AnimationUtils.loadAnimation( CollectionDetailsActivity.this,R.anim.bounce);
        CD1 = findViewById(R.id.CD1);
        CD2 = findViewById(R.id.CD2);
        CD3 = findViewById(R.id.CD3);
        CD4 = findViewById(R.id.CD4);
        CD5 = findViewById(R.id.CD5);
        CD6 = findViewById(R.id.CD6);
        CardView CD7 = findViewById(R.id.CD7);

        CD7.setAnimation(bounce);
        CD1.setAnimation(bounce);
        CD2.setAnimation(bounce);
        CD3.setAnimation(bounce);
        CD4.setAnimation(bounce);
        CD5.setAnimation(bounce);
        CD6.setAnimation(bounce);

        //TextViews
        businessNameView = findViewById(R.id.business_name);
        collectionDueDateView = findViewById(R.id.collection_due_date);
        collectionAmountView = findViewById(R.id.collection_amount);
        collectionNumberView = findViewById(R.id.collection_number);
        collectionAddressView = findViewById(R.id.collection_address);
        collectionStatusView = findViewById(R.id.collection_status);
        iconUser = findViewById(R.id.icon_user);
        userNameTextView = findViewById(R.id.user_name);

        //getting collection details value
        DueCollectionDetails dueCollectionDetails = getIntent().getParcelableExtra("dueCollectionDetails");
        collectionTable = getIntent().getParcelableExtra("collection");
        Date lastUpdatedAt = (Date) getIntent().getSerializableExtra("lastUpdatedAt");
        Date timestamp = (Date) getIntent().getSerializableExtra("timestamp");
        Date dueDate = (Date) getIntent().getSerializableExtra("dueDate");

        collectionTable.setLastUpdatedAt(lastUpdatedAt);
        collectionTable.setCollectionDueDate(dueDate);
        collectionTable.setTimestamp(timestamp);

        Log.d(TAG, "onCreate: "+collectionTable.toString());

        //UpdatesUI
        //Log.d(TAG, "onCreate: "+ dueCollectionDetails.toString());
        updateUI(dueCollectionDetails);

    }

    private void updateUI(DueCollectionDetails dueCollectionDetails) {
        if(dueCollectionDetails.getGroupName() == null) {
            userNameTextView.setText(dueCollectionDetails.getLastName() + " " + dueCollectionDetails.getFirstName());
            businessNameView.setText(dueCollectionDetails.getBusinessName());
        }else {
            userNameTextView.setText(dueCollectionDetails.getGroupName());
            iconUser.setImageResource(R.drawable.new_group);
            CD1.setVisibility(View.GONE);
        }

        collectionNumberView.setText(String.valueOf(dueCollectionDetails.getCollectionNumber()));
        collectionAddressView.setText(dueCollectionDetails.getWorkAddress());

        collectionStatusView.setText(collectionTable.getCollectionState());

        collectionAmountView.setText(String.valueOf(dueCollectionDetails.getDueAmount() - dueCollectionDetails.getAmountPaid()));

        String dueDate = monthYearDate(dueCollectionDetails.getDueCollectionDate());
        collectionDueDateView.setText(dueDate);
    }

    /****************Convert date to string format*****************/
    private String monthYearDate(String date){

        SimpleDateFormat month_date = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

        Date newDate = null;
        try {
            newDate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return month_date.format(newDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.repayment_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem register = menu.findItem(R.id.add_repayment);

        register.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getApplicationContext(), LoanRepayment.class);
                intent.putExtra("collection", collectionTable);
                intent.putExtra("lastUpdatedAt", collectionTable.getLastUpdatedAt());
                intent.putExtra("dueDate", collectionTable.getCollectionDueDate());
                intent.putExtra("timestamp", collectionTable.getTimestamp());
                startActivity(intent);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.add_repayment:
                //startAnotherActivity(RepaymentActivity.class);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
