package com.icubed.loansticdroid.fragments.EditLoanPageFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.icubed.loansticdroid.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoanScheduleFragment extends Fragment {


    public LoanScheduleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loan_schedule, container, false);
    }

}
