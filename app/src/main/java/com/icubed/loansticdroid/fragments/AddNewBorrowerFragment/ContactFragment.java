package com.icubed.loansticdroid.fragments.AddNewBorrowerFragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.AddSingleBorrower;
import com.icubed.loansticdroid.util.FormUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {

    Context context;
    private EditText phoneNumberTextView, emailTextView, homeAddressTextView;
    private FormUtil formUtil;
    Bundle bundle;

    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = container.getContext();
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        phoneNumberTextView = view.findViewById(R.id.mobile_number);
        emailTextView = view.findViewById(R.id.email_address);
        homeAddressTextView = view.findViewById(R.id.home_address);

        bundle = getArguments();

        formUtil = new FormUtil();

        ((AddSingleBorrower) getContext()).previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous();
            }
        });

        ((AddSingleBorrower) getContext()).next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
    }

    private void previous() {
        ((AddSingleBorrower) context).startFragment(((AddSingleBorrower) context).locationFragment, "borrower_location");
    }

    private void next() {
        //Checking form
        EditText[] editTexts = new EditText[]{phoneNumberTextView, emailTextView, homeAddressTextView};
        boolean isAnyFormEmpty = ((AddSingleBorrower) context).isAnyFormEmpty(editTexts);
        if (isAnyFormEmpty)
            return;
        //Checking phone numbers
        boolean doesFieldContainNumberOnly = ((AddSingleBorrower) context).doesFieldContainNumberOnly(phoneNumberTextView);
        if(!doesFieldContainNumberOnly)
            return;
        //Checking if email format is valid
        if(!formUtil.isValidEmail(emailTextView.getText().toString())) {
            emailTextView.setError("Invalid email format");
            return;
        }

        Bundle bundle1 = bundle;
        bundle1.putString("phoneNumber", phoneNumberTextView.getText().toString());
        bundle1.putString("email", emailTextView.getText().toString());
        bundle1.putString("homeAddress", homeAddressTextView.getText().toString());
        ((AddSingleBorrower) context).startFragment(((AddSingleBorrower) context).businessFragment, "business", bundle1);

    }
}
