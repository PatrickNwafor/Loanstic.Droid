package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.icubed.loansticdroid.R;

public class GroupSetUpWizard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_set_up_wizard);
    }

    public void selectLeader(View view) {
        Intent mainActivityIntent = new Intent(GroupSetUpWizard.this, SelectGroupLeader.class);
        startActivity(mainActivityIntent);

    }
}
