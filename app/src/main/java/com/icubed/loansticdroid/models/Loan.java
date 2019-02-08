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
import com.icubed.loansticdroid.localdatabase.OtherLoanTypesTable;
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
    private Activity activity;

    public Loan(Activity activity) {
        this.activity = activity;
        loansQueries = new LoansQueries();
        loanTableQueries = new LoanTableQueries(activity.getApplication());
        loanTypeTableQueries = new LoanTypeTableQueries(activity.getApplication());
        loanTypeQueries = new LoanTypeQueries();
        otherLoanTypeQueries = new OtherLoanTypeQueries();
        otherLoanTypesTableQueries = new OtherLoanTypesTableQueries(activity.getApplication());
    }

    public Boolean doesLoansTableExistInLocalStorage(){
        List<LoansTable> loansTable = loanTableQueries.loadAllLoans();
        return !loansTable.isEmpty();
    }

    /****
     * @TODO
     * to method loansQueries.retrieveAllBorrowers() to loansQueries.retrieveAllBorrowersForLoanOfficer()
     * this is necessary so that only the borrowers assigned to a loan officer is seen on his borrowers page.
     * if the officers need to search for a non assigned borrower, he has to use the search field
     */
    public void loadAllLoan(){

        loansQueries.retrieveAllLoans()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){

                                for(DocumentSnapshot doc : task.getResult().getDocuments()){
                                    LoansTable loansTable = doc.toObject(LoansTable.class);
                                    loansTable.setLoanId(doc.getId());

                                    saveLoanToLocalStorage(loansTable);
                                    getLoanType(loansTable);
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

        if(loansTable.getIsOtherLoanType()) {
            List<OtherLoanTypesTable> otherLoanTypesTable = otherLoanTypesTableQueries.loadAllLoanTpes();

            if(otherLoanTypesTable != null) {
                for (OtherLoanTypesTable typesTable : otherLoanTypesTable) {
                    if(typesTable.getOtherLoanTypeId().equals(loansTable.getLoanTypeId())){
                        return;
                    }
                }

                getOtherLoanType(loansTable.getLoanTypeId());
            }
        }
        else{
            List<LoanTypeTable> loanTypeTable = loanTypeTableQueries.loadAllLoanTpes();

            if(loanTypeTable != null){
                for (LoanTypeTable typeTable : loanTypeTable) {
                    if(typeTable.getLoanTypeId().equals(loansTable.getLoanTypeId())){
                        return;
                    }
                }

                getNormalLoanType(loansTable.getLoanTypeId());
            }
        }
    }

    private void getNormalLoanType(String loanTypeId) {
        loanTypeQueries.retrieveSingleLoanType(loanTypeId)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            LoanTypeTable loanTypeTable = task.getResult().toObject(LoanTypeTable.class);
                            loanTypeTable.setLoanTypeId(task.getResult().getId());

                            saveLoanTypeToLocalStorage(loanTypeTable);
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void saveLoanTypeToLocalStorage(LoanTypeTable loanTypeTable) {
        loanTypeQueries.saveLoanType(loanTypeTable);
    }

    private void getOtherLoanType(String loanTypeId) {
        otherLoanTypeQueries.retrieveSingleOtherLoanType(loanTypeId)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            OtherLoanTypesTable otherLoanTypesTable = task.getResult().toObject(OtherLoanTypesTable.class);
                            otherLoanTypesTable.setOtherLoanTypeId(task.getResult().getId());

                            saveOtherLoanTypeToLocalStorage(otherLoanTypesTable);
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void saveOtherLoanTypeToLocalStorage(OtherLoanTypesTable otherLoanTypesTable) {
        otherLoanTypesTableQueries.insertLoanTypeToStorage(otherLoanTypesTable);
    }

    private void saveLoanToLocalStorage(LoansTable loansTable) {
        loanTableQueries.insertLoanToStorage(loansTable);
    }

    public void loadLoansToUI(){
        List<LoansTable> loansTable = loanTableQueries.loadAllLoansOrderByCreationDate();

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
    public void loadAllLoansAndCompareToLocal() {
        final List<LoansTable> loanList = loanTableQueries.loadAllLoans();

        loansQueries.retrieveAllLoans()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){
                                
                                Boolean isThereNewData = false;
                                List<LoansTable> loansInStorage = loanList;
                                for(DocumentSnapshot doc : task.getResult().getDocuments()) {

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
                                        getLoanType(loansTable);
                                    } else {
                                        //Update local table if any changes
                                        updateTable(doc);
                                    }
                                }

                                //to delete deleted borrower in cloud from storage
                                if(!loansInStorage.isEmpty()){
                                    for(LoansTable loanTab : loansInStorage){
                                        deleteBorrowerFromLocalStorage(loanTab);
                                        Log.d("Delete", "deleted "+loanTab.getLoanId()+ " from storage");
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

        if(loansTable.getLastUpdatedAt().getTime() != currentlySaved.getLastUpdatedAt().getTime()){

            loanTableQueries.updateLoanDetails(loansTable);
            loadLoansToUI();
            Log.d("Loan", "Loan Detailed updated");

        }
    }
}
