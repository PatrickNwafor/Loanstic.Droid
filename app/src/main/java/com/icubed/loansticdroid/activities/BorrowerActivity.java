package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.BorrowerRecyclerAdapter;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;

import java.util.ArrayList;
import java.util.List;

public class BorrowerActivity extends AppCompatActivity {

    private RecyclerView borrowerRecyclerView;
    private BorrowerRecyclerAdapter borrowerRecyclerAdapter;
    private List<BorrowersTable> borrowersTableList;
    private BorrowersQueries borrowersQueries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrower);

        borrowerRecyclerView = findViewById(R.id.borrower_list);
        borrowersTableList = new ArrayList<>();
        borrowerRecyclerAdapter= new BorrowerRecyclerAdapter(borrowersTableList);
        borrowerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        borrowerRecyclerView.setAdapter(borrowerRecyclerAdapter);

        borrowersQueries = new BorrowersQueries(this);
        getAllBorrowers();
    }

    public void getAllBorrowers(){
        borrowersQueries.retrieveAllBorrowers()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().isEmpty()){
                                for(DocumentSnapshot doc : task.getResult().getDocuments()){
                                    BorrowersTable borrowersTable = doc.toObject(BorrowersTable.class);
                                    borrowersTable.setBorrowersId(doc.getId());

                                    borrowersTableList.add(borrowersTable);
                                    borrowerRecyclerAdapter.notifyDataSetChanged();
                                }
                            }
                        }else{
                            Toast.makeText(BorrowerActivity.this, "Failed to retrieve borrowers", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void backButton(View view) {
        finish();
    }

    public void add_borrower(View view) {
        Intent addBorrowerIntent = new Intent(BorrowerActivity.this, AddSingleBorrower.class);
        startActivity(addBorrowerIntent);
    }
}
