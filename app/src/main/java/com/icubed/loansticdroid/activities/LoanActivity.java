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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.icubed.loansticdroid.util.AndroidUtils;
import com.ramotion.foldingcell.FoldingCell;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoanActivity extends AppCompatActivity {
    public ProgressBar loanProgressBar;
    private EditText searchLoanEditText;
    Index index;
    private Toolbar toolbar;
    private Loan loan;

    public RecyclerView loanRecyclerView;
    public LoanRecyclerAdapter loanRecyclerAdapter;
    public SwipeRefreshLayout swipeRefreshLayout;
    public List<BorrowersTable> borrowersTables;
    public List<GroupBorrowerTable> groupBorrowerTables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan);

        searchLoanEditText = findViewById(R.id.searchEditText);
        loanProgressBar = findViewById(R.id.borrowerProgressBar);
        loanRecyclerView = findViewById(R.id.loan_list);

        loan = new Loan(this);
        borrowersTables = new ArrayList<>();
        groupBorrowerTables = new ArrayList<>();

        searchLoanEditTextListener();

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

        Client client = new Client("HGQ25JRZ8Y", "d4453ddf82775ee2324c47244b30a7c7");
        index = client.getIndex("Loan");

        // get our folding cell
        final FoldingCell fc = (FoldingCell) findViewById(R.id.folding_cell);

        // attach click listener to folding cell
        fc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fc.toggle(false);
            }
        });


        //getAllLoan();
    }

    private void searchLoanEditTextListener() {
        searchLoanEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchLoans(s);
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

    private void searchLoans(final Editable s) {
        if(!TextUtils.isEmpty(s.toString())) {
            //Search for data from cloud storage
            Query query = new Query(s.toString());
            query.setAttributesToRetrieve("*");
            query.setMinWordSizefor2Typos(3);
            query.setHitsPerPage(50);

            index.searchAsync(query, new CompletionHandler() {
                @Override
                public void requestCompleted(JSONObject content, AlgoliaException error) {
                    try {
                        JSONArray hits = content.getJSONArray("hits");
                        List<LoansTable> list = new ArrayList<>();

                        for (int i = 0; i < hits.length(); i++) {
                            JSONObject jsonObject = hits.getJSONObject(i);
                            String loanUser = jsonObject.getString("name");
                            String loanId = jsonObject.getString("objectID");

                            LoansTable loansTable = new LoansTable();
                            loansTable.setLoanId(loanId);
                            list.add(loansTable);
                        }

                        loanRecyclerAdapter = new LoanRecyclerAdapter(list);
                        loanRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        loanRecyclerView.setAdapter(loanRecyclerAdapter);

                        //This is to check immediately after the search to know if string is empty
                        if(TextUtils.isEmpty(s.toString())){
                            loan.loadLoansToUI();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else{
            loan.loadLoansToUI();
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
                showKeyboard();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showKeyboard() {
        View focuedView = getCurrentFocus();
        if (focuedView != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.showSoftInput(focuedView, 0);
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
