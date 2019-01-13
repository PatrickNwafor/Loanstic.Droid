package com.icubed.loansticdroid.fragments.AddNewBorrowerFragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.AddSingleBorrower;
import com.icubed.loansticdroid.util.FormUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class SexDOBFragment extends Fragment {

    Context context;
    private Spinner sexDrp;
    private EditText dateOfBirthTextView;
    private String selectedSex;
    private Button nextBtn, previousBtn;
    private FormUtil formUtil;
    private Bundle bundle;

    public SexDOBFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = container.getContext();
        return inflater.inflate(R.layout.fragment_sex_dob, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sexDrp = view.findViewById(R.id.spSex);
        dateOfBirthTextView = view.findViewById(R.id.date_of_birth);
        nextBtn = view.findViewById(R.id.next1);
        previousBtn = view.findViewById(R.id.previous);
        formUtil = new FormUtil();
        bundle = getArguments();

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous();
            }
        });

        ArrayAdapter<CharSequence> adapterSex;
        String[] sexArr = {"Male", "Female"};
        adapterSex = new ArrayAdapter<CharSequence>(context,android.R.layout.simple_spinner_item,sexArr);
        adapterSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexDrp.setAdapter(adapterSex);
        selectedSex = sexDrp.getSelectedItem().toString();
    }

    private void previous() {
        ((AddSingleBorrower) context).startFragment(((AddSingleBorrower) context).personalDetailsFragment, "personalDetails");
    }

    private void next() {

        //checking form
        if(formUtil.isSingleFormEmpty(dateOfBirthTextView)){
            dateOfBirthTextView.setError("This Field is required");
            dateOfBirthTextView.requestFocus();
            return;
        }else{
            dateOfBirthTextView.setError(null);
        }

        Bundle bundle1 = bundle;
        bundle1.putString("sex", selectedSex);
        bundle1.putString("DOB", dateOfBirthTextView.getText().toString());

        ((AddSingleBorrower) context).startFragment(((AddSingleBorrower) context).locationFragment, "borrower_location", bundle1);
    }
}
