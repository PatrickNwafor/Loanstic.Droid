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

/**
 * A simple {@link Fragment} subclass.
 */
public class BusinessFragment extends Fragment {

    private EditText businessAddressTextView, businessNameTextView, businessDescTextView;
    Context context;
    Bundle bundle;

    public BusinessFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = container.getContext();
        return inflater.inflate(R.layout.fragment_business, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AddSingleBorrower) getContext()).next.setText("Next");

        businessAddressTextView = view.findViewById(R.id.business_address);
        businessNameTextView = view.findViewById(R.id.business_name);
        businessDescTextView = view.findViewById(R.id.description);

        bundle = getArguments();

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
        ((AddSingleBorrower) context).startFragment(((AddSingleBorrower) context).contactFragment, "contact");
    }

    private void next() {
        //Checking form
        EditText[] editTexts = new EditText[]{businessNameTextView, businessDescTextView, businessAddressTextView};
        boolean isAnyFormEmpty = ((AddSingleBorrower) context).isAnyFormEmpty(editTexts);

        if (isAnyFormEmpty)
            return;

        Bundle bundle1 = bundle;
        bundle1.putString("businessName", businessNameTextView.getText().toString());
        bundle1.putString("businessDesc", businessDescTextView.getText().toString());
        bundle1.putString("businessAddress", businessAddressTextView.getText().toString());
        ((AddSingleBorrower) context).startFragment(((AddSingleBorrower) context).takeBorrowerPhotoFragment, "borrower_photo", bundle1);
    }
}
