package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.BorrowerBusinessVerificationRecyclerAdapter;
import com.icubed.loansticdroid.adapters.DocumentRecyclerAdapter;
import com.icubed.loansticdroid.cloudqueries.BorrowerFilesQueries;
import com.icubed.loansticdroid.cloudqueries.BorrowerPhotoValidationQueries;
import com.icubed.loansticdroid.localdatabase.BorrowerFilesTable;
import com.icubed.loansticdroid.localdatabase.BorrowerPhotoValidationTable;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.GroupPhotoValidationTable;

import java.util.ArrayList;
import java.util.List;

public class BorrowerDetailsSingle extends AppCompatActivity {
    private static final String TAG = ".BorrowerDetailsSingle";
    private Toolbar toolbar;
    private BorrowersTable borrower;
    private ImageView profileImageView;
    private TextView nameTextView, numberTextView, emailTextView, numberOfBizVerifTextView
            , businessNameTextView, businessLocationTextView, businessDescriptionTextView
            , genderTextView, dobTextView, homeAddressTextView, countryTextView
            , stateTextView, cityTextView, numberOfDocTextView, borrowerLocationTextView;

    private RecyclerView docRecyclerView;
    private RecyclerView businessVerificationRecyclerView;
    private DocumentRecyclerAdapter documentRecyclerAdapter;
    private List<BorrowerFilesTable> borrowerFilesTables;
    private List<BorrowerPhotoValidationTable> borrowerPhotoValidationTables;
    private BorrowerBusinessVerificationRecyclerAdapter borrowerBusinessVerificationRecyclerAdapter;
    private BorrowerFilesQueries borrowerFilesQueries;
    private BorrowerPhotoValidationQueries borrowerPhotoValidationQueries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrower_details_single);

        toolbar = findViewById(R.id.borrower_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Borrower profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        
        borrower = getIntent().getParcelableExtra("borrower");
        Log.d(TAG, "onCreate: "+borrower.toString());

        borrowerFilesQueries = new BorrowerFilesQueries(this);
        borrowerPhotoValidationQueries = new BorrowerPhotoValidationQueries(this);

        profileImageView = findViewById(R.id.profileImageView);
        nameTextView = findViewById(R.id.name);
        numberTextView = findViewById(R.id.mobile_number);
        emailTextView = findViewById(R.id.email_ad);
        businessNameTextView = findViewById(R.id.business_name);
        businessLocationTextView = findViewById(R.id.business_location);
        businessDescriptionTextView = findViewById(R.id.business_description);
        genderTextView = findViewById(R.id.gender);
        dobTextView = findViewById(R.id.dob);
        homeAddressTextView = findViewById(R.id.home_adress);
        countryTextView = findViewById(R.id.country);
        stateTextView = findViewById(R.id.state);
        cityTextView = findViewById(R.id.city);
        numberOfBizVerifTextView = findViewById(R.id.number_of_biz_verif);
        businessVerificationRecyclerView = findViewById(R.id.business_verif_recycler_view);
        numberOfDocTextView = findViewById(R.id.number_of_documents);
        docRecyclerView = findViewById(R.id.documentRecyclerView);
        borrowerLocationTextView = findViewById(R.id.borrower_location);

        borrowerLocationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                intent.putExtra("borrower", borrower);
                startActivity(intent);
            }
        });

        borrowerFilesTables = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        docRecyclerView.setLayoutManager(layoutManager);
        documentRecyclerAdapter = new DocumentRecyclerAdapter(borrowerFilesTables);
        docRecyclerView.setAdapter(documentRecyclerAdapter);
        getFiles();

        borrowerPhotoValidationTables = new ArrayList<>();
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        businessVerificationRecyclerView.setLayoutManager(layoutManager2);
        borrowerBusinessVerificationRecyclerAdapter = new BorrowerBusinessVerificationRecyclerAdapter(borrowerPhotoValidationTables);
        businessVerificationRecyclerView.setAdapter(borrowerBusinessVerificationRecyclerAdapter);
        getBusinessVerificationPhotos();

        setBorrowerDetailsOnUi();

    }

    private void getBusinessVerificationPhotos() {
        borrowerPhotoValidationQueries.retrieveAllValidationPhotosForBorrower(borrower.getBorrowersId())
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){

                                numberOfBizVerifTextView.setText(String.valueOf(task.getResult().size()));

                                for(DocumentSnapshot doc : task.getResult()){

                                    BorrowerPhotoValidationTable borrowerPhotoValidationTable = doc.toObject(BorrowerPhotoValidationTable.class);
                                    borrowerPhotoValidationTable.setBorrowerPhotoValidationId(doc.getId());

                                    borrowerPhotoValidationTables.add(borrowerPhotoValidationTable);
                                    borrowerBusinessVerificationRecyclerAdapter.notifyDataSetChanged();

                                }

                            }else{
                                Toast.makeText(getApplicationContext(), "No business verification photo uploaded", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "Failed to retrieve business verification photos", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void getFiles() {
        borrowerFilesQueries.retrieveFilesFromCloud(borrower.getBorrowersId())
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            if(!task.getResult().isEmpty()){

                                Log.d(TAG, "onComplete: "+task.getResult().size());

                                numberOfDocTextView.setText(String.valueOf(task.getResult().size()));

                                for(DocumentSnapshot doc : task.getResult()){
                                    BorrowerFilesTable borrowerFilesTable = doc.toObject(BorrowerFilesTable.class);
                                    borrowerFilesTable.setFilesId(doc.getId());

                                    borrowerFilesTables.add(borrowerFilesTable);
                                    documentRecyclerAdapter.notifyDataSetChanged();
                                }
                            }else{
                                Toast.makeText(BorrowerDetailsSingle.this, "No file saved", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(BorrowerDetailsSingle.this, "Unable to retrieve borrower files", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void setBorrowerDetailsOnUi() {

        Glide.with(this)
                .load(borrower.getProfileImageUri())
                .thumbnail(
                Glide.with(this)
                        .load(borrower.getProfileImageThumbUri())
        ).into(profileImageView);

        nameTextView.setText(borrower.getLastName()+" "+borrower.getMiddleName()+" "+borrower.getFirstName());
        numberTextView.setText(String.valueOf(borrower.getPhoneNumber()));
        emailTextView.setText(borrower.getEmail());
        businessNameTextView.setText(borrower.getBusinessName());
        businessLocationTextView.setText(borrower.getWorkAddress());
        businessDescriptionTextView.setText(borrower.getBusinessDescription());
        genderTextView.setText(borrower.getSex());
        dobTextView.setText(borrower.getDateOfBirth());
        homeAddressTextView.setText(borrower.getHomeAddress());
        countryTextView.setText(borrower.getNationality());
        stateTextView.setText(borrower.getState());
        cityTextView.setText(borrower.getCity());
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
