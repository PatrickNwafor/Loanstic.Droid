package com.icubed.loansticdroid.fragments.ViewLoans;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.icubed.loansticdroid.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleLoanViewFragment extends Fragment {


    public SingleLoanViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_loan_view, container, false);
    }

}
