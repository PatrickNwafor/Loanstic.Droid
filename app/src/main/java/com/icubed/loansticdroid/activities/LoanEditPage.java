package com.icubed.loansticdroid.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.fragments.EditLoanPageFragments.CollectionFragment;
import com.icubed.loansticdroid.fragments.EditLoanPageFragments.LoanColateralFragment;
import com.icubed.loansticdroid.fragments.EditLoanPageFragments.LoanCommentsFragment;
import com.icubed.loansticdroid.fragments.EditLoanPageFragments.LoanFilesFragment;
import com.icubed.loansticdroid.fragments.EditLoanPageFragments.LoanScheduleFragment;
import com.icubed.loansticdroid.fragments.EditLoanPageFragments.LoanTermFragment;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.localdatabase.LoansTable;
import com.icubed.loansticdroid.util.KeyboardUtil;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;

public class LoanEditPage extends AppCompatActivity {
    private Toolbar toolbar;
    public SegmentedButtonGroup sbg;

    private BorrowersTable borrower;
    private GroupBorrowerTable group;
    private LoansTable loan;

    //fragments
    CollectionFragment collectionFragment;
    LoanColateralFragment loanColateralFragment;
    LoanCommentsFragment loanCommentsFragment;
    LoanFilesFragment loanFilesFragment;
    LoanScheduleFragment loanScheduleFragment;
    LoanTermFragment loanTermFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_edit_page);

        borrower = getIntent().getParcelableExtra("borrower");
        group = getIntent().getParcelableExtra("group");
        loan = getIntent().getParcelableExtra("loan");

        toolbar = findViewById(R.id.borrower_toolbar);
        setSupportActionBar(toolbar);

        if(borrower != null) getSupportActionBar().setTitle(borrower.getFirstName()+" "+borrower.getLastName());
        else getSupportActionBar().setTitle(group.getGroupName());

        getSupportActionBar().setSubtitle("loan #100098");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        collectionFragment = new CollectionFragment();
        loanColateralFragment = new LoanColateralFragment();
        loanCommentsFragment = new LoanCommentsFragment();
        loanFilesFragment = new LoanFilesFragment();
        loanScheduleFragment = new LoanScheduleFragment();
        loanTermFragment = new LoanTermFragment();

        startFragment(collectionFragment, "collection_frag");

        //segmented control
        sbg = findViewById(R.id.segmentedButtonGroup);
        sbg.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition(){
            @Override
            public void onClickedButtonPosition(int position){
                if(position == 0){
                    startFragment(collectionFragment, "collection_frag");

                }else if(position == 1){
                    startFragment(loanTermFragment, "loan_terms_frag");
                }else if(position == 2){
                    startFragment(loanScheduleFragment, "loan_schedule_frag");
                }else if(position == 3){
                    startFragment(loanColateralFragment, "loan_colateral_frag");
                }else if(position == 4){
                    startFragment(loanFilesFragment, "loan_files_frag");
                }else if(position == 5){
                    startFragment(loanCommentsFragment, "loan_comment_frag");
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
    public boolean onPrepareOptionsMenu(Menu menu) {
       /* MenuItem register = menu.findItem(R.id.next_to_loan_terms);

        if(selectedBorrower != null || selectedGroup !=  null || lastChecked != null){
            register.setVisible(true);
        }else{
            register.setVisible(false);
        }*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.add_repayment:
               // startAnotherActivity(PaymentActivity.class);
                return true;

            case R.id.edit_loan_terms:
                //startAnotherActivity(LoanTerms.class);

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
