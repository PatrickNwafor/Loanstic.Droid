package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.fragments.AddNewBorrowerFragment.BorrowerFilesFragment;
import com.icubed.loansticdroid.fragments.AddNewBorrowerFragment.BorrowerLocationFragment;
import com.icubed.loansticdroid.fragments.AddNewBorrowerFragment.BusinessFragment;
import com.icubed.loansticdroid.fragments.AddNewBorrowerFragment.ContactFragment;
import com.icubed.loansticdroid.fragments.AddNewBorrowerFragment.PeronalDetailsFragment;
import com.icubed.loansticdroid.fragments.AddNewBorrowerFragment.SexDOBFragment;
import com.icubed.loansticdroid.fragments.AddNewBorrowerFragment.TakeBorrowerPhotoFragment;
import com.icubed.loansticdroid.util.FormUtil;
import com.icubed.loansticdroid.util.LocationProviderUtil;

import static android.app.PendingIntent.getActivity;

public class AddSingleBorrower extends AppCompatActivity {

    public Fragment personalDetailsFragment, contactFragment, businessFragment
            , locationFragment, sexDobFragment, takeBorrowerPhotoFragment, borrowerFilesFragment;

    private FormUtil formUtil;
    private LocationProviderUtil locationProviderUtil;
    private Toolbar toolbar;
    public ActionBar actionBar;
    public TextView next, title;
    public ImageView previous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_single_borrower);

        //fragents init
        personalDetailsFragment = new PeronalDetailsFragment();
        businessFragment = new BusinessFragment();
        takeBorrowerPhotoFragment = new TakeBorrowerPhotoFragment();
        borrowerFilesFragment = new BorrowerFilesFragment();
        contactFragment = new ContactFragment();
        sexDobFragment = new SexDOBFragment();
        locationFragment = new BorrowerLocationFragment();

        toolbar = findViewById(R.id.reg_borrower_toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        setSupportActionBar(toolbar);
        View logo = getLayoutInflater().inflate(R.layout.app_bar_layout_borrower_reg, null);
        next = logo.findViewById(R.id.next);
        previous = logo.findViewById(R.id.previous);
        title = logo.findViewById(R.id.title);
        toolbar.addView(logo);

        locationProviderUtil = new LocationProviderUtil(this);
        formUtil = new FormUtil();

        //Start personal details fragment
        startFragment(personalDetailsFragment, "personalDetails");

        //Get location permission
        locationProviderUtil.getLocationPermission();
    }

    /************Instantiate fragment transactions**********/
    public void startFragment(Fragment fragment, String fragmentTag){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.add_borrower_content_frame, fragment, fragmentTag);
        transaction.commit();
    }

    /************Instantiate fragment transactions**********/
    public void startFragment(Fragment fragment, String fragmentTag, Bundle args){
        fragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.add_borrower_content_frame, fragment, fragmentTag);
        transaction.commit();
    }

    public void goToBusinessVerification(String borrowerId, String activityCycleId){
        Intent addBorrowerIntent = new Intent(this, BusinessVerification.class);
        addBorrowerIntent.putExtra("borrowerId", borrowerId);
        addBorrowerIntent.putExtra("activityCycleId", activityCycleId);
        addBorrowerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(addBorrowerIntent);
        //finish();
    }

    public Boolean isAnyFormEmpty(EditText[] forms){
        Boolean isFormEmpty = false;
        boolean[] listOfFormsEmpty = formUtil.isListOfFormsEmpty(forms);

        for(int i = 0; i < forms.length; i++){
            if(listOfFormsEmpty[i]){
                forms[i].setError("Field is required");

                if(!isFormEmpty) {
                    forms[i].requestFocus();
                }

                isFormEmpty = true;
            }else{
                forms[i].setError(null);
            }
        }

        return isFormEmpty;
    }

    public Boolean doesFieldContainNumberOnly(EditText editText){
        if(!formUtil.doesFormContainNumbersOnly(editText)){
            editText.setError("Only numbers are allowed");
            return false;
        }else {
            editText.setError(null);
        }

        return true;
    }

}
