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
import com.icubed.loansticdroid.adapters.LoanRecyclerAdapter;
import com.icubed.loansticdroid.adapters.LoanRepaymentRecyclerAdapter;
import com.icubed.loansticdroid.adapters.SlideUpPanelRecyclerAdapter;
import com.icubed.loansticdroid.models.DueCollection;
import com.icubed.loansticdroid.models.DueCollectionDetails;
import com.icubed.loansticdroid.models.LoanDetails;
import com.icubed.loansticdroid.models.LoanRepayment;
import com.icubed.loansticdroid.util.AndroidUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoanRepaymentFragment extends Fragment {

    RecyclerView recyclerView;
    public SwipeRefreshLayout swipeRefreshLayout;
    public LinearLayout emptyCollection;
    public List<LoanDetails> loanDetailsList;
    public LoanRepaymentRecyclerAdapter loanRecyclerAdapter;
    public ProgressBar progressBar;
    private LoanRepayment loan;

    public LoanRepaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loan_repayment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.repayment_list);
        emptyCollection = view.findViewById(R.id.search_empty_layout);
        progressBar = view.findViewById(R.id.progressBar);

        loan = new LoanRepayment(getActivity());

        //Swipe down refresher initialization
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_dark,
                R.color.colorAccent);
        swipeRefreshListener();

        loanDetailsList = new ArrayList<>();
        loanRecyclerAdapter = new LoanRepaymentRecyclerAdapter(loanDetailsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter((loanRecyclerAdapter));

        getAllLoan();
    }

    //Swipe down refresh lstener
    private void swipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(AndroidUtils.isMobileDataEnabled(getContext())) {
                    swipeRefreshLayout.setRefreshing(true);
                    loan.loadAllLoansAndCompareToLocal();
                }else {
                    Toast.makeText(getContext(), "Request failed, please try again", Toast.LENGTH_SHORT).show();
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
}
