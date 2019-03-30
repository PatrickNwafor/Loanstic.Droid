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
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.cloudqueries.SavingsPlanTypeQueries;
import com.icubed.loansticdroid.cloudqueries.SavingsQueries;
import com.icubed.loansticdroid.fragments.RepaymentFragment.SavingsPaymentFragment;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.BorrowersTableQueries;
import com.icubed.loansticdroid.localdatabase.SavingsPlanTypeTable;
import com.icubed.loansticdroid.localdatabase.SavingsPlanTypeTableQueries;
import com.icubed.loansticdroid.localdatabase.SavingsTableQueries;
import com.icubed.loansticdroid.localdatabase.SavingsTable;
import com.icubed.loansticdroid.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class Savings {
    private SavingsQueries savingsQueries;
    private SavingsTableQueries savingsTableQueries;
    private BorrowersQueries borrowersQueries;
    private BorrowersTableQueries borrowersTableQueries;
    private int count = 0;
    private int docSize = 0;
    private int secondCount = 0;
    private int size = 0;
    private FragmentActivity activity;
    private SavingsPaymentFragment fragment;
    private SavingsPlanTypeQueries savingsPlanTypeQueries;
    private SavingsPlanTypeTableQueries savingsPlanTypeTableQueries;


    public Savings(FragmentActivity activity) {
        this.activity = activity;
        savingsQueries = new SavingsQueries();
        savingsTableQueries = new SavingsTableQueries(activity.getApplication());
        borrowersQueries = new BorrowersQueries(activity);
        borrowersTableQueries = new BorrowersTableQueries(activity.getApplication());
        savingsPlanTypeQueries = new SavingsPlanTypeQueries();
        savingsPlanTypeTableQueries= new SavingsPlanTypeTableQueries(activity.getApplication());

        FragmentManager fm = activity.getSupportFragmentManager();
        fragment = (SavingsPaymentFragment) fm.findFragmentByTag("savings");
    }

    /**
     * checks if savings data exist in local storage
     * @return
     */
    public Boolean doesSavingsTableExistInLocalStorage(){
        List<SavingsTable> savingsTable = savingsTableQueries.loadAllSavings();
        return !savingsTable.isEmpty();
    }

    /**
     * this method retrieves all savingss from the cloud
     * @// TODO: 09/02/2019 remember to later change to savings query to have a limit of data returned at once and add lazy loading features to the recycler view
     */
    public void loadAllSavings(){

        savingsQueries.retrieveAllSavings()
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
                loadSavingsToUI();
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
                                loadSavingsToUI();
                            }
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void checkSavingsType(SavingsTable savingsTable){
        if(savingsTable.getSavingsPlanTypeId() != null){
            getNewSavingsType(savingsTable);
        }else getBorrowerDetails(savingsTable.getBorrowerId());
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
                            getBorrowerDetails(savingsTable.getBorrowerId());
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
        List<SavingsTable> savingsTable = savingsTableQueries.loadAllSavingsOrderByCreationDate();

        fragment.savingsDetailsList.clear();
        size = savingsTable.size();

        fragment.emptyCollection.setVisibility(View.GONE);
        for(SavingsTable table : savingsTable){
            BorrowersTable borrowersTable = borrowersTableQueries.loadSingleBorrower(table.getBorrowerId());

            if(borrowersTable != null) {
                SavingsDetails savingsDetails = new SavingsDetails();
                savingsDetails.setBorrowersTable(borrowersTable);
                savingsDetails.setSavingsTable(table);

                if(table.getSavingsPlanTypeId() == null) savingsDetails.setSavingsPlanTypeTable(null);
                else{
                    SavingsPlanTypeTable savingsPlanTypeTable = savingsPlanTypeTableQueries.loadSingleSavingsPlanType(table.getSavingsPlanTypeId());
                    savingsDetails.setSavingsPlanTypeTable(savingsPlanTypeTable);
                }

                fragment.savingsDetailsList.add(savingsDetails);
                secondCount++;
                fragment.savingsRecyclerAdapter.notifyDataSetChanged();
            }else{
                getSingleBorrowerDetails(table.getBorrowerId(), table);
            }
        }

        if(secondCount == size) fragment.progressBar.setVisibility(View.GONE);
    }

    private void getSingleBorrowerDetails(String borrowerId, final SavingsTable table) {
        borrowersQueries.retrieveSingleBorrowers(borrowerId)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            BorrowersTable borrowersTable = task.getResult().toObject(BorrowersTable.class);
                            borrowersTable.setBorrowersId(task.getResult().getId());

                            saveBorrowerToLocalStorage(borrowersTable);

                            SavingsDetails savingsDetails = new SavingsDetails();
                            savingsDetails.setBorrowersTable(borrowersTable);
                            savingsDetails.setSavingsTable(table);

                            fragment.savingsDetailsList.add(savingsDetails);
                            secondCount++;
                            fragment.savingsRecyclerAdapter.notifyDataSetChanged();
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
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
                                Toast.makeText(activity, "Document is empty", Toast.LENGTH_SHORT).show();
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
        if(fragment != null) {
            fragment.swipeRefreshLayout.setRefreshing(false);
            fragment.swipeRefreshLayout.destroyDrawingCache();
            fragment.swipeRefreshLayout.clearAnimation();
        }
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

}
