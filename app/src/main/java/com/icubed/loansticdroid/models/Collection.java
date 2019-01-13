package com.icubed.loansticdroid.models;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.cloudqueries.CollectionQueries;
import com.icubed.loansticdroid.fragments.HomeFragments.MapFragment;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.BorrowersTableQueries;
import com.icubed.loansticdroid.localdatabase.CollectionTable;
import com.icubed.loansticdroid.localdatabase.CollectionTableQueries;
import com.icubed.loansticdroid.localdatabase.LoanTableQueries;
import com.icubed.loansticdroid.localdatabase.LoansTable;

import java.util.List;

public class Collection {

    private LoanTableQueries loanTableQueries;
    private Loans loans;
    private CollectionQueries collectionQueries;
    private CollectionTableQueries collectionTableQueries;
    private BorrowersTableQueries borrowersTableQueries;
    private BorrowersQueries borrowersQueries;
    private int collectionSize;
    private int count;

    private Boolean isDueCollectionSingle;

    private FragmentActivity fragmentActivity;

    private static final String TAG = ".collection";

    public Collection(Application application, FragmentActivity activity){
        loanTableQueries = new LoanTableQueries(application);
        loans = new Loans(application);
        collectionQueries = new CollectionQueries();
        collectionTableQueries = new CollectionTableQueries(application);
        borrowersTableQueries = new BorrowersTableQueries(application);
        borrowersQueries = new BorrowersQueries(activity.getApplicationContext());

        isDueCollectionSingle = false;

        fragmentActivity = activity;
    }

    /***********check if collection exist in storage********/
    public boolean doesCollectionExistInLocalStorage(){
        List<CollectionTable> listOFcollections = collectionTableQueries.loadAllCollections();

        if(listOFcollections.isEmpty()){
            return false;
        }
        return true;
    }

    /***************retrieve new due collection****************/
    public void retrieveNewDueCollectionData(){

        if(!doesCollectionExistInLocalStorage()){

            collectionQueries.retrieveAllCollection()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                Log.d(TAG, "onComplete: Success in retrieving data from server");
                                if(!task.getResult().isEmpty()){

                                    count = 0;
                                    collectionSize = task.getResult().size();

                                    for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                                        CollectionTable collectionTable = documentSnapshot.toObject(CollectionTable.class);
                                        collectionTable.setCollectionId(documentSnapshot.getId());

                                        getLoansData(collectionTable.getLoanId(), collectionTable.getCollectionId());
                                        saveNewCollectionToLocalStorage(collectionTable);
                                    }

                                }else{
                                    Log.d(TAG, "onComplete: No due collections for today");
                                }
                            }else{
                                Log.d(TAG, "onComplete: Failed to retrieve new due collections");
                            }
                        }
                    });
        }
    }

    private void getLoansData(String loanId, final String collectionId) {
        loans.retrieveSingleLoan(loanId)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: Loan retrieved");
                            if(task.getResult().exists()){

                                LoansTable loansTable = task.getResult().toObject(LoansTable.class);
                                loansTable.setLoanId(task.getResult().getId());

                                saveLoanToLocalStorage(loansTable);
                                getBorrowersDetails(loansTable.getBorrowerId(), collectionId);
                            }
                        }else{
                            Log.d(TAG, "onComplete: Loan retrieved Failed");
                        }
                    }
                });
    }

    private void getBorrowersDetails(String borrowerId, final String collectionId) {
        borrowersQueries.retrieveSingleBorrowers(borrowerId)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: BorrowersQueries retrieved");
                            if(task.getResult().exists()){
                                count++;
                                //Set local storage table
                                BorrowersTable borrowersTable = task.getResult().toObject(BorrowersTable.class);
                                borrowersTable.setBorrowersId(task.getResult().getId());

                                if(!isDueCollectionSingle) {
                                    saveBorrowerToLocalStorage(borrowersTable);
                                }else{
                                    saveSingleBorrowerToLocalStorage(borrowersTable, collectionId);
                                }
                            }
                        }else{
                            Log.d(TAG, "onComplete: BorrowersQueries retrieved Failed");
                        }
                    }
                });
    }

    /***********Save new borrowersQueries to storage********************/
    public void saveBorrowerToLocalStorage(BorrowersTable borrowersTable) {

        List<BorrowersTable> allBorrowers = borrowersTableQueries.loadAllBorrowers();

        Boolean doesLoanAlreadyExist = false;

        for(BorrowersTable borrower : allBorrowers){
            if(borrower.getBorrowersId().equals(borrowersTable.getBorrowersId())){
                doesLoanAlreadyExist = true;
                break;
            }
        }

        if(!doesLoanAlreadyExist) {
            borrowersTableQueries.insertBorrowersToStorage(borrowersTable);
        }

        if(count == collectionSize){
            getDueCollectionData();
            count = 0;
        }
    }

    public void saveSingleBorrowerToLocalStorage(BorrowersTable borrowersTable, String collectionId){
        List<BorrowersTable> allBorrowers = borrowersTableQueries.loadAllBorrowers();

        Boolean doesLoanAlreadyExist = false;

        for(BorrowersTable borrower : allBorrowers){
            if(borrower.getBorrowersId().equals(borrowersTable.getBorrowersId())){
                doesLoanAlreadyExist = true;
                break;
            }
        }

        if(!doesLoanAlreadyExist) {
            borrowersTableQueries.insertBorrowersToStorage(borrowersTable);
        }

        getSingleDueCollectionData(collectionId);
        isDueCollectionSingle = false;
    }

    /****************Save loans to storage************************/
    public void saveLoanToLocalStorage(LoansTable loansTable) {
        List<LoansTable> loansTables = loanTableQueries.loadAllLoans();

        for(LoansTable loan : loansTables){
            if(loan.getLoanId().equals(loansTable.getLoanId())){
                return;
            }
        }

        loanTableQueries.insertLoanToStorage(loansTable);
    }

    /**********************Sae new collections to storage************/
    public void saveNewCollectionToLocalStorage(CollectionTable collectionTable) {
        collectionTableQueries.insertCollectionToStorage(collectionTable);
    }

    /*******************retrieve all collections in storage*************/
    public List<CollectionTable> retrieveCollectionToLocalStorage(){

        if(doesCollectionExistInLocalStorage()) {
            return collectionTableQueries.loadAllCollections();
        }
        return null;
    }

    /**************Retrieves data to show on slideuppanel************/
    public void getDueCollectionData(){
        List<CollectionTable> collectionTables = collectionTableQueries.loadAllDueCollections();

        //Updates UI
        FragmentManager fm = fragmentActivity.getSupportFragmentManager();
        MapFragment fragment = (MapFragment) fm.findFragmentByTag("home");

        for(CollectionTable collectionTable : collectionTables){

            DueCollectionDetails dueCollectionDetails = new DueCollectionDetails();
            dueCollectionDetails.setDueAmount(collectionTable.getCollectionDueAmount());
            dueCollectionDetails.setCollectionNumber(collectionTable.getCollectionNumber());
            dueCollectionDetails.setDueCollectionDate(collectionTable.getCollectionDueDate());
            dueCollectionDetails.setIsDueCollected(collectionTable.getIsDueCollected());

            LoansTable loan = loanTableQueries.loadSingleLoan(collectionTable.getLoanId());

            BorrowersTable borrowersTable = borrowersTableQueries.loadSingleBorrower(loan.getBorrowerId());

            dueCollectionDetails.setFirstName(borrowersTable.getFirstName());
            dueCollectionDetails.setLastName(borrowersTable.getLastName());
            dueCollectionDetails.setWorkAddress(borrowersTable.getWorkAddress());
            dueCollectionDetails.setBusinessName(borrowersTable.getBusinessName());

            fragment.dueCollectionList.add(dueCollectionDetails);
            fragment.slideUpPanelRecyclerAdapter.notifyDataSetChanged();
        }

        fragment.hideProgressBar();

    }

    public void getSingleDueCollectionData(String collectionId){
        CollectionTable collectionTable = collectionTableQueries.loadSingleCollection(collectionId);

        //Updates UI
        FragmentManager fm = fragmentActivity.getSupportFragmentManager();
        MapFragment fragment = (MapFragment) fm.findFragmentByTag("home");

        DueCollectionDetails dueCollectionDetails = new DueCollectionDetails();
        dueCollectionDetails.setDueAmount(collectionTable.getCollectionDueAmount());
        dueCollectionDetails.setCollectionNumber(collectionTable.getCollectionNumber());
        dueCollectionDetails.setDueCollectionDate(collectionTable.getCollectionDueDate());
        dueCollectionDetails.setIsDueCollected(collectionTable.getIsDueCollected());

        LoansTable loan = loanTableQueries.loadSingleLoan(collectionTable.getLoanId());

        BorrowersTable borrowersTable = borrowersTableQueries.loadSingleBorrower(loan.getBorrowerId());

        dueCollectionDetails.setFirstName(borrowersTable.getFirstName());
        dueCollectionDetails.setLastName(borrowersTable.getLastName());
        dueCollectionDetails.setWorkAddress(borrowersTable.getWorkAddress());
        dueCollectionDetails.setBusinessName(borrowersTable.getBusinessName());

        fragment.dueCollectionList.add(dueCollectionDetails);
        fragment.slideUpPanelRecyclerAdapter.notifyDataSetChanged();
    }

    /***********************retrieve all collection and comparing to local***********/
    public void retrieveDueCollectionToLocalStorageAndCompareToCloud(){
        final List<CollectionTable> localCollection = retrieveCollectionToLocalStorage();

        collectionQueries.retrieveAllCollection()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: Success in retrieving data from server");
                            if(!task.getResult().isEmpty()){

                                for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){

                                    Boolean doesDataExist = false;
                                    for(CollectionTable colTab : localCollection){
                                        if(colTab.getCollectionId().equals(documentSnapshot.getId())){
                                            doesDataExist = true;
                                            Log.d(TAG, "onComplete: collection id of "+documentSnapshot.getId()+" already exist");
                                            break;
                                        }
                                    }

                                    if(!doesDataExist) {
                                        Log.d(TAG, "onComplete: collection id of "+documentSnapshot.getId()+" does not exist");

                                        isDueCollectionSingle = true;
                                        CollectionTable collectionTable = documentSnapshot.toObject(CollectionTable.class);
                                        collectionTable.setCollectionId(documentSnapshot.getId());

                                        saveNewCollectionToLocalStorage(collectionTable);
                                        getLoansData(collectionTable.getLoanId(), collectionTable.getCollectionId());
                                    }
                                }

                            }else{
                                Log.d(TAG, "onComplete: No New due collections for today");
                            }
                        }else{
                            Log.d(TAG, "onComplete: Failed to retrieve new due collections");
                        }
                    }
                });
    }
}
