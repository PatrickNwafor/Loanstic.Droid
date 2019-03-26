package com.icubed.loansticdroid.fragments.RepaymentFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.LoanRepaymentRecyclerAdapter;
import com.icubed.loansticdroid.adapters.SavingsRecyclerAdapter;
import com.icubed.loansticdroid.models.LoanDetails;
import com.icubed.loansticdroid.models.LoanRepayment;
import com.icubed.loansticdroid.models.Savings;
import com.icubed.loansticdroid.models.SavingsDetails;

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

}
