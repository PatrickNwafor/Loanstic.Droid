package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.fragments.EditLoanPageFragments.CollectionFragment;
import com.icubed.loansticdroid.fragments.EditLoanPageFragments.LoanScheduleFragment;
import com.icubed.loansticdroid.fragments.EditLoanPageFragments.LoanTermFragment;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.localdatabase.LoanTypeTable;
import com.icubed.loansticdroid.localdatabase.LoansTable;
import com.icubed.loansticdroid.localdatabase.OtherLoanTypesTable;

import java.util.Date;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;

public class LoanEditPage extends AppCompatActivity {
    private static final String TAG = ".LoanEditPage";
    private Toolbar toolbar;
    public SegmentedButtonGroup sbg;

    private BorrowersTable borrower;
    private GroupBorrowerTable group;
    public LoansTable loan;
    public LoanTypeTable loanTypeTable;
    public OtherLoanTypesTable otherLoanTypesTable;

    //fragments
    CollectionFragment collectionFragment;
    LoanScheduleFragment loanScheduleFragment;
    LoanTermFragment loanTermFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_edit_page);

        borrower = getIntent().getParcelableExtra("borrower");
        group = getIntent().getParcelableExtra("group");
        loan = getIntent().getParcelableExtra("loan");
        loan.setLoanApprovedDate((Date) getIntent().getSerializableExtra("approved"));
        loan.setLastUpdatedAt((Date) getIntent().getSerializableExtra("update"));
        loan.setLoanCreationDate((Date) getIntent().getSerializableExtra("create"));
        loan.setLoanReleaseDate((Date) getIntent().getSerializableExtra("release"));
        loanTypeTable = getIntent().getParcelableExtra("loanType");
        otherLoanTypesTable = getIntent().getParcelableExtra("otherLoanType");

        Log.d(TAG, "onCreate: "+loan.toString());

        toolbar = findViewById(R.id.borrower_toolbar);
        setSupportActionBar(toolbar);

        if(borrower != null) getSupportActionBar().setTitle(borrower.getFirstName()+" "+borrower.getLastName());
        else getSupportActionBar().setTitle(group.getGroupName());

        getSupportActionBar().setSubtitle("loan #100098");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        collectionFragment = new CollectionFragment();
        loanScheduleFragment = new LoanScheduleFragment();
        loanTermFragment = new LoanTermFragment();

        startFragment(collectionFragment, "collection_frag");

        //segmented control
        sbg = findViewById(R.id.segmentedButtonGroup);
        sbg.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition(){
            @Override
            public void onClickedButtonPosition(int position){
                if(position == 2){
                    startFragment(collectionFragment, "collection_frag");

                }else if(position == 0){
                    startFragment(loanTermFragment, "loan_terms_frag");
                }else if(position == 1){
                    startFragment(loanScheduleFragment, "loan_schedule_frag");
                }
            }
        });
    }

    /************Instantiate fragment transactions**********/
    public void startFragment(Fragment fragment, String fragmentTag){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, fragment, fragmentTag);
        transaction.commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.repayment_menu, menu);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.add_repayment:
               startAnotherActivity(PaymentActivity.class);
                return true;



            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void startAnotherActivity(Class newActivity){
        Intent newActivityIntent = new Intent(this, newActivity);
        startActivity(newActivityIntent);
    }

}
