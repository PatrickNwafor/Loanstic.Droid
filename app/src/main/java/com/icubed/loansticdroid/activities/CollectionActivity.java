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
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.CollectionLoanRecyclerAdapter;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.cloudqueries.GroupBorrowerQueries;
import com.icubed.loansticdroid.cloudqueries.LoansQueries;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.BorrowersTableQueries;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTableQueries;
import com.icubed.loansticdroid.localdatabase.LoanTableQueries;
import com.icubed.loansticdroid.localdatabase.LoansTable;
import com.icubed.loansticdroid.models.LoanDetails;
import com.icubed.loansticdroid.util.AndroidUtils;
import com.icubed.loansticdroid.util.BitmapUtil;
import com.icubed.loansticdroid.util.EditTextExtension.CustomEditText;
import com.icubed.loansticdroid.util.EditTextExtension.DrawableClickListener;
import com.icubed.loansticdroid.util.KeyboardUtil;
import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class CollectionActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<LoanDetails> loanDetailsList;
    private CollectionLoanRecyclerAdapter loanRecyclerAdapter;
    private LoansQueries loansQueries;
    private LoanTableQueries loanTableQueries;
    private BorrowersQueries borrowersQueries;
    private BorrowersTableQueries borrowersTableQueries;
    private GroupBorrowerQueries groupBorrowerQueries;
    private GroupBorrowerTableQueries groupBorrowerTableQueries;
    private int count = 0;
    private int docSize = 0;
    private int secondCount = 0;
    private int size = 0;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout emptyCollection;
    private ProgressBar progressBar;
    private CustomEditText searchLoanEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        toolbar = findViewById(R.id.collection_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Loans");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loansQueries = new LoansQueries();
        loanTableQueries = new LoanTableQueries(getApplication());
        borrowersQueries = new BorrowersQueries(this);
        borrowersTableQueries = new BorrowersTableQueries(getApplication());
        groupBorrowerQueries = new GroupBorrowerQueries();
        groupBorrowerTableQueries = new GroupBorrowerTableQueries(getApplication());

        emptyCollection = findViewById(R.id.search_empty_layout);
        progressBar = findViewById(R.id.progressBar);
        searchLoanEditText = findViewById(R.id.searchEditText);

        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_dark,
                R.color.colorAccent);
        swipeRefreshListener();

        loanDetailsList = new ArrayList<>();
        recyclerView = findViewById(R.id.loan_list);
        loanRecyclerAdapter = new CollectionLoanRecyclerAdapter(loanDetailsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter((loanRecyclerAdapter));

        searchDrawableButtonListener();
        searchLoanEditTextListener();

        getAllLoan();
    }

    private void searchDrawableButtonListener() {
        searchLoanEditText.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case LEFT:
                        KeyboardUtil.hideKeyboard(CollectionActivity.this);
                        searchLoanEditText.setVisibility(View.GONE);
                        break;

                    case RIGHT:
                        searchLoanEditText.setText("");
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void searchLoanEditTextListener() {
        searchLoanEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    if(TextUtils.isEmpty(searchLoanEditText.getText().toString())){
                        return false;
                    }

                    Intent intent = new Intent(CollectionActivity.this, LoanSearchActivity.class);
                    intent.putExtra("search", searchLoanEditText.getText().toString());
                    intent.putExtra("from", "col");
                    startActivity(intent);
                    searchLoanEditText.setText("");
                    searchLoanEditText.setVisibility(View.GONE);
                    return true;
                }
                return false;
            }
        });
    }

    private void getAllLoan() {
        if(!doesLoansTableExistInLocalStorage()){
            loadAllLoan();
        }else{
            swipeRefreshLayout.setRefreshing(true);
            loadLoansToUI();
            loadAllLoansAndCompareToLocal();
        }
    }

    //Swipe down refresh lstener
    private void swipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(AndroidUtils.isMobileDataEnabled(getApplicationContext())) {
                    swipeRefreshLayout.setRefreshing(true);
                    loadAllLoansAndCompareToLocal();
                }else {
                    Toast.makeText(getApplicationContext(), "Request failed, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * checks if loan data exist in local storage
     * @return
     */
    public Boolean doesLoansTableExistInLocalStorage(){
        List<LoansTable> loansTable = loanTableQueries.loadAllLoans();
        return !loansTable.isEmpty();
    }

    /**
     * this method retrieves all loans from the cloud
     * @// TODO: 09/02/2019 remember to later change to loan query to have a limit of data returned at once and add lazy loading features to the recycler view
     */
    public void loadAllLoan(){

        loansQueries.retrieveAllLoans()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){

                                docSize = task.getResult().size();
                                count = 0;

                                for(DocumentSnapshot doc : task.getResult().getDocuments()){
                                    LoansTable loansTable = doc.toObject(LoansTable.class);
                                    loansTable.setLoanId(doc.getId());

                                    saveLoanToLocalStorage(loansTable);
                                    if(loansTable.getBorrowerId() != null) getBorrowerDetails(loansTable.getBorrowerId());
                                    else getGroupDetails(loansTable.getGroupId());
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

    /**
     * this methods checks if group details already exist in local storage
     * if it does exist, it does need to go to cloud to get the data all over again
     * if it does exit it calls up getNewGroupDetails(groupId)
     * @param groupId
     */
    private void getGroupDetails(String groupId) {
        GroupBorrowerTable groupBorrowerTables = groupBorrowerTableQueries.loadSingleBorrowerGroup(groupId);

        if(groupBorrowerTables == null){
            getNewGroupDetails(groupId);
        }else{
            count++;
            if(count == docSize) {
                loadLoansToUI();
            }
        }

    }

    /**
     * this method gets group details from firebase firestore
     * @param groupId
     */
    private void getNewGroupDetails(String groupId) {
        groupBorrowerQueries.retrieveSingleBorrowerGroup(groupId)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            GroupBorrowerTable groupBorrowerTable = task.getResult().toObject(GroupBorrowerTable.class);
                            groupBorrowerTable.setGroupId(task.getResult().getId());

                            saveGroupToLocalStorage(groupBorrowerTable);
                            count++;
                            if(count == docSize) {
                                loadLoansToUI();
                            }
                        }else {
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    /**
     * this saves new group details data to local storage
     * @param groupBorrowerTable
     */
    private void saveGroupToLocalStorage(GroupBorrowerTable groupBorrowerTable) {
        GroupBorrowerTable groupBorrowerTable1 = groupBorrowerTableQueries.loadSingleBorrowerGroup(groupBorrowerTable.getGroupId());
        if(groupBorrowerTable1 == null) groupBorrowerTableQueries.insertGroupToStorage(groupBorrowerTable);
    }

    /**
     * this methods checks if borrower details already exist in local storage
     * if it does exist, it does need to go to cloud to get the data all over again
     * if it does exit it calls up getNewBorrowerDetail(borrowerId)
     * @param borrowerId
     */
    private void getBorrowerDetails(String borrowerId) {
        BorrowersTable borrowersTables = borrowersTableQueries.loadSingleBorrower(borrowerId);

        if(borrowersTables == null){
            getNewBorrowerDetail(borrowerId);
        }else{
            count++;
            if(count == docSize) {
                loadLoansToUI();
            }
        }

    }

    /**
     * gets new borrower details from firebase firestore
     * @param borrowerId
     */
    private void getNewBorrowerDetail(String borrowerId) {
        borrowersQueries.retrieveSingleBorrowers(borrowerId)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            BorrowersTable borrowersTable = task.getResult().toObject(BorrowersTable.class);
                            borrowersTable.setBorrowersId(task.getResult().getId());

                            saveBorrowerToLocalStorage(borrowersTable);
                            saveBorrowerImage(borrowersTable);
                            count++;
                            if(count == docSize) {
                                loadLoansToUI();
                            }
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void saveBorrowerImage(final BorrowersTable borrowersTable) {
        final BorrowersTable table = borrowersTableQueries.loadSingleBorrower(borrowersTable.getBorrowersId());

        if(table.getBorrowerImageByteArray() == null){

            BitmapUtil.getImageWithGlide(this, borrowersTable.getProfileImageUri())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            saveImage(resource, table);
                        }
                    });

        }else if(table.getBorrowerImageByteArray() != null
                && !borrowersTable.getProfileImageUri().equals(table.getProfileImageUri())){

            BitmapUtil.getImageWithGlide(this, borrowersTable.getProfileImageUri())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            borrowersTable.setId(table.getId());
                            saveImage(resource, borrowersTable);
                        }
                    });
        }
    }

    private void saveImage(Bitmap resource, BorrowersTable borrowersTable) {
        byte[] bytes = BitmapUtil.getBytesFromBitmapInJPG(resource, 100);
        borrowersTable.setBorrowerImageByteArray(bytes);
        borrowersTableQueries.updateBorrowerDetails(borrowersTable);

        Log.d(TAG, "saveImage: borrower image byte[] saved");
    }

    /**
     * saves new borrower details to local storage
     * @param borrowersTable
     */
    private void saveBorrowerToLocalStorage(BorrowersTable borrowersTable) {
        BorrowersTable borrowersTables = borrowersTableQueries.loadSingleBorrower(borrowersTable.getBorrowersId());
        if(borrowersTables == null){
            borrowersTableQueries.insertBorrowersToStorage(borrowersTable);
        }
    }

    /**
     * save loans to loan storage
     * @param loansTable
     */
    private void saveLoanToLocalStorage(LoansTable loansTable) {
        LoansTable loansTables = loanTableQueries.loadSingleLoan(loansTable.getLoanId());
        if(loansTables == null) loanTableQueries.insertLoanToStorage(loansTable);
    }

    /**
     * loads all our loan data to UI for the user to see
     */
    public void loadLoansToUI(){
        List<LoansTable> loansTable = loanTableQueries.loadAllLoansOrderByCreationDate();

        loanDetailsList.clear();
        size = loansTable.size();

        emptyCollection.setVisibility(View.GONE);
        for(LoansTable table : loansTable){

            if(table.getBorrowerId() != null) {
                BorrowersTable borrowersTable = borrowersTableQueries.loadSingleBorrower(table.getBorrowerId());

                if(borrowersTable != null) {
                    LoanDetails loanDetails = new LoanDetails();
                    loanDetails.setBorrowersTable(borrowersTable);
                    loanDetails.setLoansTable(table);

                    loanDetailsList.add(loanDetails);
                    secondCount++;
                    loanRecyclerAdapter.notifyDataSetChanged();
                }else{
                    getSingleBorrowerDetails(table.getBorrowerId(), table);
                }
            }else{
                GroupBorrowerTable groupBorrowerTable = groupBorrowerTableQueries.loadSingleBorrowerGroup(table.getGroupId());

                if(groupBorrowerTable != null) {
                    LoanDetails loanDetails = new LoanDetails();
                    loanDetails.setGroupBorrowerTable(groupBorrowerTable);
                    loanDetails.setLoansTable(table);
                    loanDetailsList.add(loanDetails);
                    secondCount++;
                    loanRecyclerAdapter.notifyDataSetChanged();
                }else{
                    getSingleGroupDetails(table.getGroupId(), table);
                }
            }
        }

        if(secondCount == size) progressBar.setVisibility(View.GONE);
    }

    private void getSingleBorrowerDetails(String borrowerId, final LoansTable table) {
        borrowersQueries.retrieveSingleBorrowers(borrowerId)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            BorrowersTable borrowersTable = task.getResult().toObject(BorrowersTable.class);
                            borrowersTable.setBorrowersId(task.getResult().getId());

                            saveBorrowerToLocalStorage(borrowersTable);

                            LoanDetails loanDetails = new LoanDetails();
                            loanDetails.setBorrowersTable(borrowersTable);
                            loanDetails.setLoansTable(table);

                            loanDetailsList.add(loanDetails);
                            secondCount++;
                            loanRecyclerAdapter.notifyDataSetChanged();
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void getSingleGroupDetails(String groupId, final LoansTable table) {
        groupBorrowerQueries.retrieveSingleBorrowerGroup(groupId)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            GroupBorrowerTable groupBorrowerTable = task.getResult().toObject(GroupBorrowerTable.class);
                            groupBorrowerTable.setGroupId(task.getResult().getId());

                            saveGroupToLocalStorage(groupBorrowerTable);

                            LoanDetails loanDetails = new LoanDetails();
                            loanDetails.setGroupBorrowerTable(groupBorrowerTable);
                            loanDetails.setLoansTable(table);
                            loanDetailsList.add(loanDetails);
                            secondCount++;
                            loanRecyclerAdapter.notifyDataSetChanged();

                        }else {
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    /**
     * this method is called if loans already exist in local storage
     * this method helps to get data from cloud storage and compare to local storage to see if there is any update or an entirely new data has been created
     */
    public void loadAllLoansAndCompareToLocal() {
        final List<LoansTable> loanList = loanTableQueries.loadAllLoans();

        loansQueries.retrieveAllLoans()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){

                                docSize = task.getResult().size();
                                count = 0;

                                List<LoansTable> loansInStorageToRemove = new ArrayList<>();
                                for(DocumentSnapshot doc : task.getResult().getDocuments()) {

                                    Boolean doesDataExist = false;
                                    for (LoansTable loanTab : loanList) {
                                        if (loanTab.getLoanId().equals(doc.getId())) {
                                            doesDataExist = true;
                                            loansInStorageToRemove.add(loanTab);
                                            Log.d(TAG, "onComplete: loan id of " + doc.getId() + " already exist");
                                            break;
                                        }
                                    }

                                    if (!doesDataExist) {
                                        Log.d(TAG, "onComplete: loan id of " + doc.getId() + " does not exist");

                                        LoansTable loansTable = doc.toObject(LoansTable.class);
                                        loansTable.setLoanId(doc.getId());

                                        saveLoanToLocalStorage(loansTable);
                                        if(loansTable.getBorrowerId() != null) getBorrowerDetails(loansTable.getBorrowerId());
                                        else getGroupDetails(loansTable.getGroupId());
                                    } else {
                                        //Update local table if any changes
                                        docSize--;
                                        updateTable(doc);
                                    }
                                }

                                loanList.removeAll(loansInStorageToRemove);

                                //to delete deleted borrower in cloud from storage
                                if(!loanList.isEmpty()){
                                    for(LoansTable loanTab : loanList){
                                        deleteLoanFromLocalStorage(loanTab);
                                        Log.d("Delete", "deleted "+loanTab.getLoanId()+ " from storage");
                                    }
                                }

                                removeRefresher();
                            }else{
                                Toast.makeText(getApplicationContext(), "Document is empty", Toast.LENGTH_SHORT).show();
                                removeRefresher();
                            }
                        }else{
                            Log.d("Loan", "onComplete: "+task.getException().getMessage());
                            removeRefresher();
                        }
                    }
                });
    }

    /**
     * this method deletes a borrower from local storage
     * @param loansTable
     */
    private void deleteLoanFromLocalStorage(LoansTable loansTable) {
        loanTableQueries.deleteLoan(loansTable);
        loadLoansToUI();
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
     * updates any changes in the loan details from cloud in local storage
     * @param doc
     */
    private void updateTable(DocumentSnapshot doc) {
        LoansTable loansTable = doc.toObject(LoansTable.class);
        loansTable.setLoanId(doc.getId());

        LoansTable currentlySaved = loanTableQueries.loadSingleLoan(doc.getId());
        loansTable.setId(currentlySaved.getId());

        if(loansTable.getLastUpdatedAt().getTime() != currentlySaved.getLastUpdatedAt().getTime()){

            loanTableQueries.updateLoanDetails(loansTable);
            loadLoansToUI();
            Log.d("Loan", "Loan Detailed updated");

        }
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
