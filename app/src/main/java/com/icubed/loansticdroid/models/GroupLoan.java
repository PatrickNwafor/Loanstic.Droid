package com.icubed.loansticdroid.models;

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
import com.icubed.loansticdroid.activities.BorrowerActivity;
import com.icubed.loansticdroid.activities.NewLoanWizard;
import com.icubed.loansticdroid.adapters.GroupLoanRecyclerAdapter;
import com.icubed.loansticdroid.adapters.GroupRecyclerAdapter;
import com.icubed.loansticdroid.cloudqueries.GroupBorrowerQueries;
import com.icubed.loansticdroid.fragments.BorrowersFragment.GroupBorrowerFragment;
import com.icubed.loansticdroid.fragments.SelectLoanUser.GroupLoanFragment;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTableQueries;

import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class GroupLoan {

    private GroupBorrowerQueries groupBorrowerQueries;
    private GroupBorrowerTableQueries groupBorrowerTableQueries;
    FragmentActivity activity;
    GroupLoanFragment fragment;

    public GroupLoan(FragmentActivity activity) {
        this.activity = activity;
        groupBorrowerQueries = new GroupBorrowerQueries();
        groupBorrowerTableQueries = new GroupBorrowerTableQueries(activity.getApplication());

        FragmentManager fm = activity.getSupportFragmentManager();
        fragment = (GroupLoanFragment) fm.findFragmentByTag("group");
    }

    public Boolean doesGroupTableExistInLocalStorage(){
        List<GroupBorrowerTable> borrowersTables = groupBorrowerTableQueries.loadAllGroups();
        return !borrowersTables.isEmpty();
    }

    /****
     * @TODO
     * to method borrowersQueries.retrieveAllBorrowers() to borrowersQueries.retrieveAllBorrowersForLoanOfficer()
     * this is necessary so that only the borrowers assigned to a loan officer is seen on his borrowers page.
     * if the officers need to search for a non assigned borrower, he has to use the search field
     */
    public void loadAllGroups(){

        groupBorrowerQueries.retrieveAllBorrowersGroup().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().isEmpty()){

                        for(DocumentSnapshot doc : task.getResult().getDocuments()){
                            GroupBorrowerTable groupBorrowerTable = doc.toObject(GroupBorrowerTable.class);
                            groupBorrowerTable.setGroupId(doc.getId());

                            saveBorrowersToLocalStorage(groupBorrowerTable);
                        }

                        loadGroupsToUI();
                    }else{
                        removeRefresher();
                        if(fragment != null) fragment.emptyLayout.setVisibility(View.VISIBLE);
                        ((NewLoanWizard) activity).borrowerProgressBar.setVisibility(View.GONE);
                        Toast.makeText(activity, "Document is empty", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Log.d(TAG, "onComplete: "+task.getException().getMessage());
                }
            }
        });
    }

    private void saveBorrowersToLocalStorage(GroupBorrowerTable groupBorrowerTable) {
        GroupBorrowerTable groupBorrowerTable1 = groupBorrowerTableQueries.loadSingleBorrowerGroup(groupBorrowerTable.getGroupId());
        if(groupBorrowerTable1 == null) groupBorrowerTableQueries.insertGroupToStorage(groupBorrowerTable);
    }

    public void loadGroupsToUI(){
        List<GroupBorrowerTable> groupBorrowerTables = groupBorrowerTableQueries.loadAllGroupsOrderByLastName();

        ((NewLoanWizard) activity).isGroupSearch = false;

        if(fragment != null) fragment.emptyLayout.setVisibility(View.GONE);

        if(fragment != null) {
            fragment.groupLoanRecyclerAdapter = new GroupLoanRecyclerAdapter(groupBorrowerTables);
            fragment.groupRecyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));
            fragment.groupRecyclerView.setAdapter((fragment.groupLoanRecyclerAdapter));
        }

        ((NewLoanWizard) activity).borrowerProgressBar.setVisibility(View.GONE);
    }

    /****
     * @TODO
     * to method borrowersQueries.retrieveAllBorrowers() to borrowersQueries.retrieveAllBorrowersForLoanOfficer()
     * this is necessary so that only the borrowers assigned to a loan officer is seen on his borrowers page.
     * if the officers need to search for a non assigned borrower, he has to use the search field
     */
    public void loadAllGroupsAndCompareToLocal() {
        final List<GroupBorrowerTable> allGroups = groupBorrowerTableQueries.loadAllGroups();

        groupBorrowerQueries.retrieveAllBorrowersGroup()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){

                                Boolean isThereNewData = false;
                                List<GroupBorrowerTable> groupsInStorage = allGroups;
                                for(DocumentSnapshot doc : task.getResult().getDocuments()) {

                                    Boolean doesDataExist = false;
                                    for (GroupBorrowerTable bowTab : allGroups) {
                                        if (bowTab.getGroupId().equals(doc.getId())) {
                                            doesDataExist = true;
                                            groupsInStorage.remove(bowTab);
                                            Log.d(TAG, "onComplete: Group id of " + doc.getId() + " already exist");
                                            break;
                                        }
                                    }

                                    if (!doesDataExist) {
                                        Log.d(TAG, "onComplete: group id of " + doc.getId() + " does not exist");

                                        GroupBorrowerTable groupBorrowerTable = doc.toObject(GroupBorrowerTable.class);
                                        groupBorrowerTable.setGroupId(doc.getId());
                                        isThereNewData = true;

                                        saveBorrowersToLocalStorage(groupBorrowerTable);
                                    }else{
                                        //Update local table if any changes
                                        updateTable(doc);
                                    }
                                }

                                //to delete deleted borrower in cloud from storage
                                if(!groupsInStorage.isEmpty()){
                                    for(GroupBorrowerTable borowTab : groupsInStorage){
                                        deleteGroupFromLocalStorage(borowTab);
                                        Log.d("Delete", "deleted "+borowTab.getGroupId()+ " from storage");
                                    }
                                }

                                if(isThereNewData || !groupsInStorage.isEmpty()) {
                                    loadGroupsToUI();
                                }
                                removeRefresher();
                            }else{
                                Toast.makeText(activity, "Document is empty", Toast.LENGTH_SHORT).show();
                                removeRefresher();
                            }
                        }else{
                            Log.d("Group", "onComplete: "+task.getException().getMessage());
                            removeRefresher();
                        }
                    }
                });
    }

    private void deleteGroupFromLocalStorage(GroupBorrowerTable borowTab) {
        groupBorrowerTableQueries.deleteGroupFromStorage(borowTab);
    }

    private void removeRefresher(){
        if(fragment != null) {
            fragment.swipeRefreshLayout.setRefreshing(false);
            fragment.swipeRefreshLayout.destroyDrawingCache();
            fragment.swipeRefreshLayout.clearAnimation();
        }
    }

    private void updateTable(DocumentSnapshot doc) {
        GroupBorrowerTable groupBorrowerTable = doc.toObject(GroupBorrowerTable.class);
        groupBorrowerTable.setGroupId(doc.getId());

        GroupBorrowerTable currentlySaved = groupBorrowerTableQueries.loadSingleBorrowerGroup(doc.getId());
        groupBorrowerTable.setId(currentlySaved.getId());

        if(groupBorrowerTable.getLastUpdatedAt().getTime() != currentlySaved.getLastUpdatedAt().getTime()){

            groupBorrowerTableQueries.updateGroupDetails(groupBorrowerTable);
            loadGroupsToUI();
            Log.d("Group", "Group Detailed updated");

        }
    }

}
