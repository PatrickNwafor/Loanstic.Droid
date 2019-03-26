package com.icubed.loansticdroid.fragments.RepaymentFragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.LoanRepaymentRecyclerAdapter;
import com.icubed.loansticdroid.adapters.SavingsRecyclerAdapter;
import com.icubed.loansticdroid.models.LoanDetails;
import com.icubed.loansticdroid.models.LoanRepayment;
import com.icubed.loansticdroid.models.Savings;
import com.icubed.loansticdroid.models.SavingsDetails;
import com.icubed.loansticdroid.util.AndroidUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SavingsPaymentFragment extends Fragment {

    RecyclerView recyclerView;
    public SwipeRefreshLayout swipeRefreshLayout;
    public LinearLayout emptyCollection;
    public List<SavingsDetails> savingsDetailsList;
    public SavingsRecyclerAdapter savingsRecyclerAdapter;
    public ProgressBar progressBar;
    private Savings savings;


    public SavingsPaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_savings_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.savings_list);
        emptyCollection = view.findViewById(R.id.search_empty_layout);
        progressBar = view.findViewById(R.id.progressBar);

        savings = new Savings(getActivity());

        //Swipe down refresher initialization
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_dark,
                R.color.colorAccent);
        swipeRefreshListener();

        savingsDetailsList = new ArrayList<>();
        savingsRecyclerAdapter = new SavingsRecyclerAdapter(savingsDetailsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter((savingsRecyclerAdapter));

        getAllSavings();
    }

    //Swipe down refresh lstener
    private void swipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(AndroidUtils.isMobileDataEnabled(getContext())) {
                    swipeRefreshLayout.setRefreshing(true);
                    savings.loadAllSavingssAndCompareToLocal();
                }else {
                    Toast.makeText(getContext(), "Request failed, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getAllSavings() {
        if(!savings.doesSavingsTableExistInLocalStorage()){
            savings.loadAllSavings();
        }else{
            swipeRefreshLayout.setRefreshing(true);
            savings.loadSavingsToUI();
            savings.loadAllSavingssAndCompareToLocal();
        }
    }
}
