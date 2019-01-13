package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.models.SelectedBorrowerForGroup;

import java.util.ArrayList;

public class GroupSetUpWizard extends AppCompatActivity {

    ArrayList<SelectedBorrowerForGroup> selectedBorrowerForGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_set_up_wizard);

        selectedBorrowerForGroups = getIntent().getParcelableArrayListExtra("selectedBorrowers");
        Log.i("SelectedGroup", selectedBorrowerForGroups.toString());
    }

    public void selectLeader(View view) {
        Intent mainActivityIntent = new Intent(GroupSetUpWizard.this, SelectGroupLeader.class);
        startActivity(mainActivityIntent);

    }
}
