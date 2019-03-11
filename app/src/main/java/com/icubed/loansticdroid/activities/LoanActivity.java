package com.icubed.loansticdroid.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.LoanRecyclerAdapter;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.localdatabase.LoansTable;
import com.icubed.loansticdroid.models.Loan;
import com.icubed.loansticdroid.models.LoanDetails;
import com.icubed.loansticdroid.util.AndroidUtils;
import com.icubed.loansticdroid.util.EditTextExtension.CustomEditText;
import com.icubed.loansticdroid.util.EditTextExtension.DrawableClickListener;
import com.icubed.loansticdroid.util.KeyboardUtil;
import com.ramotion.foldingcell.FoldingCell;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoanActivity extends AppCompatActivity {
    public ProgressBar loanProgressBar;
    private CustomEditText searchLoanEditText;
    private Toolbar toolbar;
    private Loan loan;

    public RecyclerView loanRecyclerView;
    public LoanRecyclerAdapter loanRecyclerAdapter;
    public SwipeRefreshLayout swipeRefreshLayout;
    public List<LoanDetails> loanDetailsList;
    public LinearLayout emptyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan);

        searchLoanEditText = findViewById(R.id.searchEditText);
        loanProgressBar = findViewById(R.id.borrowerProgressBar);
        loanRecyclerView = findViewById(R.id.loan_list);
        emptyLayout = findViewById(R.id.search_empty_layout);

        loan = new Loan(this);
        searchLoanEditTextListener();
        searchDrawableButtonListener();

        //Swipe down refresher initialization
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_dark,
                R.color.colorAccent);
        swipeRefreshListener();

        toolbar = findViewById(R.id.loan_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Loans");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loanDetailsList = new ArrayList<>();
        loanRecyclerAdapter = new LoanRecyclerAdapter(loanDetailsList);
        loanRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        loanRecyclerView.setAdapter((loanRecyclerAdapter));

        getAllLoan();
    }

    private void searchDrawableButtonListener() {
        searchLoanEditText.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case LEFT:
                        KeyboardUtil.hideKeyboard(LoanActivity.this);
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

                    Intent intent = new Intent(LoanActivity.this, LoanSearchActivity.class);
                    intent.putExtra("search", searchLoanEditText.getText().toString());
                    intent.putExtra("from", true);
                    startActivity(intent);
                    searchLoanEditText.setText("");
                    searchLoanEditText.setVisibility(View.GONE);
                    return true;
                }
                return false;
            }
        });
    }

    //Swipe down refresh listener
    private void swipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(AndroidUtils.isMobileDataEnabled(getApplicationContext())) {
                    swipeRefreshLayout.setRefreshing(true);
                    loan.loadAllLoansAndCompareToLocal();
                }else {
                    Toast.makeText(getApplicationContext(), "Request failed, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getAllLoan() {
        if(!loan.doesLoansTableExistInLocalStorage()){
            loan.loadAllLoan();
        }else{
            swipeRefreshLayout.setRefreshing(true);
            loan.loadLoansToUI();
            loan.loadAllLoansAndCompareToLocal();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.loans_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.add_loan:
                startAnotherActivity(NewLoanWizard.class);
                return true;

            case R.id.search_loan:
                searchLoanEditText.setVisibility(View.VISIBLE);
                searchLoanEditText.requestFocus();
                KeyboardUtil.showKeyboard(LoanActivity.this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startAnotherActivity(Class newActivity){
        Intent newActivityIntent = new Intent(this, newActivity);
        startActivity(newActivityIntent);
    }

    @Override
    public void onBackPressed() {
        if(searchLoanEditText.getVisibility() == View.VISIBLE){
            searchLoanEditText.setVisibility(View.GONE);
            return;
        }

        super.onBackPressed();
    }

}
