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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.SlideUpPanelRecyclerAdapter;
import com.icubed.loansticdroid.fragments.BranchesFragment;
import com.icubed.loansticdroid.fragments.CollectionsFragment;
import com.icubed.loansticdroid.fragments.BorrowersFragment;
import com.icubed.loansticdroid.fragments.DashboardFragment;
import com.icubed.loansticdroid.fragments.LoansFragment;
import com.icubed.loansticdroid.fragments.MapFragment;
import com.icubed.loansticdroid.fragments.RepaymentFragment;
import com.icubed.loansticdroid.fragments.SavingsFragment;
import com.icubed.loansticdroid.fragments.SettingsFragment;
import com.icubed.loansticdroid.models.Account;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Account account;

    //Navigation Drawer Layout
    private DrawerLayout mDrawerLayout;
    private LinearLayout dragView;
    private RecyclerView slideUpRecyclerView;
    private SlideUpPanelRecyclerAdapter slideUpPanelRecyclerAdapter;

    private List<String> collectionList;

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

        viewSwitch.setChecked(false);

        dragView = findViewById(R.id.dragView);
        setSlideUpPanelMaxHeight();

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

        //Recycler View for slide up panel
        collectionList = new ArrayList<>();
        slideUpRecyclerView = findViewById(R.id.list);

        slideUpRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        slideUpPanelRecyclerAdapter = new SlideUpPanelRecyclerAdapter(collectionList);
        slideUpRecyclerView.setAdapter(slideUpPanelRecyclerAdapter);

        collectionList.add(0,"Collection 1");
        collectionList.add(1,"Collection 2");
        collectionList.add(2,"Collection 3");
        collectionList.add(3,"Collection 4");
        collectionList.add(4,"Collection 5");
        collectionList.add(5,"Collection 6");
        collectionList.add(6,"Collection 7");
        collectionList.add(7,"Collection 8");

        for(String collection : collectionList){
            slideUpPanelRecyclerAdapter.notifyDataSetChanged();
        }

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
                RepaymentFragment repaymentFragment = new RepaymentFragment();
                startFragment(repaymentFragment);
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

    private void setSlideUpPanelMaxHeight(){
        //Setting Slide Panel Maximum height
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;

        ViewGroup.LayoutParams params = dragView.getLayoutParams();
        // Changes the height and width to the specified *pixels*
        params.height = height/2;

        dragView.setLayoutParams(params);
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
