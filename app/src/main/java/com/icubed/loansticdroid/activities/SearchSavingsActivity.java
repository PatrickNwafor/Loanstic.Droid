package com.icubed.loansticdroid.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.CollectionLoanRecyclerAdapter;
import com.icubed.loansticdroid.adapters.LoanRecyclerAdapter;
import com.icubed.loansticdroid.adapters.LoanRepaymentRecyclerAdapter;
import com.icubed.loansticdroid.adapters.SavingsRecyclerAdapter;
import com.icubed.loansticdroid.models.LoanDetails;
import com.icubed.loansticdroid.models.SavingsDetails;
import com.icubed.loansticdroid.models.SearchLoan;
import com.icubed.loansticdroid.models.SearchSavings;
import com.icubed.loansticdroid.util.EditTextExtension.CustomEditText;
import com.icubed.loansticdroid.util.EditTextExtension.DrawableClickListener;
import com.icubed.loansticdroid.util.KeyboardUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchSavingsActivity extends AppCompatActivity {

    private Index index;
    private CustomEditText searchEditText;

    public RecyclerView savingsRecyclerView;
    public SavingsRecyclerAdapter savingsRecyclerAdapter;
//    public LoanRepaymentRecyclerAdapter loanRepaymentRecyclerAdapter;
//    public CollectionLoanRecyclerAdapter collectionLoanRecyclerAdapter;
    public String type;
    public List<SavingsDetails> savingsDetails;
    public ProgressBar progressBar;
    private LinearLayout searchEmptyLayout;
    private SearchSavings searchSavings;
    public String fromSavingsActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_savings);

        searchEditText = findViewById(R.id.searchEditText);
        savingsRecyclerView = findViewById(R.id.search_loan_list);
        progressBar = findViewById(R.id.search_progress_bar);
        searchEmptyLayout = findViewById(R.id.search_empty_layout);

        searchSavings = new SearchSavings(this);

        String searchString = getIntent().getStringExtra("search");
        fromSavingsActivity = getIntent().getStringExtra("from");
        searchEditText.setText(searchString);

        savingsRecyclerView.requestFocus();

        searchFieldListener();
        searchDrawableButtonListener();

        Client client = new Client("HGQ25JRZ8Y", "d4453ddf82775ee2324c47244b30a7c7");
        index = client.getIndex("Savings");

        searchSavingsFunc(searchString);
    }

    private void searchDrawableButtonListener() {
        searchEditText.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case LEFT:
                        finish();
                        break;

                    case RIGHT:
                        searchEditText.setText("");
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void searchFieldListener() {
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    if(TextUtils.isEmpty(searchEditText.getText().toString())){
                        return false;
                    }

                    KeyboardUtil.hideKeyboard(SearchSavingsActivity.this);
                    progressBar.setVisibility(View.VISIBLE);
                    savingsRecyclerView.setVisibility(View.INVISIBLE);
                    searchEmptyLayout.setVisibility(View.GONE);
                    searchSavingsFunc(searchEditText.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void searchSavingsFunc(final String search) {

        savingsDetails = new ArrayList<>();

        //Search for data from cloud storage
        Query query = new Query(search);
        query.setAttributesToRetrieve("*");
        query.setMinWordSizefor2Typos(3);
        query.setHitsPerPage(50);

        index.searchAsync(query, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                try {
                    JSONArray hits = content.getJSONArray("hits");
                    List<String> list = new ArrayList<>();

                    for (int i = 0; i < hits.length(); i++) {
                        JSONObject jsonObject = hits.getJSONObject(i);
                        String loanId = jsonObject.getString("objectID");

                        list.add(loanId);
                    }

                    //Start getting search result from firebase firestore
                    if(!list.isEmpty())
                        searchSavings.loadAllSavings(list);
                    else {
                        searchEmptyLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }

                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
