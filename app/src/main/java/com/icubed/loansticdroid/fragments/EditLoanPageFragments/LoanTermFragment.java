package com.icubed.loansticdroid.fragments.EditLoanPageFragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TextView;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.LoanEditPage;
import com.icubed.loansticdroid.localdatabase.LoanTypeTable;
import com.icubed.loansticdroid.localdatabase.LoansTable;
import com.icubed.loansticdroid.localdatabase.OtherLoanTypesTable;
import com.icubed.loansticdroid.util.DateUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoanTermFragment extends Fragment {

    private TextView loanReleaseDateTextView, loanStatusTextView, loanInterestRateTextView
            , loanDurationRateTextView, repaymentCycleRateTextView;
    private TextView loanTypeNameTextView, principlaAmountTextView, loanInterestTextView
            , loanDurationTextView, repaymentCycleTextView, loanFeesTextView, loanTypeDescTextView;
    private LoansTable loan;
    private LoanTypeTable loanTypeTable;
    private OtherLoanTypesTable otherLoanTypesTable;


    public LoanTermFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loan_term, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loan = ((LoanEditPage) getActivity()).loan;
        loanTypeTable = ((LoanEditPage) getActivity()).loanTypeTable;
        otherLoanTypesTable = ((LoanEditPage) getActivity()).otherLoanTypesTable;

        loanTypeNameTextView = view.findViewById(R.id.loan_type_name);
        principlaAmountTextView = view.findViewById(R.id.principal_amount);
        loanReleaseDateTextView= view.findViewById(R.id.loan_releae_date);
        loanInterestTextView = view.findViewById(R.id.loan_interest);
        loanDurationTextView = view.findViewById(R.id.loan_duration);
        repaymentCycleTextView = view.findViewById(R.id.repayment_cycle);
        loanFeesTextView = view.findViewById(R.id.loan_fees);
        loanTypeDescTextView = view.findViewById(R.id.loan_type_desc);
        CardView otherLoanCardView = view.findViewById(R.id.other_loan_card);
        loanStatusTextView = view.findViewById(R.id.loan_status);
        loanInterestRateTextView = view.findViewById(R.id.loan_interest_rate);
        loanDurationRateTextView = view.findViewById(R.id.spDuration);
        repaymentCycleRateTextView = view.findViewById(R.id.spCycle);

        if(loan.getIsOtherLoanType()){
            otherLoanCardView.setVisibility(View.VISIBLE);
        }

        loadDetailsToUI();
    }

    private void loadDetailsToUI() {
        loanStatusTextView.setText("Active");

        if(loan.getIsOtherLoanType()){
            loanTypeNameTextView.setText(otherLoanTypesTable.getOtherLoanTypeName());
            loanTypeDescTextView.setText(otherLoanTypesTable.getOtherLoanTypeDescription());
        }else loanTypeNameTextView.setText(loanTypeTable.getLoanTypeName());

        principlaAmountTextView.setText(String.valueOf(loan.getLoanAmount()));
        loanReleaseDateTextView.setText(DateUtil.dateString(loan.getLoanReleaseDate()));
        loanInterestTextView.setText(String.valueOf(loan.getLoanInterestRate()));
        loanInterestRateTextView.setText(loan.getLoanInterestRateUnit());
        loanDurationTextView.setText(String.valueOf(loan.getLoanDuration()));
        loanDurationRateTextView.setText(loan.getLoanDurationUnit());
        repaymentCycleTextView.setText(String.valueOf(loan.getRepaymentAmount()));
        repaymentCycleRateTextView.setText(loan.getRepaymentAmountUnit());
        loanFeesTextView.setText(String.valueOf(loan.getLoanFees()));
    }
}
