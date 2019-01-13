package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.icubed.loansticdroid.R;

public class SelectGroupLeader extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group_leader);
    }



    public void collectionLocation(View view) {
        Intent mainActivityIntent = new Intent(SelectGroupLeader.this, SelectLocationWizard.class);
        startActivity(mainActivityIntent);
    }
}
