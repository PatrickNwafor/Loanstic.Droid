package com.icubed.loansticdroid.models;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.activities.LoanActivity;
import com.icubed.loansticdroid.activities.RepaymentActivity;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.cloudqueries.GroupBorrowerQueries;
import com.icubed.loansticdroid.cloudqueries.LoansQueries;
import com.icubed.loansticdroid.fragments.RepaymentFragment.LoanRepaymentFragment;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.BorrowersTableQueries;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTableQueries;
import com.icubed.loansticdroid.localdatabase.LoanTableQueries;
import com.icubed.loansticdroid.localdatabase.LoansTable;
import com.icubed.loansticdroid.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class LoanRepayment {
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
    private FragmentActivity activity;
    private LoanRepaymentFragment fragment;


    public LoanRepayment(FragmentActivity activity) {
        this.activity = activity;
        loansQueries = new LoansQueries();
        loanTableQueries = new LoanTableQueries(activity.getApplication());
        borrowersQueries = new BorrowersQueries(activity);
        borrowersTableQueries = new BorrowersTableQueries(activity.getApplication());
        groupBorrowerQueries = new GroupBorrowerQueries();
        groupBorrowerTableQueries = new GroupBorrowerTableQueries(activity.getApplication());

        FragmentManager fm = activity.getSupportFragmentManager();
        fragment = (LoanRepaymentFragment) fm.findFragmentByTag("repayment");
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
                                if(fragment != null) {
                                    fragment.progressBar.setVisibility(View.GONE);
                                    fragment.emptyCollection.setVisibility(View.VISIBLE);
                                }
                                Toast.makeText(activity, "Document is empty", Toast.LENGTH_SHORT).show();
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

            BitmapUtil.getImageWithGlide(activity, borrowersTable.getProfileImageUri())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            saveImage(resource, table);
                        }
                    });

        }else if(table.getBorrowerImageByteArray() != null
                && !borrowersTable.getProfileImageUri().equals(table.getProfileImageUri())){

            BitmapUtil.getImageWithGlide(activity, borrowersTable.getProfileImageUri())
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

        fragment.loanDetailsList.clear();
        size = loansTable.size();

        fragment.emptyCollection.setVisibility(View.GONE);
        for(LoansTable table : loansTable){

            if(table.getBorrowerId() != null) {
                BorrowersTable borrowersTable = borrowersTableQueries.loadSingleBorrower(table.getBorrowerId());

                LoanDetails loanDetails = new LoanDetails();
                loanDetails.setBorrowersTable(borrowersTable);
                loanDetails.setLoansTable(table);

                fragment.loanDetailsList.add(loanDetails);
                secondCount++;
                fragment.loanRecyclerAdapter.notifyDataSetChanged();
            }else{
                GroupBorrowerTable groupBorrowerTable = groupBorrowerTableQueries.loadSingleBorrowerGroup(table.getGroupId());

                LoanDetails loanDetails = new LoanDetails();
                loanDetails.setGroupBorrowerTable(groupBorrowerTable);
                loanDetails.setLoansTable(table);
                fragment.loanDetailsList.add(loanDetails);
                secondCount++;
                fragment.loanRecyclerAdapter.notifyDataSetChanged();
            }
        }

        if(secondCount == size) fragment.progressBar.setVisibility(View.GONE);
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
        if(fragment != null) {
            fragment.swipeRefreshLayout.setRefreshing(false);
            fragment.swipeRefreshLayout.destroyDrawingCache();
            fragment.swipeRefreshLayout.clearAnimation();
        }
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

}
