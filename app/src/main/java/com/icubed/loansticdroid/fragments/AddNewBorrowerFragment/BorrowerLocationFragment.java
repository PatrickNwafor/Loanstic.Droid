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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class BorrowerLocationFragment extends Fragment {


    Context context;
    private EditText cityTextView,stateTextView, zipCodeTextView;
    private Spinner citizenship;
    private static final String DEFAULT_LOCAL = "Nigeria";
    private String selectedCountry;
    Bundle bundle;


    public BorrowerLocationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = container.getContext();
        return inflater.inflate(R.layout.fragment_borrower_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        citizenship = view.findViewById(R.id.citizenship);
        cityTextView = view.findViewById(R.id.city);
        stateTextView = view.findViewById(R.id.state);
        zipCodeTextView = view.findViewById(R.id.zip_code);

        bundle = getArguments();

        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<>();
        String country;
        for( Locale loc : locale ){
            country = loc.getDisplayCountry();
            if( country.length() > 0 && !countries.contains(country) ){
                countries.add( country );
            }
        }

        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item, countries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citizenship.setAdapter(adapter);
        citizenship.setSelection(adapter.getPosition(DEFAULT_LOCAL));
        selectedCountry = citizenship.getSelectedItem().toString();

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
        ((AddSingleBorrower) context).startFragment(((AddSingleBorrower) context).sexDobFragment, "sexDOB");
    }

    private void next() {
        //Checking form
        EditText[] editTexts = new EditText[]{cityTextView, stateTextView, zipCodeTextView};
        boolean isAnyFormEmpty = ((AddSingleBorrower) context).isAnyFormEmpty(editTexts);
        if (isAnyFormEmpty)
            return;

        boolean doesFieldContainNumberOnly = ((AddSingleBorrower) context).doesFieldContainNumberOnly(zipCodeTextView);
        if(!doesFieldContainNumberOnly)
            return;

        Bundle bundle1 = bundle;
        bundle1.putString("city", cityTextView.getText().toString());
        bundle1.putString("state", stateTextView.getText().toString());
        bundle1.putString("zipcode", zipCodeTextView.getText().toString());
        bundle1.putString("nationality", selectedCountry);

        ((AddSingleBorrower) context).startFragment(((AddSingleBorrower) context).contactFragment, "contact", bundle1);
    }

}
