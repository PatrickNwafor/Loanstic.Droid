package com.icubed.loansticdroid.fragments.AddNewBorrowerFragment;


import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.AddSingleBorrower;
import com.icubed.loansticdroid.activities.LoanTerms;
import com.icubed.loansticdroid.util.FormUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class SexDOBFragment extends Fragment {

    Context context;
    private Spinner sexDrp;
    EditText dateOfBirthTextView;
    private String selectedSex;
    private Button nextBtn, previousBtn;
    private FormUtil formUtil;
    private Bundle bundle;
    final Calendar myCalendar = Calendar.getInstance();

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




        dateOfBirthTextView= (EditText) view.findViewById(R.id.date_of_birth);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        dateOfBirthTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



    }
    private void updateLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateOfBirthTextView.setText(sdf.format(myCalendar.getTime()));
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
