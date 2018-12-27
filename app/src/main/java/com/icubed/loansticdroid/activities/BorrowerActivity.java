package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.BorrowerRecyclerAdapter;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.models.Borrowers;

import java.util.ArrayList;
import java.util.List;

public class BorrowerActivity extends AppCompatActivity {

    private RecyclerView borrowerRecyclerView;
    public BorrowerRecyclerAdapter borrowerRecyclerAdapter;
    public List<BorrowersTable> borrowersTableList;
    public ProgressBar borrowerProgressBar;
    private BorrowersQueries borrowersQueries;
    private Borrowers borrowers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrower);

        borrowerRecyclerView = findViewById(R.id.borrower_list);
        borrowerProgressBar = findViewById(R.id.borrowerProgressBar);
        borrowersTableList = new ArrayList<>();
        borrowerRecyclerAdapter= new BorrowerRecyclerAdapter(borrowersTableList);
        borrowerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        borrowerRecyclerView.setAdapter(borrowerRecyclerAdapter);
        borrowers = new Borrowers(this);
        borrowersQueries = new BorrowersQueries(this);

//        //Algolia search initiation
//        Client client = new Client("HGQ25JRZ8Y", "d4453ddf82775ee2324c47244b30a7c7");
//        Index index = client.getIndex("Borrowers");


        getAllBorrowers();
    }

    private void getAllBorrowers() {
        if(!borrowers.doesBorrowersTableExistInLocalStorage()){
            borrowers.loadAllBorrowers();
        }else{
            borrowers.loadAllBorrowersAndCompareToLocal();
        }
    }

    public void backButton(View view) {
        finish();
    }

    public void add_borrower(View view) {
        Intent addBorrowerIntent = new Intent(BorrowerActivity.this, AddSingleBorrower.class);
        startActivity(addBorrowerIntent);
    }
}
