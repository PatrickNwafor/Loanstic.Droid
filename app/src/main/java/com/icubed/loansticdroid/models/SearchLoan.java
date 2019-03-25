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
import com.icubed.loansticdroid.activities.LoanSearchActivity;
import com.icubed.loansticdroid.adapters.CollectionLoanRecyclerAdapter;
import com.icubed.loansticdroid.adapters.LoanRecyclerAdapter;
import com.icubed.loansticdroid.adapters.LoanRepaymentRecyclerAdapter;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.cloudqueries.GroupBorrowerQueries;
import com.icubed.loansticdroid.cloudqueries.LoanTypeQueries;
import com.icubed.loansticdroid.cloudqueries.LoansQueries;
import com.icubed.loansticdroid.cloudqueries.OtherLoanTypeQueries;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.localdatabase.LoanTypeTable;
import com.icubed.loansticdroid.localdatabase.LoansTable;
import com.icubed.loansticdroid.localdatabase.OtherLoanTypesTable;

import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class SearchLoan {
    private LoansQueries loansQueries;
    private LoanTypeQueries loanTypeQueries;
    private OtherLoanTypeQueries otherLoanTypeQueries;
    private Activity activity;
    private BorrowersQueries borrowersQueries;
    private GroupBorrowerQueries groupBorrowerQueries;
    private int count = 0;
    private int docSize = 0;

    public SearchLoan(Activity activity) {
        this.activity = activity;
        loansQueries = new LoansQueries();
        loanTypeQueries = new LoanTypeQueries();
        otherLoanTypeQueries = new OtherLoanTypeQueries();
        borrowersQueries = new BorrowersQueries(activity);
        groupBorrowerQueries = new GroupBorrowerQueries();
    }

    /**
     * this method accepts a list of loan id returned algolia search
     * the ids are then used to query data from firebase firestore to display to UI
     * @param loanIds
     */
    public void loadAllLoan(List<String> loanIds){

        docSize = loanIds.size();
        count = 0;

        for (String loanId : loanIds) {
            loansQueries.retrieveSingleLoan(loanId)
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                if(task.getResult().exists()){
                                    LoansTable loansTable = task.getResult().toObject(LoansTable.class);
                                    loansTable.setLoanId(task.getResult().getId());

                                    getLoanType(loansTable);
                                }else{
                                    ((LoanSearchActivity) activity).progressBar.setVisibility(View.GONE);
                                    Toast.makeText(activity, "Loan does not exist", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Log.d(TAG, "onComplete: "+task.getException().getMessage());
                            }
                        }
                    });
        }
    }

    /**
     * This method fetch borrower group details from firebase firestore
     * it is usually called when the loan was registered by a group and not single borrower
     * @param loansTable
     * @param loanTypeTable
     * @param otherLoanTypesTable
     */
    private void getNewGroupDetails(final LoansTable loansTable, final LoanTypeTable loanTypeTable, final OtherLoanTypesTable otherLoanTypesTable) {
        groupBorrowerQueries.retrieveSingleBorrowerGroup(loansTable.getGroupId())
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            GroupBorrowerTable groupBorrowerTable = task.getResult().toObject(GroupBorrowerTable.class);
                            groupBorrowerTable.setGroupId(task.getResult().getId());
                            groupBorrowerTable.setId((long) 4443);

                            LoanDetails loanDetails = new LoanDetails();
                            loanDetails.setBorrowersTable(null);
                            loanDetails.setGroupBorrowerTable(groupBorrowerTable);
                            loanDetails.setLoansTable(loansTable);
                            loanDetails.setLoanTypeTable(loanTypeTable);
                            loanDetails.setOtherLoanTypesTable(otherLoanTypesTable);

                            ((LoanSearchActivity) activity).loanDetailsList.add(loanDetails);

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
     * This method fetch borrower details from firebase firestore
     * it is usually called when the loan was registered by a borrower and not a group
     * @param loansTable
     * @param loanTypeTable
     * @param otherLoanTypesTable
     */
    private void getNewBorrowerDetail(final LoansTable loansTable, final LoanTypeTable loanTypeTable, final OtherLoanTypesTable otherLoanTypesTable) {
        borrowersQueries.retrieveSingleBorrowers(loansTable.getBorrowerId())
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            BorrowersTable borrowersTable = task.getResult().toObject(BorrowersTable.class);
                            borrowersTable.setBorrowersId(task.getResult().getId());
                            borrowersTable.setId((long) 443);

                            LoanDetails loanDetails = new LoanDetails();
                            loanDetails.setBorrowersTable(borrowersTable);
                            loanDetails.setGroupBorrowerTable(null);
                            loanDetails.setLoansTable(loansTable);
                            loanDetails.setLoanTypeTable(loanTypeTable);
                            loanDetails.setOtherLoanTypesTable(otherLoanTypesTable);

                            ((LoanSearchActivity) activity).loanDetailsList.add(loanDetails);

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

    /**
     * this methods helps to decide if the loan is of a type registered initially by the branch manager
     * or the loan type is a custom one created during the registration of the loan
     * @param loansTable
     */
    private void getLoanType(LoansTable loansTable) {

        if(loansTable.getIsOtherLoanType()) {
            getOtherLoanType(loansTable);
        } else{
            getNormalLoanType(loansTable);
        }
    }

    /**
     * gets registered loan type from firebase firestore
     * @param loansTable
     */
    private void getNormalLoanType(final LoansTable loansTable) {
        loanTypeQueries.retrieveSingleLoanType(loansTable.getLoanTypeId())
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            LoanTypeTable loanTypeTable = task.getResult().toObject(LoanTypeTable.class);
                            loanTypeTable.setLoanTypeId(task.getResult().getId());

                            if(loansTable.getBorrowerId() != null) getNewBorrowerDetail(loansTable, loanTypeTable, null);
                            else getNewGroupDetails(loansTable, loanTypeTable, null);
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    /**
     * gets custom loan type created during loan registration from firebase firestore
     * @param loansTable
     */
    private void getOtherLoanType(final LoansTable loansTable) {
        otherLoanTypeQueries.retrieveSingleOtherLoanType(loansTable.getLoanTypeId())
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            OtherLoanTypesTable otherLoanTypesTable = task.getResult().toObject(OtherLoanTypesTable.class);
                            otherLoanTypesTable.setOtherLoanTypeId(task.getResult().getId());

                            if(loansTable.getBorrowerId() != null) getNewBorrowerDetail(loansTable, null, otherLoanTypesTable);
                            else getNewGroupDetails(loansTable,  null, otherLoanTypesTable);
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    /**
     * loads all our loan data to UI for the user to see
     */
    private void loadLoansToUI(){
        if(((LoanSearchActivity) activity).fromLoanActivity.equals("loan")) {
            ((LoanSearchActivity) activity).loanRecyclerAdapter = new LoanRecyclerAdapter(((LoanSearchActivity) activity).loanDetailsList);
            ((LoanSearchActivity) activity).loanRecyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));
            ((LoanSearchActivity) activity).loanRecyclerView.setAdapter((((LoanSearchActivity) activity).loanRecyclerAdapter));
        }else if(((LoanSearchActivity) activity).fromLoanActivity.equals("repay")){
            ((LoanSearchActivity) activity).loanRepaymentRecyclerAdapter = new LoanRepaymentRecyclerAdapter(((LoanSearchActivity) activity).loanDetailsList);
            ((LoanSearchActivity) activity).loanRecyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));
            ((LoanSearchActivity) activity).loanRecyclerView.setAdapter((((LoanSearchActivity) activity).loanRepaymentRecyclerAdapter));
        }else if(((LoanSearchActivity) activity).fromLoanActivity.equals("col")){
            ((LoanSearchActivity) activity).collectionLoanRecyclerAdapter = new CollectionLoanRecyclerAdapter(((LoanSearchActivity) activity).loanDetailsList);
            ((LoanSearchActivity) activity).loanRecyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));
            ((LoanSearchActivity) activity).loanRecyclerView.setAdapter((((LoanSearchActivity) activity).collectionLoanRecyclerAdapter));
        }
        ((LoanSearchActivity) activity).loanRecyclerView.setVisibility(View.VISIBLE);
        ((LoanSearchActivity) activity).progressBar.setVisibility(View.GONE);
    }
}
