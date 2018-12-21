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

public class CollectionDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_details);
    }
}
