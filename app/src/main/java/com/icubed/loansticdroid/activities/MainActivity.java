package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Account account;
    Animation bounce;

    //Navigation Drawer Layout
    private DrawerLayout mDrawerLayout;

    //Navigation drawer menu btn
    private ImageView menuBtn;
    private Switch viewSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        account = new Account();
        menuBtn = findViewById(R.id.menu_btn);
        viewSwitch = findViewById(R.id.viewSwitch);
       // bounce = AnimationUtils.loadAnimation( this,R.anim.);

        viewSwitch.setChecked(false);

        //Replacing our frame layout with our map fragment
        MapFragment mapFragment = new MapFragment();
        startFragment(mapFragment);

        viewSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //change switch image to map
                    viewSwitch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_map, 0,0,0);

                    //Replacing the frame layout with our dashboard fragment
                    DashboardFragment dashboardFragment = new DashboardFragment();
                    startFragment(dashboardFragment, R.anim.enter_from_right, R.anim.exit_to_left);
                }else{
                    //change switch image to dashboard
                    viewSwitch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_dashboard, 0,0,0);

                    //Replacing our frame layout with our map fragment
                    MapFragment mapFragment = new MapFragment();
                    startFragment(mapFragment, R.anim.enter_from_left, R.anim.exit_to_right);
                }
            }
        });

        //Navigation Drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // set item as selected to persist highlight
                item.setChecked(true);
                // close drawer when item is tapped
                mDrawerLayout.closeDrawers();

                // Add code here to update the UI based on the item selected
                // For example, swap UI fragments here
                return navActions(item);
            }
        });

        //To bring out Navigation drawer
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    /**********Item selected on Navigation Drawer Actions*******/
    private boolean navActions(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_dashboard:
                DashboardFragment dashboardFragment = new DashboardFragment();
                startFragment(dashboardFragment);
                return true;

            case R.id.nav_collections:
                CollectionsFragment collectionsFragment = new CollectionsFragment();
                startFragment(collectionsFragment);
                return true;

            case R.id.nav_repayment:
                PaymentFragment paymentFragment = new PaymentFragment();
                startFragment(paymentFragment);
                return true;

            case R.id.nav_loans:
                LoansFragment loansFragment = new LoansFragment();
                startFragment(loansFragment);
                return true;

            case R.id.nav_savings:
                SavingsFragment savingsFragment = new SavingsFragment();
                startFragment(savingsFragment);
                return true;

            case R.id.nav_customers:
                BorrowersFragment borrowersFragment = new BorrowersFragment();
                startFragment(borrowersFragment);
                return true;

            case R.id.nav_branches:
                BranchesFragment branchesFragment = new BranchesFragment();
                startFragment(branchesFragment);
                return true;

            case R.id.nav_settings:
                SettingsFragment settingsFragment = new SettingsFragment();
                startFragment(settingsFragment);
                return true;

            case R.id.nav_signout:
                signOut();
                return true;

            default:
                return false;
        }

    }

    /************Instantiate fragment transactions**********/
    private void startFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }

    /***************Instantiate fragment transactions with animations*************/
    private void startFragment(Fragment fragment, int enter, int exit) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(enter,exit);
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }

    /************Sign Out Of Account***********/
    private void signOut(){
        account.signOutAccount();
        Log.d(TAG, "signOut: Successful");
        Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();

        //Move to Login Page
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!account.isUserLoggedIn()){
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }
}
