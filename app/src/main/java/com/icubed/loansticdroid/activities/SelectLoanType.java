package com.icubed.loansticdroid.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.LoanTypeRecyclerAdapter;
import com.icubed.loansticdroid.cloudqueries.LoanTypeQueries;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.localdatabase.LoanTypeTable;
import com.icubed.loansticdroid.localdatabase.LoanTypeTableQueries;
import com.icubed.loansticdroid.util.AndroidUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SelectLoanType extends AppCompatActivity {
    private static final String TAG = ".SelectLoanType";
    private Toolbar toolbar;
    private RecyclerView loanTypeRecyclerView;
    private LoanTypeRecyclerAdapter loanTypeRecyclerAdapter;
    private LoanTypeQueries loanTypeQueries;
    private ProgressBar progressBar;
    private LoanTypeTableQueries loanTypeTableQueries;
    private List<LoanTypeTable> currentLoanTable;
    public LoanTypeTable selectedLoanTypeTable = null;
    public ImageView lastCheck = null;

    private BorrowersTable borrower;
    private GroupBorrowerTable group;

    private CardView otherLoanCard;
    public ImageView otherLoanCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_loan_type);

        loanTypeQueries = new LoanTypeQueries();
        loanTypeTableQueries = new LoanTypeTableQueries(getApplication());

        currentLoanTable = loanTypeTableQueries.loadAllLoanTpes();


        toolbar = findViewById(R.id.select_loan_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select loan type");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        borrower = getIntent().getParcelableExtra("borrower");
        group = getIntent().getParcelableExtra("group");

        progressBar = findViewById(R.id.loan_types_progress_bar);
        otherLoanCard = findViewById(R.id.other_loan_card);
        otherLoanCheck = findViewById(R.id.other_loan_check);
        loanTypeRecyclerView = findViewById(R.id.loan_types_list);

        otherLoanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOtherLoan();
            }
        });

        if(!doesLoanTypeExistInLocalStorage()) {
            getLoanTypes();
        }else{
            loadLoanTypesToUI();
            getNewLoanTypes();
        }
    }

    private void selectOtherLoan() {
        if(otherLoanCheck.getVisibility() == View.GONE){

            if(lastCheck != null){
                lastCheck.setVisibility(View.GONE);
            }

            otherLoanCheck.setVisibility(View.VISIBLE);
            lastCheck = otherLoanCheck;
            selectedLoanTypeTable = null;
            invalidateOptionsMenu();
        }else{
            lastCheck = null;
            selectedLoanTypeTable = null;
            otherLoanCheck.setVisibility(View.GONE);
            invalidateOptionsMenu();
        }
    }

    private void getNewLoanTypes() {
        loanTypeQueries.retrieveAllLoanType()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){

                                Boolean isThereNewData = false;
                                List<LoanTypeTable> loanInStorage = currentLoanTable;
                                for(DocumentSnapshot doc : task.getResult().getDocuments()) {

                                    Boolean doesDataExist = false;
                                    for (LoanTypeTable loanType : currentLoanTable) {
                                        if (loanType.getLoanTypeId().equals(doc.getId())) {
                                            doesDataExist = true;
                                            loanInStorage.remove(loanType);
                                            Log.d(TAG, "onComplete: Loan Type id of " + doc.getId() + " already exist");
                                            break;
                                        }
                                    }

                                    if (!doesDataExist) {
                                        Log.d(TAG, "onComplete: Loan type id of " + doc.getId() + " does not exist");

                                        LoanTypeTable loanTypeTable = doc.toObject(LoanTypeTable.class);
                                        loanTypeTable.setLoanTypeId(doc.getId());
                                        isThereNewData = true;

                                        saveLoanTypes(loanTypeTable);
                                    }else{
                                        //Update local table if any changes
                                        updateTable(doc);
                                    }
                                }

                                //to delete deleted borrower in cloud from storage
                                if(!loanInStorage.isEmpty()){
                                    for(LoanTypeTable loanType : loanInStorage){
                                        deleteBorrowerFromLocalStorage(loanType);
                                        Log.d("Delete", "deleted "+loanType.getLoanTypeId()+ " from storage");
                                    }
                                }

                                if(isThereNewData || !loanInStorage.isEmpty()) {
                                    loadLoanTypesToUI();
                                }
                            }else{
                                Toast.makeText(getApplicationContext(), "Document is empty", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Log.d("Borrower", "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void deleteBorrowerFromLocalStorage(LoanTypeTable loanType) {
        loanTypeTableQueries.deleteLoanType(loanType);
    }

    private void updateTable(DocumentSnapshot doc) {
        LoanTypeTable loanTypeTable = doc.toObject(LoanTypeTable.class);
        loanTypeTable.setLoanTypeId(doc.getId());

        LoanTypeTable saved = loanTypeTableQueries.loadSingleLoanType(doc.getId());
        loanTypeTable.setId(saved.getId());

        if(!saved.getLoanTypeName().equals(loanTypeTable.getLoanTypeName()) ||
                !saved.getLoanTypeDescription().equals(loanTypeTable.getLoanTypeDescription()) ||
                !saved.getLoanTypeImageUri().equals(loanTypeTable.getLoanTypeImageUri())){

            loanTypeTableQueries.updateLoanTypeDetails(loanTypeTable);
            loadLoanTypesToUI();
            Log.d("LoanType", "Loan Type Detailed updated");
        }

    }

    private boolean doesLoanTypeExistInLocalStorage(){
        List<LoanTypeTable> loanTypeTable = loanTypeTableQueries.loadAllLoanTpes();
        return !loanTypeTable.isEmpty();
    }

    private void getLoanTypes(){
        loanTypeQueries.retrieveAllLoanType()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()) {
                                for (DocumentSnapshot doc : task.getResult()) {
                                    LoanTypeTable loanTypeTable = doc.toObject(LoanTypeTable.class);
                                    loanTypeTable.setLoanTypeId(doc.getId());

                                    saveLoanTypes(loanTypeTable);
                                }
                                loadLoanTypesToUI();
                            }else{
                                hideProgressBar();
                                Toast.makeText(SelectLoanType.this, "No available loan types", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(SelectLoanType.this, "Failed to retrieve loan types", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                            hideProgressBar();
                        }
                    }
                });
    }

    private void saveLoanTypes(LoanTypeTable loanTypeTable){
        loanTypeTableQueries.insertLoanTypeToStorage(loanTypeTable);
    }

    private void loadLoanTypesToUI(){
        List<LoanTypeTable> loanTypeTable = loanTypeTableQueries.loadAllLoanTypesOrderByName();

        loanTypeRecyclerAdapter = new LoanTypeRecyclerAdapter(loanTypeTable);
        loanTypeRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        loanTypeRecyclerView.setAdapter(loanTypeRecyclerAdapter);

        hideProgressBar();

    }

    private void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.select_loan_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.next_to_loan_terms:
                   startAnotherActivity(LoanTerms.class);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem register = menu.findItem(R.id.next_to_loan_terms);

        if(selectedLoanTypeTable != null || otherLoanCheck.getVisibility() == View.VISIBLE){
            register.setVisible(true);
        }else{
            register.setVisible(false);
        }

        return true;
    }

    private void startAnotherActivity(Class newActivity){
        Intent newActivityIntent = new Intent(this, newActivity);
        newActivityIntent.putExtra("loan_type", selectedLoanTypeTable);
        if(borrower != null){
            newActivityIntent.putExtra("borrower", borrower);
        }else{
            newActivityIntent.putExtra("group", group);
        }
        startActivity(newActivityIntent);
    }

    public void getImage(final LoanTypeTable loanTypeTable){

        for (LoanTypeTable typeTable : currentLoanTable) {
            if(typeTable.getLoanTypeId().equals(loanTypeTable.getLoanTypeId())
                    && typeTable.getLoanTypeImageByteArray() == null){
                Glide.with(this)
                        .asBitmap()
                        .load(loanTypeTable.getLoanTypeImageUri())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                saveImage(resource, loanTypeTable);
                            }
                        });
                return;
            }
        }

    }

    public void saveImage(Bitmap bitmap, LoanTypeTable loanTypeTable){
        byte[] bytes = AndroidUtils.getBytesFromBitmap(bitmap);

        LoanTypeTable currentlySaved = loanTypeTableQueries.loadSingleLoanType(loanTypeTable.getLoanTypeId());
        currentlySaved.setLoanTypeImageByteArray(bytes);
        loanTypeTableQueries.updateLoanTypeDetails(currentlySaved);
    }
}
