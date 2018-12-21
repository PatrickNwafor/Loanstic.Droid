package com.icubed.loansticdroid.activities;

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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.icubed.loansticdroid.models.Collection;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private boolean doubleBackToExitPressedOnce = false;

    private Account account;

    //Navigation Drawer Layout
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private Switch viewSwitch;
    private ProgressBar mainProgrressBar;
    private FrameLayout contentFrame;

    private Collection collection;

    //Fragments
    MapFragment mapFragment;
    DashboardFragment dashboardFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        account = new Account();
        ImageView menuBtn = findViewById(R.id.menu_btn);
        viewSwitch = findViewById(R.id.viewSwitch);
        mainProgrressBar = findViewById(R.id.mainProgressBar);
        contentFrame = findViewById(R.id.content_frame);

        viewSwitch.setChecked(false);

        //Replacing our frame layout with our map fragment
        mapFragment = new MapFragment();
        dashboardFragment = new DashboardFragment();
        startFragment(mapFragment, "home");

        viewSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //change switch image to map
                    viewSwitch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_map, 0,0,0);

                    //Replacing the frame layout with our dashboard fragment
                    startFragment(dashboardFragment, R.anim.enter_from_right, R.anim.exit_to_left, "home");
                }else{
                    //change switch image to dashboard
                    viewSwitch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_dashboard, 0,0,0);

                    //Replacing our frame layout with our map fragment
                    startFragment(mapFragment, R.anim.enter_from_left, R.anim.exit_to_right, "home");
                }
            }
        });

        //Navigation Drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
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
            case R.id.nav_home:

                viewSwitch.setVisibility(View.VISIBLE);
                startFragment(mapFragment, "home");
                return true;

            case R.id.nav_collections:
                viewSwitch.setVisibility(View.GONE);
                CollectionsFragment collectionsFragment = new CollectionsFragment();
                startFragment(collectionsFragment, "collection");
                return true;

            case R.id.nav_repayment:
                viewSwitch.setVisibility(View.GONE);
                PaymentFragment paymentFragment = new PaymentFragment();
                startFragment(paymentFragment, "payment");
                return true;

            case R.id.nav_loans:
                viewSwitch.setVisibility(View.GONE);
                LoansFragment loansFragment = new LoansFragment();
                startFragment(loansFragment, "loan");
                return true;

            case R.id.nav_savings:
                viewSwitch.setVisibility(View.GONE);
                SavingsFragment savingsFragment = new SavingsFragment();
                startFragment(savingsFragment, "savings");
                return true;

            case R.id.nav_customers:
                viewSwitch.setVisibility(View.GONE);
                BorrowersFragment borrowersFragment = new BorrowersFragment();
                startFragment(borrowersFragment, "borrowers");
                return true;

            case R.id.nav_branches:
                viewSwitch.setVisibility(View.GONE);
                BranchesFragment branchesFragment = new BranchesFragment();
                startFragment(branchesFragment, "branches");
                return true;

            case R.id.nav_settings:
                viewSwitch.setVisibility(View.GONE);
                SettingsFragment settingsFragment = new SettingsFragment();
                startFragment(settingsFragment, "settings");
                return true;

            case R.id.nav_signout:
                signOut();
                return true;

            default:
                return false;
        }

    }

    /************Instantiate fragment transactions**********/
    private void startFragment(Fragment fragment, String fragmentTag){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment, fragmentTag);
        transaction.commit();
    }

    /***************Instantiate fragment transactions with animations*************/
    private void startFragment(Fragment fragment, int enter, int exit, String fragmentTag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(enter,exit);
        transaction.replace(R.id.content_frame, fragment, fragmentTag);
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

    public void hideProgressBar(){
        contentFrame.setVisibility(View.VISIBLE);
        mainProgrressBar.setVisibility(View.GONE);
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

    @Override
    public void onBackPressed() {

        FragmentManager fm = getSupportFragmentManager();

        //hides slide up panel if already up
        MapFragment fragment = (MapFragment) fm.findFragmentByTag("home");

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        //Actions to carry out when back button is pressed depending on the state of the app;
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){

            mDrawerLayout.closeDrawers();

        }else if(fragment != null && fragment.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {

            fragment.hidePanel();

        } else if(fragment == null){

            viewSwitch.setVisibility(View.VISIBLE);
            navigationView.getMenu().getItem(0).setChecked(true);
            startFragment(mapFragment, "home");

        }else{
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }

    }


}
