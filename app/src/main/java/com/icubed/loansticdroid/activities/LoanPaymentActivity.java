package com.icubed.loansticdroid.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.cloudqueries.PaymentQueries;
import com.icubed.loansticdroid.fragments.PaymentFragment.CollectionPaymentFragment;
import com.icubed.loansticdroid.fragments.PaymentFragment.LoanPaymentFragments;
import com.icubed.loansticdroid.fragments.RepaymentFragment.LoanRepaymentFragment;
import com.icubed.loansticdroid.fragments.RepaymentFragment.SavingsPaymentFragment;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;

public class LoanPaymentActivity extends AppCompatActivity {

    private CollectionPaymentFragment collectionPaymentFragment;
    private LoanPaymentFragments loanPaymentFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_payment);

        Toolbar toolbar = findViewById(R.id.repayment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Loan Payments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        collectionPaymentFragment = new CollectionPaymentFragment();
        loanPaymentFragments = new LoanPaymentFragments();

        startFragment(loanPaymentFragments, "payment");

        //segmented control
        SegmentedButtonGroup sbg = findViewById(R.id.segmentedButtonGroup);
        sbg.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition(){
            @Override
            public void onClickedButtonPosition(int position){
                if(position==0) {
                    startFragment(loanPaymentFragments, "payment");
                }
                else if (position==1) {
                    startFragment(collectionPaymentFragment, "collection");
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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.action_search:
                // searchBorrowerEditText.setVisibility(View.VISIBLE);
                // searchBorrowerEditText.requestFocus();
                // KeyboardUtil.showKeyboard(BorrowerActivity.this);
                return true;



            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
