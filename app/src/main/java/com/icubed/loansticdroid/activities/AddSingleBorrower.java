package com.icubed.loansticdroid.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.icubed.loansticdroid.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class AddSingleBorrower extends AppCompatActivity {
    Spinner sexDrp,citizenship;
    private static final String DEFAULT_LOCAL = "Nigeria";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_single_borrower);
        sexDrp =findViewById(R.id.spSex);
        citizenship = findViewById(R.id.input_citizenship);

        ArrayAdapter<CharSequence> adapterSex;
        String[] sexArr = {"Male", "Female"};
        adapterSex = new ArrayAdapter<CharSequence>(this,android.R.layout.simple_spinner_item,sexArr);
        adapterSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexDrp.setAdapter(adapterSex);
        String selectedSex  = sexDrp.getSelectedItem().toString();


        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        String country;
        for( Locale loc : locale ){
            country = loc.getDisplayCountry();
            if( country.length() > 0 && !countries.contains(country) ){
                countries.add( country );
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, countries);
        citizenship.setAdapter(adapter);
        citizenship.setSelection(adapter.getPosition(DEFAULT_LOCAL));
        String selectedCountry  = citizenship.getSelectedItem().toString();

    }





}
