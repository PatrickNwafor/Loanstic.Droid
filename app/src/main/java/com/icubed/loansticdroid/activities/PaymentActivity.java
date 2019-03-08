package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.cloudqueries.PaymentQueries;
import com.icubed.loansticdroid.util.KeyboardUtil;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;

public class PaymentActivity extends AppCompatActivity {


    public SegmentedButtonGroup sbg;
    private Toolbar toolbar;
    private PaymentQueries paymentQueries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        toolbar = findViewById(R.id.repayment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Repayments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);





        paymentQueries = new PaymentQueries();
        //segmented control
        sbg = findViewById(R.id.segmentedButtonGroup);
        sbg.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition(){
            @Override
            public void onClickedButtonPosition(int position){
                if(position==0) {
                   // searchPosition = 0;
                    //startFragment(singleBorrowerFragment, "single");
                }
                else if (position==1) {
                    //searchPosition = 1;
                    //startFragment(groupBorrowerFragment, "group");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.repayments_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {


            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_search:
               // searchBorrowerEditText.setVisibility(View.VISIBLE);
               // searchBorrowerEditText.requestFocus();
               // KeyboardUtil.showKeyboard(BorrowerActivity.this);
                return true;



            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startAnotherActivity(Class newActivity){
        Intent newActivityIntent = new Intent(this, newActivity);
        startActivity(newActivityIntent);
    }

    @Override
    public void onBackPressed() {
        /*if(searchBorrowerEditText.getVisibility() == View.VISIBLE){
            searchBorrowerEditText.setVisibility(View.GONE);
            return;
        }*/

        super.onBackPressed();
    }

}
