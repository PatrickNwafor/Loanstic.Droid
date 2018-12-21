package com.icubed.loansticdroid.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.icubed.loansticdroid.R;

public class CollectionDetailsActivity extends AppCompatActivity {
    Animation bounce;
    CardView CD1,CD2,CD3,CD4,CD5,CD6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_details);
        bounce = AnimationUtils.loadAnimation( CollectionDetailsActivity.this,R.anim.bounce);
        CD1 = findViewById(R.id.CD1);
        CD2 = findViewById(R.id.CD2);
        CD3 = findViewById(R.id.CD3);
        CD4 = findViewById(R.id.CD4);
        CD5 = findViewById(R.id.CD5);
        CD6 = findViewById(R.id.CD6);

        CD1.setAnimation(bounce);
        CD2.setAnimation(bounce);
        CD3.setAnimation(bounce);
        CD4.setAnimation(bounce);
        CD5.setAnimation(bounce);
        CD6.setAnimation(bounce);


    }

    public void backButton(View view) {
        finish();
    }
}
