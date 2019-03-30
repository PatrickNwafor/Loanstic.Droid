package com.icubed.loansticdroid.models;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.icubed.loansticdroid.activities.SearchSavingsActivity;
import com.icubed.loansticdroid.adapters.CollectionLoanRecyclerAdapter;
import com.icubed.loansticdroid.adapters.LoanRecyclerAdapter;
import com.icubed.loansticdroid.adapters.LoanRepaymentRecyclerAdapter;
import com.icubed.loansticdroid.adapters.SavingsRecyclerAdapter;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.cloudqueries.GroupBorrowerQueries;
import com.icubed.loansticdroid.cloudqueries.SavingsPlanTypeQueries;
import com.icubed.loansticdroid.cloudqueries.SavingsPlanTypeQueries;
import com.icubed.loansticdroid.cloudqueries.SavingsQueries;
import com.icubed.loansticdroid.cloudqueries.SavingsQueries;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.localdatabase.LoanTypeTable;
import com.icubed.loansticdroid.localdatabase.LoansTable;
import com.icubed.loansticdroid.localdatabase.OtherLoanTypesTable;
import com.icubed.loansticdroid.localdatabase.SavingsPlanTypeTable;
import com.icubed.loansticdroid.localdatabase.SavingsTable;

import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class SearchSavings {
    private SavingsQueries savingsQueries;
    private SavingsPlanTypeQueries savingsPlanTypeQueries;
    private Activity activity;
    private BorrowersQueries borrowersQueries;
    private int count = 0;
    private int docSize = 0;

    public SearchSavings(Activity activity) {
        this.activity = activity;
        savingsQueries = new SavingsQueries();
        savingsPlanTypeQueries = new SavingsPlanTypeQueries();
        borrowersQueries = new BorrowersQueries(activity);
    }

    /**
     * this method accepts a list of loan id returned algolia search
     * the ids are then used to query data from firebase firestore to display to UI
     * @param savingsIds
     */
    public void loadAllSavings(List<String> savingsIds){

        docSize = savingsIds.size();
        count = 0;

        for (String savingsId : savingsIds) {
            savingsQueries.retrieveSingleSavings(savingsId)
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                if(task.getResult().exists()){
                                    SavingsTable savingsTable = task.getResult().toObject(SavingsTable.class);
                                    savingsTable.setSavingsId(task.getResult().getId());

                                    getSavingsType(savingsTable);
                                }else{
                                    ((SearchSavingsActivity) activity).progressBar.setVisibility(View.GONE);
                                    Toast.makeText(activity, "Loan does not exist", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Log.d(TAG, "onComplete: "+task.getException().getMessage());
                            }
                        }
                    });
        }
    }

    private void getSavingsType(SavingsTable savingsTable) {
        if(savingsTable.getSavingsPlanTypeId() != null){
            getNewSavingsType(savingsTable);
        }else getBorrower(savingsTable, null);
    }

    private void getBorrower(final SavingsTable savingsTable, final SavingsPlanTypeTable savingsPlanTypeTable) {
        borrowersQueries.retrieveSingleBorrowers(savingsTable.getBorrowerId())
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            BorrowersTable borrowersTable = task.getResult().toObject(BorrowersTable.class);
                            borrowersTable.setBorrowersId(task.getResult().getId());
                            borrowersTable.setId((long) 443);

                            SavingsDetails savingsDetails = new SavingsDetails();
                            savingsDetails.setBorrowersTable(borrowersTable);
                            savingsDetails.setSavingsTable(savingsTable);
                            savingsDetails.setSavingsPlanTypeTable(savingsPlanTypeTable);

                            ((SearchSavingsActivity) activity).savingsDetails.add(savingsDetails);

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

    private void getNewSavingsType(final SavingsTable savingsTable) {
        savingsPlanTypeQueries.retrieveSingleSavingsPlanType(savingsTable.getSavingsPlanTypeId())
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            SavingsPlanTypeTable savingsPlanTypeTable = task.getResult().toObject(SavingsPlanTypeTable.class);
                            savingsPlanTypeTable.setSavingsPlanTypeId(task.getResult().getId());

                            getBorrower(savingsTable, savingsPlanTypeTable);

                        }else {
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    /**
     * loads all our loan data to UI for the user to see
     */
    private void loadLoansToUI(){
        if(((SearchSavingsActivity) activity).fromSavingsActivity.equals("savings")) {
            ((SearchSavingsActivity) activity).savingsRecyclerAdapter = new SavingsRecyclerAdapter(((SearchSavingsActivity) activity).savingsDetails);
            ((SearchSavingsActivity) activity).savingsRecyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));
            ((SearchSavingsActivity) activity).savingsRecyclerView.setAdapter((((SearchSavingsActivity) activity).savingsRecyclerAdapter));
        }
//        else if(((SearchSavingsActivity) activity).fromSavingsActivity.equals("repay")){
//            ((SearchSavingsActivity) activity).loanRepaymentRecyclerAdapter = new LoanRepaymentRecyclerAdapter(((SearchSavingsActivity) activity).loanDetailsList);
//            ((SearchSavingsActivity) activity).loanRecyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));
//            ((SearchSavingsActivity) activity).loanRecyclerView.setAdapter((((SearchSavingsActivity) activity).loanRepaymentRecyclerAdapter));
//        }else if(((SearchSavingsActivity) activity).fromSavingsActivity.equals("col")){
//            ((SearchSavingsActivity) activity).collectionLoanRecyclerAdapter = new CollectionLoanRecyclerAdapter(((SearchSavingsActivity) activity).loanDetailsList);
//            ((SearchSavingsActivity) activity).loanRecyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));
//            ((SearchSavingsActivity) activity).loanRecyclerView.setAdapter((((SearchSavingsActivity) activity).collectionLoanRecyclerAdapter));
//        }
        ((SearchSavingsActivity) activity).savingsRecyclerView.setVisibility(View.VISIBLE);
        ((SearchSavingsActivity) activity).progressBar.setVisibility(View.GONE);
    }
}
