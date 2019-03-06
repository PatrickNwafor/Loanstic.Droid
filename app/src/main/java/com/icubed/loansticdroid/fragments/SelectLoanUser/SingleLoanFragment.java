package com.icubed.loansticdroid.fragments.SelectLoanUser;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.NewLoanWizard;
import com.icubed.loansticdroid.adapters.BorrowerRecyclerAdapter;
import com.icubed.loansticdroid.adapters.SingleLoanRecyclerAdapter;
import com.icubed.loansticdroid.models.Borrowers;
import com.icubed.loansticdroid.models.SingleLoan;
import com.icubed.loansticdroid.util.AndroidUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleLoanFragment extends Fragment {

    public RecyclerView borrowerRecyclerView;
    public SingleLoanRecyclerAdapter singleLoanRecyclerAdapter;
    public SwipeRefreshLayout swipeRefreshLayout;
    public SingleLoan singleLoan;
    public LinearLayout emptyLayout;

    public SingleLoanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_loan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        singleLoan = new SingleLoan(getActivity());

        borrowerRecyclerView = view.findViewById(R.id.borrower_list);
        emptyLayout = view.findViewById(R.id.search_empty_layout);

        ((NewLoanWizard) getContext()).selectedGroup = null;

        //Swipe down refresher initialization
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_dark,
                R.color.colorAccent);
        swipeRefreshListener();

        getAllBorrowers();
    }

    //Swipe down refresh lstener
    private void swipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(AndroidUtils.isMobileDataEnabled(getContext())) {
                    swipeRefreshLayout.setRefreshing(true);
                    singleLoan.loadAllBorrowersAndCompareToLocal();
                }else {
                    Toast.makeText(getContext(), "Request failed, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getAllBorrowers() {
        if(!singleLoan.doesBorrowersTableExistInLocalStorage()){
            singleLoan.loadAllBorrowers();
        }else{
            swipeRefreshLayout.setRefreshing(true);
            singleLoan.loadBorrowersToUI();
            singleLoan.loadAllBorrowersAndCompareToLocal();
        }
    }
}
