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
import com.icubed.loansticdroid.activities.AddGroupBorrower;
import com.icubed.loansticdroid.activities.AddSingleBorrower;

/**
 * A simple {@link Fragment} subclass.
 */
public class PeronalDetailsFragment extends Fragment {

    private EditText firstNameTextView, middleNameTextView, lastNameTextView;
    Context context;

    public PeronalDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = container.getContext();
        return inflater.inflate(R.layout.fragment_peronal_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        firstNameTextView = v.findViewById(R.id.first_name);
        middleNameTextView = v.findViewById(R.id.middle_name);
        lastNameTextView = v.findViewById(R.id.last_name);

        ((AddSingleBorrower) getContext()).previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AddSingleBorrower) getContext()).finish();
            }
        });

        ((AddSingleBorrower) getContext()).next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToNextFragment();
            }
        });
    }

    private void moveToNextFragment() {

        //Checking form
        EditText[] editTexts = new EditText[]{firstNameTextView, lastNameTextView};
        boolean isAnyFormEmpty = ((AddSingleBorrower) context).isAnyFormEmpty(editTexts);
        if (isAnyFormEmpty) {
            return;
        }

        Bundle args = new Bundle();
        args.putString("firstName", firstNameTextView.getText().toString());
        args.putString("middleName", middleNameTextView.getText().toString());
        args.putString("lastName", lastNameTextView.getText().toString());

        ((AddSingleBorrower) context).startFragment(((AddSingleBorrower) context).sexDobFragment, "SexDOB", args);
    }
}
