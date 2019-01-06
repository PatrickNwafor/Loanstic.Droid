package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.icubed.loansticdroid.R;

public class LetsVerifyBusiness extends AppCompatActivity {
    Button  proceed;
    Animation frombottom;
    TextView almostThere,letsVerify;
    String borrowerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lets_verify_business);

        almostThere = findViewById(R.id.textView4);
        letsVerify = findViewById(R.id.textView5);
        proceed = findViewById(R.id.proceed);
        frombottom = AnimationUtils.loadAnimation( this,R.anim.frombottom);

        borrowerId = getIntent().getStringExtra("borrowerId");

        letsVerify.setAnimation(frombottom);
        proceed.setAnimation(frombottom);
    }

    public void proceed(View view) {
        Intent addBorrowerIntent = new Intent(LetsVerifyBusiness.this, BusinessVerification.class);
        addBorrowerIntent.putExtra("borrowerId", borrowerId);
        startActivity(addBorrowerIntent);
    }
}
