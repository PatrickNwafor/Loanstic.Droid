package com.icubed.loansticdroid.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.fragments.BranchesFragment;
import com.icubed.loansticdroid.fragments.CollectionsFragment;
import com.icubed.loansticdroid.fragments.BorrowersFragment;
import com.icubed.loansticdroid.fragments.DashboardFragment;
import com.icubed.loansticdroid.fragments.LoansFragment;
import com.icubed.loansticdroid.fragments.MapFragment;
import com.icubed.loansticdroid.fragments.PaymentFragment;
import com.icubed.loansticdroid.fragments.SavingsFragment;
import com.icubed.loansticdroid.fragments.SettingsFragment;
import com.icubed.loansticdroid.models.Account;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

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


        onBackPressed();




    }
}
