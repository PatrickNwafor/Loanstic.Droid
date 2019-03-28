package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.LifeGoals.LifeGoalsSetup1GoalName;
import com.icubed.loansticdroid.adapters.SavingsPlanTypeRecyclerAdapter;
import com.icubed.loansticdroid.adapters.SavingsRecyclerAdapter;
import com.icubed.loansticdroid.cloudqueries.SavingsPlanTypeQueries;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.localdatabase.SavingsPlanTypeTable;
import com.icubed.loansticdroid.localdatabase.SavingsPlanTypeTableQueries;
import com.icubed.loansticdroid.localdatabase.SavingsTable;
import com.icubed.loansticdroid.util.BitmapUtil;

import java.util.Date;
import java.util.List;

public class SavingsPickPlan extends AppCompatActivity {

    private MenuItem register;
    private static final String TAG = ".SavingsPickPlan";
    private Toolbar toolbar;
    private RecyclerView savingsPlanRecyclerView;
    private SavingsPlanTypeRecyclerAdapter savingsPlanRecyclerAdapter;
    private SavingsPlanTypeQueries savingsPlanTypeQueries;
    private ProgressBar progressBar;
    private SavingsPlanTypeTableQueries savingsPlanTypeTableQueries;
    private List<SavingsPlanTypeTable> currentLoanTable;
    public SavingsPlanTypeTable selectedSavingsPlanTypeTable = null;
    public SavingsTable savingsTable;
    // public ImageView lastCheck = null;
    public LottieAnimationView lastCheck = null;

    private BorrowersTable borrower;
    private GroupBorrowerTable group;

    private CardView otherLoanCard;
    // public ImageView otherLoanCheck;
    public LottieAnimationView otherLoanCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_plan);

        savingsPlanTypeQueries = new SavingsPlanTypeQueries();
        savingsPlanTypeTableQueries = new SavingsPlanTypeTableQueries(getApplication());

        currentLoanTable = savingsPlanTypeTableQueries.loadAllSavingsPlanTypes();

        toolbar = findViewById(R.id.select_loan_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select Savings Plan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        borrower = getIntent().getParcelableExtra("borrower");
        savingsTable = getIntent().getParcelableExtra("savings");

        progressBar = findViewById(R.id.loan_types_progress_bar);
        otherLoanCard = findViewById(R.id.other_loan_card);
        otherLoanCheck = findViewById(R.id.other_loan_check);
        savingsPlanRecyclerView = findViewById(R.id.loan_types_list);

        otherLoanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOtherLoan();
            }
        });

        if(!doesSavingsPlanExistInLocalStorage()) {
            getSavingsPlans();
        }else{
            loadSavingsPlansToUI();
            getNewSavingsPlans();
        }
    }

    private void selectOtherLoan() {
        if(otherLoanCheck.getVisibility() == View.GONE){

            if(lastCheck != null){
                lastCheck.setVisibility(View.GONE);
            }

            otherLoanCheck.setVisibility(View.VISIBLE);
            otherLoanCheck.playAnimation();
            lastCheck = otherLoanCheck;
            selectedSavingsPlanTypeTable = new SavingsPlanTypeTable();
            selectedSavingsPlanTypeTable.setSavingsTypeName("Other Savings Plan");
            selectedSavingsPlanTypeTable.setLastUpdatedAt(new Date());
            selectedSavingsPlanTypeTable.setTimestamp(new Date());
            invalidateOptionsMenu();
        }else{
            lastCheck = null;
            selectedSavingsPlanTypeTable = null;
            otherLoanCheck.setVisibility(View.GONE);
            invalidateOptionsMenu();
        }
    }

    private void getNewSavingsPlans() {
        savingsPlanTypeQueries.retrieveAllSavingsPlanType()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){

                                Boolean isThereNewData = false;
                                List<SavingsPlanTypeTable> loanInStorage = currentLoanTable;
                                for(DocumentSnapshot doc : task.getResult().getDocuments()) {

                                    Boolean doesDataExist = false;
                                    for (SavingsPlanTypeTable savingsPlan : currentLoanTable) {
                                        if (savingsPlan.getSavingsPlanTypeId().equals(doc.getId())) {
                                            doesDataExist = true;
                                            loanInStorage.remove(savingsPlan);
                                            Log.d(TAG, "onComplete: Loan Type id of " + doc.getId() + " already exist");
                                            break;
                                        }
                                    }

                                    if (!doesDataExist) {
                                        Log.d(TAG, "onComplete: Loan type id of " + doc.getId() + " does not exist");

                                        SavingsPlanTypeTable savingsPlanTypeTable = doc.toObject(SavingsPlanTypeTable.class);
                                        savingsPlanTypeTable.setSavingsPlanTypeId(doc.getId());
                                        isThereNewData = true;

                                        saveSavingsPlans(savingsPlanTypeTable);
                                    }else{
                                        //Update local table if any changes
                                        updateTable(doc);
                                    }
                                }

                                //to delete deleted borrower in cloud from storage
                                if(!loanInStorage.isEmpty()){
                                    for(SavingsPlanTypeTable savingsPlan : loanInStorage){
                                        deleteBorrowerFromLocalStorage(savingsPlan);
                                        Log.d("Delete", "deleted "+savingsPlan.getSavingsPlanTypeId()+ " from storage");
                                    }
                                }

                                if(isThereNewData || !loanInStorage.isEmpty()) {
                                    loadSavingsPlansToUI();
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

    private void deleteBorrowerFromLocalStorage(SavingsPlanTypeTable savingsPlan) {
        savingsPlanTypeTableQueries.deleteSavingsPlanType(savingsPlan);
    }

    private void updateTable(DocumentSnapshot doc) {
        SavingsPlanTypeTable savingsPlanTypeTable = doc.toObject(SavingsPlanTypeTable.class);
        savingsPlanTypeTable.setSavingsPlanTypeId(doc.getId());

        SavingsPlanTypeTable saved = savingsPlanTypeTableQueries.loadSingleSavingsPlanType(doc.getId());
        savingsPlanTypeTable.setId(saved.getId());

        if(savingsPlanTypeTable.getLastUpdatedAt().getTime() != saved.getLastUpdatedAt().getTime()){

            savingsPlanTypeTableQueries.updateSavingsPlanTypeDetails(savingsPlanTypeTable);
            loadSavingsPlansToUI();
            Log.d("SavingsPlan", "Loan Type Detailed updated");
        }

    }

    private boolean doesSavingsPlanExistInLocalStorage(){
        List<SavingsPlanTypeTable> savingsPlanTypeTable = savingsPlanTypeTableQueries.loadAllSavingsPlanTypes();
        return !savingsPlanTypeTable.isEmpty();
    }

    private void getSavingsPlans(){
        savingsPlanTypeQueries.retrieveAllSavingsPlanType()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()) {
                                for (DocumentSnapshot doc : task.getResult()) {
                                    SavingsPlanTypeTable savingsPlanTypeTable = doc.toObject(SavingsPlanTypeTable.class);
                                    savingsPlanTypeTable.setSavingsPlanTypeId(doc.getId());

                                    saveSavingsPlans(savingsPlanTypeTable);
                                }
                                loadSavingsPlansToUI();
                            }else{
                                hideProgressBar();
                                Toast.makeText(SavingsPickPlan.this, "No available loan types", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(SavingsPickPlan.this, "Failed to retrieve loan types", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                            hideProgressBar();
                        }
                    }
                });
    }

    private void saveSavingsPlans(SavingsPlanTypeTable savingsPlanTypeTable){
        savingsPlanTypeTableQueries.insertSavingsPlanTypeToStorage(savingsPlanTypeTable);
    }

    private void loadSavingsPlansToUI(){
        List<SavingsPlanTypeTable> savingsPlanTypeTable = savingsPlanTypeTableQueries.loadAllSavingsPlanTypesOrderByName();

        savingsPlanRecyclerAdapter = new SavingsPlanTypeRecyclerAdapter(savingsPlanTypeTable);
        savingsPlanRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        savingsPlanRecyclerView.setAdapter(savingsPlanRecyclerAdapter);

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
                startAnotherActivity(LifeGoalsSetup1GoalName.class);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem register = menu.findItem(R.id.next_to_loan_terms);

        if(selectedSavingsPlanTypeTable != null || otherLoanCheck.getVisibility() == View.VISIBLE){
            register.setVisible(true);
        }else{
            register.setVisible(false);
        }

        return true;
    }

    private void startAnotherActivity(Class newActivity){
        Intent newActivityIntent = new Intent(this, newActivity);
        newActivityIntent.putExtra("savings_type", selectedSavingsPlanTypeTable);
        //newActivityIntent.putExtra("borrower", borrower);
        //newActivityIntent.putExtra("savings", savingsTable);
        startActivity(newActivityIntent);
    }

    public void getImage(final SavingsPlanTypeTable savingsPlanTypeTable){

        for (SavingsPlanTypeTable typeTable : currentLoanTable) {
            if(typeTable.getSavingsPlanTypeId().equals(savingsPlanTypeTable.getSavingsPlanTypeId())
                    && typeTable.getSavingsTypeImageByteArray() == null){

                BitmapUtil.getImageWithGlide(getApplicationContext(), savingsPlanTypeTable.getSavingsTypeImageUri())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                saveImage(resource, savingsPlanTypeTable);
                            }
                        });
                return;

            }else if(typeTable.getSavingsPlanTypeId().equals(savingsPlanTypeTable.getSavingsPlanTypeId()) &&
                    typeTable.getSavingsTypeImageByteArray() != null &&
                    !typeTable.getSavingsTypeImageUri().equals(savingsPlanTypeTable.getSavingsTypeImageUri())){

                BitmapUtil.getImageWithGlide(getApplicationContext(), savingsPlanTypeTable.getSavingsTypeImageUri())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                saveImage(resource, savingsPlanTypeTable);
                            }
                        });
                return;
            }
        }

    }


    public void saveImage(Bitmap bitmap, SavingsPlanTypeTable savingsPlanTypeTable){
        byte[] bytes = BitmapUtil.getBytesFromBitmapInPNG(bitmap, 100);

        SavingsPlanTypeTable currentlySaved = savingsPlanTypeTableQueries.loadSingleSavingsPlanType(savingsPlanTypeTable.getSavingsPlanTypeId());
        currentlySaved.setSavingsTypeImageByteArray(bytes);
        savingsPlanTypeTableQueries.updateSavingsPlanTypeDetails(currentlySaved);
        Log.d(TAG, "saveImage: savings type image byte[] saved");
    }
}
