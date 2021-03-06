package com.icubed.loansticdroid.models;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
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
import com.icubed.loansticdroid.adapters.LoanRecyclerAdapter;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.cloudqueries.GroupBorrowerQueries;
import com.icubed.loansticdroid.cloudqueries.LoanTypeQueries;
import com.icubed.loansticdroid.cloudqueries.LoansQueries;
import com.icubed.loansticdroid.cloudqueries.OtherLoanTypeQueries;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.BorrowersTableQueries;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTableQueries;
import com.icubed.loansticdroid.localdatabase.LoanTableQueries;
import com.icubed.loansticdroid.localdatabase.LoanTypeTable;
import com.icubed.loansticdroid.localdatabase.LoanTypeTableQueries;
import com.icubed.loansticdroid.localdatabase.LoansTable;
import com.icubed.loansticdroid.localdatabase.OtherLoanTypesTable;
import com.icubed.loansticdroid.localdatabase.OtherLoanTypesTableQueries;
import com.icubed.loansticdroid.util.BitmapUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.support.constraint.Constraints.TAG;

public class Loan {
    private LoansQueries loansQueries;
    private LoanTableQueries loanTableQueries;
    private LoanTypeTableQueries loanTypeTableQueries;
    private LoanTypeQueries loanTypeQueries;
    private OtherLoanTypeQueries otherLoanTypeQueries;
    private OtherLoanTypesTableQueries otherLoanTypesTableQueries;
    private Activity activity;
    private BorrowersQueries borrowersQueries;
    private BorrowersTableQueries borrowersTableQueries;
    private GroupBorrowerQueries groupBorrowerQueries;
    private GroupBorrowerTableQueries groupBorrowerTableQueries;
    private int count = 0;
    private int docSize = 0;
    private int secondCount = 0;
    private int size = 0;

    public Loan(Activity activity) {
        this.activity = activity;
        loansQueries = new LoansQueries();
        loanTableQueries = new LoanTableQueries(activity.getApplication());
        loanTypeTableQueries = new LoanTypeTableQueries(activity.getApplication());
        loanTypeQueries = new LoanTypeQueries();
        otherLoanTypeQueries = new OtherLoanTypeQueries();
        otherLoanTypesTableQueries = new OtherLoanTypesTableQueries(activity.getApplication());
        borrowersQueries = new BorrowersQueries(activity);
        borrowersTableQueries = new BorrowersTableQueries(activity.getApplication());
        groupBorrowerQueries = new GroupBorrowerQueries();
        groupBorrowerTableQueries = new GroupBorrowerTableQueries(activity.getApplication());
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
                                    getLoanType(loansTable);
                                }
                            }else{
                                removeRefresher();
                                ((LoanActivity) activity).loanProgressBar.setVisibility(View.GONE);
                                ((LoanActivity) activity).emptyLayout.setVisibility(View.VISIBLE);
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
     * this methods helps to decide if the loan is of a type registered initially by the branch manager
     * or the loan type is a custom one created during the registration of the loan
     * also it also checks if the loan type exist in local storage
     * if it doesnt exist it calls up either  getOtherLoanType(loansTable) or getNormalLoanType(loansTable)
     * the above depends on whether the loan type is registered or custom
     * @param loansTable
     */
    private void getLoanType(LoansTable loansTable) {

        if(loansTable.getIsOtherLoanType()) {
            OtherLoanTypesTable otherLoanTypesTable = otherLoanTypesTableQueries.loadSingleLoanType(loansTable.getLoanTypeId());

            if(otherLoanTypesTable != null){
                if(loansTable.getBorrowerId() != null) getBorrowerDetails(loansTable.getBorrowerId());
                else getGroupDetails(loansTable.getGroupId());
            }else getOtherLoanType(loansTable);
        }
        else{
            LoanTypeTable loanTypeTable = loanTypeTableQueries.loadSingleLoanType(loansTable.getLoanTypeId());

            if(loanTypeTable != null){
                if(loansTable.getBorrowerId() != null) getBorrowerDetails(loansTable.getBorrowerId());
                else getGroupDetails(loansTable.getGroupId());
            }else getNormalLoanType(loansTable);
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

                            saveLoanTypeToLocalStorage(loanTypeTable);

                            if(loansTable.getBorrowerId() != null) getBorrowerDetails(loansTable.getBorrowerId());
                            else getGroupDetails(loansTable.getGroupId());
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    /**
     * saves registered loan type to local storage
     * @param loanTypeTable
     */
    private void saveLoanTypeToLocalStorage(LoanTypeTable loanTypeTable) {
        LoanTypeTable loanTypeTableList = loanTypeTableQueries.loadSingleLoanType(loanTypeTable.getLoanTypeId());
        if(loanTypeTableList == null) loanTypeTableQueries.insertLoanTypeToStorage(loanTypeTable);
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

                            saveOtherLoanTypeToLocalStorage(otherLoanTypesTable);

                            if(loansTable.getBorrowerId() != null) getBorrowerDetails(loansTable.getBorrowerId());
                            else getGroupDetails(loansTable.getGroupId());
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    /**
     * saves custom loan type to local storage
     * @param otherLoanTypesTable
     */
    private void saveOtherLoanTypeToLocalStorage(OtherLoanTypesTable otherLoanTypesTable) {
        OtherLoanTypesTable otherLoanTypesTableList = otherLoanTypesTableQueries.loadSingleLoanType(otherLoanTypesTable.getOtherLoanTypeId());
        if(otherLoanTypesTableList == null) otherLoanTypesTableQueries.insertLoanTypeToStorage(otherLoanTypesTable);
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

        ((LoanActivity) activity).loanDetailsList.clear();
        size = loansTable.size();

        ((LoanActivity) activity).emptyLayout.setVisibility(View.GONE);
        for(LoansTable table : loansTable){

            if(table.getBorrowerId() != null) {
                BorrowersTable borrowersTable = borrowersTableQueries.loadSingleBorrower(table.getBorrowerId());

                if(table.getIsOtherLoanType()){
                    OtherLoanTypesTable otherLoanTypesTable = otherLoanTypesTableQueries.loadSingleLoanType(table.getLoanTypeId());

                    LoanDetails loanDetails = new LoanDetails();
                    loanDetails.setBorrowersTable(borrowersTable);
                    loanDetails.setLoansTable(table);
                    if(otherLoanTypesTable != null){
                        loanDetails.setOtherLoanTypesTable(otherLoanTypesTable);
                        ((LoanActivity) activity).loanDetailsList.add(loanDetails);
                        secondCount++;
                        ((LoanActivity) activity).loanRecyclerAdapter.notifyDataSetChanged();
                    }
                    else getOtherLoanTypeForWhenDueCollection(table, loanDetails);

                }else{
                    LoanTypeTable loanTypeTable = loanTypeTableQueries.loadSingleLoanType(table.getLoanTypeId());

                    LoanDetails loanDetails = new LoanDetails();
                    loanDetails.setBorrowersTable(borrowersTable);
                    loanDetails.setLoansTable(table);
                    if(loanTypeTable != null){
                        loanDetails.setLoanTypeTable(loanTypeTable);
                        ((LoanActivity) activity).loanDetailsList.add(loanDetails);
                        secondCount++;
                        ((LoanActivity) activity).loanRecyclerAdapter.notifyDataSetChanged();
                    }
                    else getLoanTypeForWhenDueCollection(table, loanDetails);
                }
            }else{
                GroupBorrowerTable groupBorrowerTable = groupBorrowerTableQueries.loadSingleBorrowerGroup(table.getGroupId());

                if(table.getIsOtherLoanType()){
                    OtherLoanTypesTable otherLoanTypesTable = otherLoanTypesTableQueries.loadSingleLoanType(table.getLoanTypeId());

                    LoanDetails loanDetails = new LoanDetails();
                    loanDetails.setGroupBorrowerTable(groupBorrowerTable);
                    loanDetails.setLoansTable(table);
                    if(otherLoanTypesTable != null){
                        loanDetails.setOtherLoanTypesTable(otherLoanTypesTable);
                        ((LoanActivity) activity).loanDetailsList.add(loanDetails);
                        secondCount++;
                        ((LoanActivity) activity).loanRecyclerAdapter.notifyDataSetChanged();
                    }
                    else getOtherLoanTypeForWhenDueCollection(table, loanDetails);
                }else{
                    LoanTypeTable loanTypeTable = loanTypeTableQueries.loadSingleLoanType(table.getLoanTypeId());

                    LoanDetails loanDetails = new LoanDetails();
                    loanDetails.setGroupBorrowerTable(groupBorrowerTable);
                    loanDetails.setLoansTable(table);
                    if(loanTypeTable != null){
                        loanDetails.setLoanTypeTable(loanTypeTable);
                        ((LoanActivity) activity).loanDetailsList.add(loanDetails);
                        secondCount++;
                        ((LoanActivity) activity).loanRecyclerAdapter.notifyDataSetChanged();
                    }
                    else getLoanTypeForWhenDueCollection(table, loanDetails);
                }
            }
        }

        if(secondCount == size) ((LoanActivity) activity).loanProgressBar.setVisibility(View.GONE);
    }

    private void getOtherLoanTypeForWhenDueCollection(LoansTable table, final LoanDetails loanDetails) {
        otherLoanTypeQueries.retrieveSingleOtherLoanType(table.getLoanTypeId())
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            OtherLoanTypesTable otherLoanTypesTable = task.getResult().toObject(OtherLoanTypesTable.class);
                            otherLoanTypesTable.setOtherLoanTypeId(task.getResult().getId());

                            saveOtherLoanTypeToLocalStorage(otherLoanTypesTable);
                            loanDetails.setOtherLoanTypesTable(otherLoanTypesTable);
                            ((LoanActivity) activity).loanDetailsList.add(loanDetails);
                            secondCount++;
                            ((LoanActivity) activity).loanRecyclerAdapter.notifyDataSetChanged();

                            if(secondCount==size) ((LoanActivity) activity).loanProgressBar.setVisibility(View.GONE);
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void getLoanTypeForWhenDueCollection(LoansTable table, final LoanDetails loanDetails) {
        loanTypeQueries.retrieveSingleLoanType(table.getLoanTypeId())
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            LoanTypeTable loanTypeTable = task.getResult().toObject(LoanTypeTable.class);
                            loanTypeTable.setLoanTypeId(task.getResult().getId());

                            saveLoanTypeToLocalStorage(loanTypeTable);
                            loanDetails.setLoanTypeTable(loanTypeTable);
                            ((LoanActivity) activity).loanDetailsList.add(loanDetails);
                            secondCount++;
                            ((LoanActivity) activity).loanRecyclerAdapter.notifyDataSetChanged();

                            if(secondCount==size) ((LoanActivity) activity).loanProgressBar.setVisibility(View.GONE);
                        }else{
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
                                        getLoanType(loansTable);
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
        ((LoanActivity) activity).swipeRefreshLayout.setRefreshing(false);
        ((LoanActivity) activity).swipeRefreshLayout.destroyDrawingCache();
        ((LoanActivity) activity).swipeRefreshLayout.clearAnimation();
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
