package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.cloudqueries.PaymentQueries;
import com.icubed.loansticdroid.fragments.RepaymentFragment.LoanRepaymentFragment;
import com.icubed.loansticdroid.fragments.RepaymentFragment.SavingsPaymentFragment;
import com.icubed.loansticdroid.util.EditTextExtension.CustomEditText;
import com.icubed.loansticdroid.util.EditTextExtension.DrawableClickListener;
import com.icubed.loansticdroid.util.KeyboardUtil;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;

public class RepaymentActivity extends AppCompatActivity {


    private SegmentedButtonGroup sbg;
    private Toolbar toolbar;
    private LoanRepaymentFragment loanRepaymentFragment;
    private SavingsPaymentFragment savingsPaymentFragment;
    private CustomEditText searchLoanEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repayment);

        toolbar = findViewById(R.id.repayment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Repayments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        searchLoanEditText = findViewById(R.id.searchEditText);

        searchDrawableButtonListener();
        searchLoanEditTextListener();

        savingsPaymentFragment = new SavingsPaymentFragment();
        loanRepaymentFragment = new LoanRepaymentFragment();

        startFragment(loanRepaymentFragment, "repayment");

        //segmented control
        sbg = findViewById(R.id.segmentedButtonGroup);
        sbg.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition(){
            @Override
            public void onClickedButtonPosition(int position){
                if(position==0) {
                    startFragment(loanRepaymentFragment, "repayment");
                }
                else if (position==1) {
                    startFragment(savingsPaymentFragment, "savings");
                }
            }
        });
    }

    private void searchDrawableButtonListener() {
        searchLoanEditText.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case LEFT:
                        KeyboardUtil.hideKeyboard(RepaymentActivity.this);
                        searchLoanEditText.setVisibility(View.GONE);
                        break;

                    case RIGHT:
                        searchLoanEditText.setText("");
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void searchLoanEditTextListener() {
        searchLoanEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    if(TextUtils.isEmpty(searchLoanEditText.getText().toString())){
                        return false;
                    }

                    Intent intent = new Intent(RepaymentActivity.this, LoanSearchActivity.class);
                    intent.putExtra("search", searchLoanEditText.getText().toString());
                    intent.putExtra("from", false);
                    startActivity(intent);
                    searchLoanEditText.setText("");
                    searchLoanEditText.setVisibility(View.GONE);
                    return true;
                }
                return false;
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

        getMenuInflater().inflate(R.menu.repayments_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.action_search:
                searchLoanEditText.setVisibility(View.VISIBLE);
                searchLoanEditText.requestFocus();
                KeyboardUtil.showKeyboard(RepaymentActivity.this);
                return true;



            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
