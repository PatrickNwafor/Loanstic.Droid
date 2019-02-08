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

    public Boolean doesLoansTableExistInLocalStorage(){
        List<LoansTable> loansTable = loanTableQueries.loadAllLoans();
        return !loansTable.isEmpty();
    }

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
                                Toast.makeText(activity, "Document is empty", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Log.d(TAG, "onComplete: "+task.getException().getMessage());
                        }
                    }
                });
    }

    private void getGroupDetails(String groupId) {
        List<GroupBorrowerTable> groupBorrowerTables = groupBorrowerTableQueries.loadAllGroups();

        Boolean doesDataExist = false;
        for (GroupBorrowerTable table : groupBorrowerTables) {
            if(table.getGroupId().equals(groupId)){
                doesDataExist = true;
                count++;
                if(count == docSize) {
                    loadLoansToUI();
                }
                break;
            }
        }

        if(!doesDataExist) getNewGroupDetails(groupId);

    }

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

    private void saveGroupToLocalStorage(GroupBorrowerTable groupBorrowerTable) {
        List<GroupBorrowerTable> groupBorrowerTable1 = groupBorrowerTableQueries.loadAllGroups();
        for (GroupBorrowerTable table : groupBorrowerTable1) {
            if(table.getGroupId().equals(groupBorrowerTable.getGroupId())) return;
        }

        groupBorrowerTableQueries.insertGroupToStorage(groupBorrowerTable);
    }

    private void getBorrowerDetails(String borrowerId) {
        List<BorrowersTable> borrowersTables = borrowersTableQueries.loadAllBorrowers();

        Boolean doesDataExist = false;
        for (BorrowersTable borrowersTable : borrowersTables) {
            if(borrowersTable.getBorrowersId().equals(borrowerId)){
                doesDataExist = true;
                count++;
                if(count == docSize) {
                    loadLoansToUI();
                }
                break;
            }
        }

        if(!doesDataExist) getNewBorrowerDetail(borrowerId);

    }

    private void getNewBorrowerDetail(String borrowerId) {
        borrowersQueries.retrieveSingleBorrowers(borrowerId)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            BorrowersTable borrowersTable = task.getResult().toObject(BorrowersTable.class);
                            borrowersTable.setBorrowersId(task.getResult().getId());

                            saveBorrowerToLocalStorage(borrowersTable);
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

    private void saveBorrowerToLocalStorage(BorrowersTable borrowersTable) {
        List<BorrowersTable> borrowersTables = borrowersTableQueries.loadAllBorrowers();
        for (BorrowersTable table : borrowersTables) {
            if(table.getBorrowersId().equals(borrowersTable.getBorrowersId())) return;
        }

        borrowersTableQueries.insertBorrowersToStorage(borrowersTable);
    }

    private void getLoanType(LoansTable loansTable) {

        if(loansTable.getIsOtherLoanType()) {
            List<OtherLoanTypesTable> otherLoanTypesTable = otherLoanTypesTableQueries.loadAllLoanTpes();

            Boolean doesDataExist = false;
            if(otherLoanTypesTable != null) {
                for (OtherLoanTypesTable typesTable : otherLoanTypesTable) {
                    if(typesTable.getOtherLoanTypeId().equals(loansTable.getLoanTypeId())){
                        doesDataExist = true;
                        if(loansTable.getBorrowerId() != null) getBorrowerDetails(loansTable.getBorrowerId());
                        else getGroupDetails(loansTable.getGroupId());
                        break;
                    }
                }

                if(!doesDataExist) getOtherLoanType(loansTable);
            }
        }
        else{
            List<LoanTypeTable> loanTypeTable = loanTypeTableQueries.loadAllLoanTpes();

            Boolean doesDataExist = false;
            if(loanTypeTable != null){
                for (LoanTypeTable typeTable : loanTypeTable) {
                    if(typeTable.getLoanTypeId().equals(loansTable.getLoanTypeId())){
                        doesDataExist = true;
                        if(loansTable.getBorrowerId() != null) getBorrowerDetails(loansTable.getBorrowerId());
                        else getGroupDetails(loansTable.getGroupId());
                        break;
                    }
                }

                if(!doesDataExist) getNormalLoanType(loansTable);
            }
        }
    }

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

    private void saveLoanTypeToLocalStorage(LoanTypeTable loanTypeTable) {
        List<LoanTypeTable> loanTypeTableList = loanTypeTableQueries.loadAllLoanTpes();
        for (LoanTypeTable table : loanTypeTableList) {
            if(table.getLoanTypeId().equals(loanTypeTable.getLoanTypeId())) return;
        }

        loanTypeTableQueries.insertLoanTypeToStorage(loanTypeTable);
    }
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

    private void saveOtherLoanTypeToLocalStorage(OtherLoanTypesTable otherLoanTypesTable) {
        List<OtherLoanTypesTable> otherLoanTypesTableList = otherLoanTypesTableQueries.loadAllLoanTpes();
        for (OtherLoanTypesTable table : otherLoanTypesTableList) {
            if(table.getOtherLoanTypeId().equals(otherLoanTypesTable.getOtherLoanTypeId())) return;
        }

        otherLoanTypesTableQueries.insertLoanTypeToStorage(otherLoanTypesTable);
    }

    private void saveLoanToLocalStorage(LoansTable loansTable) {
        loanTableQueries.insertLoanToStorage(loansTable);
    }

    public void loadLoansToUI(){
        List<LoansTable> loansTable = loanTableQueries.loadAllLoansOrderByCreationDate();

        List<LoanDetails> loanDetailsList = new ArrayList<>();

        for(LoansTable table : loansTable){

            if(table.getBorrowerId() != null) {
                BorrowersTable borrowersTable = borrowersTableQueries.loadSingleBorrower(table.getBorrowerId());

                if(table.getIsOtherLoanType()){
                    OtherLoanTypesTable otherLoanTypesTable = otherLoanTypesTableQueries.loadSingleLoanType(table.getLoanTypeId());

                    LoanDetails loanDetails = new LoanDetails();
                    loanDetails.setBorrowersTable(borrowersTable);
                    loanDetails.setLoansTable(table);
                    loanDetails.setOtherLoanTypesTable(otherLoanTypesTable);

                    loanDetailsList.add(loanDetails);
                }else{
                    LoanTypeTable loanTypeTable = loanTypeTableQueries.loadSingleLoanType(table.getLoanTypeId());

                    LoanDetails loanDetails = new LoanDetails();
                    loanDetails.setBorrowersTable(borrowersTable);
                    loanDetails.setLoansTable(table);
                    loanDetails.setLoanTypeTable(loanTypeTable);

                    loanDetailsList.add(loanDetails);
                }
            }else{
                GroupBorrowerTable groupBorrowerTable = groupBorrowerTableQueries.loadSingleBorrowerGroup(table.getGroupId());

                if(table.getIsOtherLoanType()){
                    OtherLoanTypesTable otherLoanTypesTable = otherLoanTypesTableQueries.loadSingleLoanType(table.getLoanTypeId());

                    LoanDetails loanDetails = new LoanDetails();
                    loanDetails.setGroupBorrowerTable(groupBorrowerTable);
                    loanDetails.setLoansTable(table);
                    loanDetails.setOtherLoanTypesTable(otherLoanTypesTable);

                    loanDetailsList.add(loanDetails);
                }else{
                    LoanTypeTable loanTypeTable = loanTypeTableQueries.loadSingleLoanType(table.getLoanTypeId());

                    LoanDetails loanDetails = new LoanDetails();
                    loanDetails.setGroupBorrowerTable(groupBorrowerTable);
                    loanDetails.setLoansTable(table);
                    loanDetails.setLoanTypeTable(loanTypeTable);

                    loanDetailsList.add(loanDetails);
                }
            }
        }

        Log.d(TAG, "loadLoansToUI: "+loanDetailsList.toString());

        ((LoanActivity) activity).loanRecyclerAdapter = new LoanRecyclerAdapter(loanDetailsList);
        ((LoanActivity) activity).loanRecyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));
        ((LoanActivity) activity).loanRecyclerView.setAdapter((((LoanActivity) activity).loanRecyclerAdapter));
        ((LoanActivity) activity).loanProgressBar.setVisibility(View.GONE);
    }

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

                                        saveLoanToLocalStorage(loansTable);
                                        getLoanType(loansTable);
                                    } else {
                                        //Update local table if any changes
                                        docSize--;
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
