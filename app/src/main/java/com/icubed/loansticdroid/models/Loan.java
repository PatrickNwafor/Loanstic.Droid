package com.icubed.loansticdroid.models;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.activities.LoanActivity;
import com.icubed.loansticdroid.adapters.LoanRecyclerAdapter;
import com.icubed.loansticdroid.cloudqueries.LoanTypeQueries;
import com.icubed.loansticdroid.cloudqueries.LoansQueries;
import com.icubed.loansticdroid.cloudqueries.OtherLoanTypeQueries;
import com.icubed.loansticdroid.localdatabase.LoanTableQueries;
import com.icubed.loansticdroid.localdatabase.LoanTypeTable;
import com.icubed.loansticdroid.localdatabase.LoanTypeTableQueries;
import com.icubed.loansticdroid.localdatabase.LoansTable;
import com.icubed.loansticdroid.localdatabase.OtherLoanTypesTableQueries;

import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class Loan {
    private LoansQueries loansQueries;
    private LoanTableQueries loanTableQueries;
    private LoanTypeTableQueries loanTypeTableQueries;
    private LoanTypeQueries loanTypeQueries;
    private OtherLoanTypeQueries otherLoanTypeQueries;
    private OtherLoanTypesTableQueries otherLoanTypesTableQueries;
    Activity activity;

    public Loan(Activity activity) {
        this.activity = activity;
        loansQueries = new LoansQueries();
        loanTableQueries = new LoanTableQueries(activity.getApplication());
        loanTypeTableQueries = new LoanTypeTableQueries(activity.getApplication());
        loanTypeQueries = new LoanTypeQueries();
    }

    public Boolean doesSingleLoansTableExistInLocalStorage(){
        List<LoansTable> loansTable = loanTableQueries.loadAllSingleLoans();
        return !loansTable.isEmpty();
    }

    /****
     * @TODO
     * to method loansQueries.retrieveAllBorrowers() to loansQueries.retrieveAllBorrowersForLoanOfficer()
     * this is necessary so that only the borrowers assigned to a loan officer is seen on his borrowers page.
     * if the officers need to search for a non assigned borrower, he has to use the search field
     */
    public void loadAllSingleLoan(){

        if(doesSingleLoansTableExistInLocalStorage()){
            loadAllBorrowersAndCompareToLocal();
            return;
        }

        loansQueries.retrieveAllLoans()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().isEmpty()){

                        for(DocumentSnapshot doc : task.getResult().getDocuments()){
                            LoansTable loansTable = doc.toObject(LoansTable.class);
                            loansTable.setLoanId(doc.getId());
                            
                            if(loansTable.getBorrowerId() != null) {
                                saveLoanToLocalStorage(loansTable);
                                getLoanType(loansTable);
                            }
                        }

                        loadLoansToUI();
                    }else{
                        removeRefresher();
                        ((LoanActivity) activity).loanProgressBar.setVisibility(View.GONE);
                        Toast.makeText(activity, "Document is empty", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Log.d(TAG, "onComplete: "+task.getException().getMessage());
                }
            }
        });
    }

    private void getLoanType(LoansTable loansTable) {
        LoanTypeTable loanTypeTable = loanTypeTableQueries.loadSingleLoanType(loansTable.getLoanTypeId());

        if(loanTypeTable ==  null){
            if(loansTable.getIsOtherLoanType()) getOtherLoanType(loansTable.getLoanTypeId());
            else getNormalLoanType(loansTable.getLoanTypeId());
        }
    }

    private void getNormalLoanType(String loanTypeId) {
        loanTypeQueries.retrieveSingleLoanType(loanTypeId)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                        }else{

                        }
                    }
                });
    }

    private void getOtherLoanType(String loanTypeId) {
    }

    private void saveLoanToLocalStorage(LoansTable loansTable) {
        loanTableQueries.insertLoanToStorage(loansTable);
    }

    public void loadLoansToUI(){
        List<LoansTable> loansTable = loanTableQueries.loadAllSingleLoans();

        ((LoanActivity) activity).loanRecyclerAdapter = new LoanRecyclerAdapter(loansTable);
        ((LoanActivity) activity).loanRecyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));
        ((LoanActivity) activity).loanRecyclerView.setAdapter((((LoanActivity) activity).loanRecyclerAdapter));
        ((LoanActivity) activity).loanProgressBar.setVisibility(View.GONE);
    }

    /****
     * @TODO
     * to method loansQueries.retrieveAllBorrowers() to loansQueries.retrieveAllBorrowersForLoanOfficer()
     * this is necessary so that only the borrowers assigned to a loan officer is seen on his borrowers page.
     * if the officers need to search for a non assigned borrower, he has to use the search field
     */
    public void loadAllBorrowersAndCompareToLocal() {
        final List<LoansTable> loanList = loanTableQueries.loadAllSingleLoans();

        loansQueries.retrieveAllLoans()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){
                                
                                Boolean isThereNewData = false;
                                List<LoansTable> loansInStorage = loanList;
                                for(DocumentSnapshot doc : task.getResult().getDocuments()) {
                                    
                                    if(doc.getString("borrowerId") != null) {

                                        Boolean doesDataExist = false;
                                        for (LoansTable loanTab : loanList) {
                                            if (loanTab.getLoanId().equals(doc.getId())) {
                                                doesDataExist = true;
                                                loansInStorage.remove(loanTab);
                                                Log.d(TAG, "onComplete: loan id of " + doc.getId() + " already exist");
                                                break;
                                            }
                                        }

                                        if (!doesDataExist) {
                                            Log.d(TAG, "onComplete: loan id of " + doc.getId() + " does not exist");

                                            LoansTable loansTable = doc.toObject(LoansTable.class);
                                            loansTable.setLoanId(doc.getId());
                                            isThereNewData = true;

                                            saveLoanToLocalStorage(loansTable);
                                        } else {
                                            //Update local table if any changes
                                            updateTable(doc);
                                        }

                                    }    
                                }

                                //to delete deleted borrower in cloud from storage
                                if(!loansInStorage.isEmpty()){
                                    for(LoansTable borowTab : loansInStorage){
                                        deleteBorrowerFromLocalStorage(borowTab);
                                        Log.d("Delete", "deleted "+borowTab.getLoanId()+ " from storage");
                                    }
                                }

                                if(isThereNewData || !loansInStorage.isEmpty()) {
                                    loadLoansToUI();
                                }
                                removeRefresher();
                            }else{
                                Toast.makeText(activity, "Document is empty", Toast.LENGTH_SHORT).show();
                                removeRefresher();
                            }
                        }else{
                            Log.d("Loan", "onComplete: "+task.getException().getMessage());
                            removeRefresher();
                        }
                    }
                });
    }

    private void deleteBorrowerFromLocalStorage(LoansTable loansTable) {
        loanTableQueries.deleteLoan(loansTable);
    }

    private void removeRefresher(){
        ((LoanActivity) activity).swipeRefreshLayout.setRefreshing(false);
        ((LoanActivity) activity).swipeRefreshLayout.destroyDrawingCache();
        ((LoanActivity) activity).swipeRefreshLayout.clearAnimation();
    }

    private void updateTable(DocumentSnapshot doc) {
        LoansTable loansTable = doc.toObject(LoansTable.class);
        loansTable.setLoanId(doc.getId());

        LoansTable currentlySaved = loanTableQueries.loadSingleLoan(doc.getId());
        loansTable.setId(currentlySaved.getId());

        if(!loansTable.getLastUpdatedAt().equals(currentlySaved.getLastUpdatedAt())){

            loanTableQueries.updateLoanDetails(loansTable);
            loadLoansToUI();
            Log.d("Borrower", "Borrower Detailed updated");

        }
    }
}
