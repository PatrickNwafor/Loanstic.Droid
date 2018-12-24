package com.icubed.loansticdroid.models;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.BorrowersTableQueries;

import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class Borrower {

    private BorrowersQueries borrowersQueries;
    private BorrowersTableQueries borrowersTableQueries;
    private int borrowerSize;
    private int count;

    public Borrower(Application application) {
        borrowersQueries = new BorrowersQueries();
        borrowersTableQueries = new BorrowersTableQueries(application);
    }

    public Boolean doesBorrowerDataExistInLocalStorage(){
        List<BorrowersTable> borrowersTables = borrowersTableQueries.loadAllBorrowers();

        if(borrowersTables.isEmpty()){
            return false;
        }

        return true;

    }

    public void retrieveAllNewBorrowers(){

        if(!doesBorrowerDataExistInLocalStorage()) {
            borrowersQueries.retrieveAllBorrowers().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: Success in retrieving borrowers");

                        if (!task.getResult().isEmpty()) {
                            Log.d(TAG, "onComplete: Retrieved results contains new borrowers");

                            borrowerSize = task.getResult().size();

                            for (DocumentSnapshot doc : task.getResult()) {
                                BorrowersTable borrowersTable = doc.toObject(BorrowersTable.class);
                                borrowersTable.setBorrowersId(doc.getId());

                                saveBorrowerToLocalStorage(borrowersTable);
                                count++;
                            }
                        }
                    } else {
                        Log.d(TAG, "onComplete: Failed to retrieve borrowers");
                    }
                }
            });

        }
    }

    private void saveBorrowerToLocalStorage(BorrowersTable borrowersTable) {
        borrowersTableQueries.insertBorrowersToStorage(borrowersTable);

        if(borrowerSize == count){
            retrieveAllBorrowerFromLocalStorage();
            count = 0;
        }
    }

    private void saveSingleBorrowerToLocalStorage(BorrowersTable borrowersTable){
        borrowersTableQueries.insertBorrowersToStorage(borrowersTable);

        retrieveSingleBorrowerFromLocalStorage(borrowersTable.getBorrowersId());
    }

    private void retrieveAllBorrowerFromLocalStorage(){
        List<BorrowersTable> borrowersTables = borrowersTableQueries.loadAllBorrowers();

        for(BorrowersTable borrowersTable : borrowersTables){



        }
    }

    private void retrieveSingleBorrowerFromLocalStorage(String borrowerId){
        BorrowersTable borrowersTable = borrowersTableQueries.loadSingleBorrower(borrowerId);

    }


    public void retrieveAllNewBorrowerAndCompareToLocalStorage(){

        final List<BorrowersTable> borrowersTables = borrowersTableQueries.loadAllBorrowers();

        if(doesBorrowerDataExistInLocalStorage()) {
            borrowersQueries.retrieveAllBorrowers().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: Success in retrieving borrowers");

                        if (!task.getResult().isEmpty()) {
                            Log.d(TAG, "onComplete: Retrieved results contains new borrowers");

                            Boolean doesDataAlreadyExist = false;
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {

                                for (BorrowersTable table : borrowersTables) {
                                    if (table.getBorrowersId().equals(doc.getId())) {
                                        doesDataAlreadyExist = true;
                                        Log.d(TAG, "onComplete: Borrower id of " + doc.getId() + " already exist");
                                        break;
                                    }
                                }

                                if (!doesDataAlreadyExist) {
                                    Log.d(TAG, "onComplete: collection id of " + doc.getId() + " does not exist");

                                    BorrowersTable borrowersTable = doc.toObject(BorrowersTable.class);
                                    borrowersTable.setBorrowersId(doc.getId());

                                    saveSingleBorrowerToLocalStorage(borrowersTable);
                                }

                            }

                        }
                    } else {
                        Log.d(TAG, "onComplete: Failed to retrieve borrowers");
                    }
                }
            });

        }
    }
}
