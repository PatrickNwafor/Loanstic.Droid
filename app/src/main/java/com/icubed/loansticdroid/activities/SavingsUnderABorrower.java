package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.SavingsRecyclerAdapter;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.cloudqueries.SavingsPlanTypeQueries;
import com.icubed.loansticdroid.cloudqueries.SavingsQueries;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.BorrowersTableQueries;
import com.icubed.loansticdroid.localdatabase.SavingsPlanTypeTable;
import com.icubed.loansticdroid.localdatabase.SavingsPlanTypeTableQueries;
import com.icubed.loansticdroid.localdatabase.SavingsTable;
import com.icubed.loansticdroid.localdatabase.SavingsTableQueries;
import com.icubed.loansticdroid.models.SavingsDetails;
import com.icubed.loansticdroid.util.AndroidUtils;
import com.icubed.loansticdroid.util.BitmapUtil;
import com.icubed.loansticdroid.util.EditTextExtension.CustomEditText;
import com.icubed.loansticdroid.util.KeyboardUtil;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class SavingsUnderABorrower extends AppCompatActivity {

    private BorrowersTable borrowersTable;
    RecyclerView recyclerView;
    public SwipeRefreshLayout swipeRefreshLayout;
    public LinearLayout emptyCollection;
    public List<SavingsDetails> savingsDetailsList;
    public SavingsRecyclerAdapter savingsRecyclerAdapter;
    public ProgressBar progressBar;
    private SavingsQueries savingsQueries;
    private SavingsTableQueries savingsTableQueries;
    private int count = 0;
    private int docSize = 0;
    private int secondCount = 0;
    private int size = 0;
    private SavingsPlanTypeQueries savingsPlanTypeQueries;
    private SavingsPlanTypeTableQueries savingsPlanTypeTableQueries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings_under_a_borrower);

        borrowersTable = getIntent().getParcelableExtra("borrower");

        Toolbar toolbar = findViewById(R.id.borrower_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(borrowersTable.getLastName() + " " + borrowersTable.getFirstName());
        getSupportActionBar().setSubtitle("Savings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.savings_list);
        emptyCollection = findViewById(R.id.search_empty_layout);
        progressBar = findViewById(R.id.progressBar);

        savingsQueries = new SavingsQueries();
        savingsTableQueries = new SavingsTableQueries(getApplication());
        savingsPlanTypeQueries = new SavingsPlanTypeQueries();
        savingsPlanTypeTableQueries = new SavingsPlanTypeTableQueries(getApplication());

        //Swipe down refresher initialization
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_dark,
                R.color.colorAccent);
        swipeRefreshListener();

        savingsDetailsList = new ArrayList<>();
        savingsRecyclerAdapter = new SavingsRecyclerAdapter(savingsDetailsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter((savingsRecyclerAdapter));

        getAllSavings();
    }

    public Boolean doesSavingsTableExistInLocalStorage(){
        List<SavingsTable> savingsTable = savingsTableQueries.loadSavingsForBorrowerOrderByCreationDate(borrowersTable.getBorrowersId());
        return !savingsTable.isEmpty();
    }

    /**
     * this method retrieves all savingss from the cloud
     * @// TODO: 09/02/2019 remember to later change to savings query to have a limit of data returned at once and add lazy loading features to the recycler view
     */
    public void loadAllSavings(){

        savingsQueries.retrieveSavingsForBorrower(borrowersTable.getBorrowersId())
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){

                                docSize = task.getResult().size();
                                count = 0;

                                for(DocumentSnapshot doc : task.getResult().getDocuments()){
                                    SavingsTable savingsTable = doc.toObject(SavingsTable.class);
                                    savingsTable.setSavingsId(doc.getId());

                                    saveSavingsToLocalStorage(savingsTable);
                                    checkSavingsType(savingsTable);
                                }
                            }else{
                                removeRefresher();
                                progressBar.setVisibility(View.GONE);
                                emptyCollection.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), "Document is empty", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void checkSavingsType(SavingsTable savingsTable){
        if(savingsTable.getSavingsPlanTypeId() != null){

            SavingsPlanTypeTable savingsPlanTypeTable = savingsPlanTypeTableQueries.loadSingleSavingsPlanType(savingsTable.getSavingsPlanTypeId());
            if(savingsPlanTypeTable == null) {
                getNewSavingsType(savingsTable);
            }else{
                count++;
                if (count == docSize) {
                    loadSavingsToUI();
                }
            }
        }else {
            count++;
            if (count == docSize) {
                loadSavingsToUI();
            }
        }
    }

    private void getNewSavingsType(final SavingsTable savingsTable) {
        savingsPlanTypeQueries.retrieveSingleSavingsPlanType(savingsTable.getSavingsPlanTypeId())
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            SavingsPlanTypeTable savingsPlanTypeTable = task.getResult().toObject(SavingsPlanTypeTable.class);
                            savingsPlanTypeTable.setSavingsPlanTypeId(task.getResult().getId());

                            saveTypeToLocal(savingsPlanTypeTable);
                            count++;
                            if(count == docSize) {
                                loadSavingsToUI();
                            }
                        }else {
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void saveTypeToLocal(SavingsPlanTypeTable savingsPlanTypeTable) {
        SavingsPlanTypeTable savingsPlanTypeTable1 = savingsPlanTypeTableQueries.loadSingleSavingsPlanType(savingsPlanTypeTable.getSavingsPlanTypeId());
        if(savingsPlanTypeTable1 == null) savingsPlanTypeTableQueries.insertSavingsPlanTypeToStorage(savingsPlanTypeTable);
    }

    /**
     * save savingss to savings storage
     * @param savingsTable
     */
    private void saveSavingsToLocalStorage(SavingsTable savingsTable) {
        SavingsTable savingsTables = savingsTableQueries.loadSingleSavings(savingsTable.getSavingsId());
        if(savingsTables == null) savingsTableQueries.insertSavingsToStorage(savingsTable);
    }

    /**
     * loads all our savings data to UI for the user to see
     */
    public void loadSavingsToUI(){
        List<SavingsTable> savingsTable = savingsTableQueries.loadSavingsForBorrowerOrderByCreationDate(borrowersTable.getBorrowersId());

        savingsDetailsList.clear();
        size = savingsTable.size();

        emptyCollection.setVisibility(View.GONE);
        for(SavingsTable table : savingsTable){

            SavingsDetails savingsDetails = new SavingsDetails();
            savingsDetails.setBorrowersTable(borrowersTable);
            savingsDetails.setSavingsTable(table);

            if(table.getSavingsPlanTypeId() == null) savingsDetails.setSavingsPlanTypeTable(null);
            else{
                SavingsPlanTypeTable savingsPlanTypeTable = savingsPlanTypeTableQueries.loadSingleSavingsPlanType(table.getSavingsPlanTypeId());
                savingsDetails.setSavingsPlanTypeTable(savingsPlanTypeTable);
            }

            savingsDetailsList.add(savingsDetails);
            secondCount++;
            savingsRecyclerAdapter.notifyDataSetChanged();
        }

        if(secondCount == size) progressBar.setVisibility(View.GONE);
    }

    /**
     * this method is called if savingss already exist in local storage
     * this method helps to get data from cloud storage and compare to local storage to see if there is any update or an entirely new data has been created
     */
    public void loadAllSavingssAndCompareToLocal() {
        final List<SavingsTable> savingsList = savingsTableQueries.loadAllSavings();

        savingsQueries.retrieveAllSavings()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){

                                docSize = task.getResult().size();
                                count = 0;

                                List<SavingsTable> savingssInStorageToRemove = new ArrayList<>();
                                for(DocumentSnapshot doc : task.getResult().getDocuments()) {

                                    Boolean doesDataExist = false;
                                    for (SavingsTable savingsTab : savingsList) {
                                        if (savingsTab.getSavingsId().equals(doc.getId())) {
                                            doesDataExist = true;
                                            savingssInStorageToRemove.add(savingsTab);
                                            Log.d(TAG, "onComplete: savings id of " + doc.getId() + " already exist");
                                            break;
                                        }
                                    }

                                    if (!doesDataExist) {
                                        Log.d(TAG, "onComplete: savings id of " + doc.getId() + " does not exist");

                                        SavingsTable savingsTable = doc.toObject(SavingsTable.class);
                                        savingsTable.setSavingsId(doc.getId());

                                        saveSavingsToLocalStorage(savingsTable);
                                        checkSavingsType(savingsTable);
                                    } else {
                                        //Update local table if any changes
                                        docSize--;
                                        updateTable(doc);
                                    }
                                }

                                savingsList.removeAll(savingssInStorageToRemove);

                                //to delete deleted borrower in cloud from storage
                                if(!savingsList.isEmpty()){
                                    for(SavingsTable savingsTab : savingsList){
                                        deleteSavingsFromLocalStorage(savingsTab);
                                        Log.d("Delete", "deleted "+savingsTab.getSavingsId()+ " from storage");
                                    }
                                }

                                removeRefresher();
                            }else{
                                Toast.makeText(getApplicationContext(), "Document is empty", Toast.LENGTH_SHORT).show();
                                removeRefresher();
                            }
                        }else{
                            Log.d("Savings", "onComplete: "+task.getException().getMessage());
                            removeRefresher();
                        }
                    }
                });
    }

    /**
     * this method deletes a borrower from local storage
     * @param savingsTable
     */
    private void deleteSavingsFromLocalStorage(SavingsTable savingsTable) {
        savingsTableQueries.deleteSavings(savingsTable);
        loadSavingsToUI();
    }

    /**
     * removes refresher
     */
    private void removeRefresher(){
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.destroyDrawingCache();
        swipeRefreshLayout.clearAnimation();
    }

    /**
     * updates any changes in the savings details from cloud in local storage
     * @param doc
     */
    private void updateTable(DocumentSnapshot doc) {
        SavingsTable savingsTable = doc.toObject(SavingsTable.class);
        savingsTable.setSavingsId(doc.getId());

        SavingsTable currentlySaved = savingsTableQueries.loadSingleSavings(doc.getId());
        savingsTable.setId(currentlySaved.getId());

        if(savingsTable.getLastUpdatedAt().getTime() != currentlySaved.getLastUpdatedAt().getTime()){

            savingsTableQueries.updateSavingsDetails(savingsTable);
            loadSavingsToUI();
            Log.d("Savings", "Savings Detailed updated");

        }
    }

    //Swipe down refresh lstener
    private void swipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(AndroidUtils.isMobileDataEnabled(getApplicationContext())) {
                    swipeRefreshLayout.setRefreshing(true);
                    loadAllSavingssAndCompareToLocal();
                }else {
                    Toast.makeText(getApplicationContext(), "Request failed, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getAllSavings() {
        if(!doesSavingsTableExistInLocalStorage()){
            loadAllSavings();
        }else{
            swipeRefreshLayout.setRefreshing(true);
            loadSavingsToUI();
            loadAllSavingssAndCompareToLocal();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
