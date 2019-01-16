package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.icubed.loansticdroid.R;

public class SelectGroupLeader extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group_leader);

        toolbar = findViewById(R.id.select_group_leader_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select group leader");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }



    public void collectionLocation(View view) {
        Intent mainActivityIntent = new Intent(SelectGroupLeader.this, SelectCollectionLocation.class);
        startActivity(mainActivityIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {


            case android.R.id.home:
                onBackPressed();
                return true;

           /* case R.id.action_search:
                searchEditText.setVisibility(View.VISIBLE);
                searchEditText.requestFocus();
                showKeyboard();
                return true;*/



            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
