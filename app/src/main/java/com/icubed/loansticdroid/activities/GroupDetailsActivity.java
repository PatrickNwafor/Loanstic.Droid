package com.icubed.loansticdroid.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.models.SelectedBorrowerForGroup;

import java.util.List;

public class GroupDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private List<SelectedBorrowerForGroup> groupList;
    private String groupLeaderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        toolbar = findViewById(R.id.group_details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Group Registration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        groupLeaderId = getIntent().getStringExtra("groupLeaderId");
        groupList = getIntent().getParcelableArrayListExtra("selectedBorrowers");
    }
}
