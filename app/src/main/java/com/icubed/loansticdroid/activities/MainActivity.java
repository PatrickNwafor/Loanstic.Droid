package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.UploadTask;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.cloudqueries.SavingsPlanTypeQueries;
import com.icubed.loansticdroid.fragments.HomeFragments.DashboardFragment;
import com.icubed.loansticdroid.fragments.HomeFragments.MapFragment;
import com.icubed.loansticdroid.cloudqueries.Account;
import com.icubed.loansticdroid.models.GetPaymentMode;
import com.icubed.loansticdroid.util.BitmapUtil;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private boolean doubleBackToExitPressedOnce = false;

    private Account account;

    //Navigation Drawer Layout
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    public ToggleButton viewSwitch1;
    private FrameLayout contentFrame;
    private GetPaymentMode getPaymentMode;
    private SavingsPlanTypeQueries savingsPlanTypeQueries;

    //Fragments
    MapFragment mapFragment;
    DashboardFragment dashboardFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        account = new Account();
        ImageView menuBtn = findViewById(R.id.menu_btn);
        viewSwitch1 = findViewById(R.id.toggleButton);
        contentFrame = findViewById(R.id.content_frame);

        savingsPlanTypeQueries = new SavingsPlanTypeQueries();
        //startAnotherActivity(SavingsPickPlan.class);

        //Replacing our frame layout with our map fragment
        mapFragment = new MapFragment();
        dashboardFragment = new DashboardFragment();
        startFragment(mapFragment, "home");

        viewSwitch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    startFragment(dashboardFragment, R.anim.enter_from_right, R.anim.exit_to_left, "home");
                }
                else {
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
                item.setChecked(false);
                // Add code here to update the UI based on the item selected
                // For example, swap UI fragments here
                return navActions(item);
            }
        });

        View headerView = navigationView.getHeaderView(0);
        TextView nav_email = headerView.findViewById(R.id.nav_email);
        TextView nav_username= headerView.findViewById(R.id.nav_username);
        CircleImageView nav_image = headerView.findViewById(R.id.nav_image);

        if (account.getCurrentUser() != null){
            nav_email.setText(account.getCurrentUser().getEmail());
        }


        //To bring out Navigation drawer
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        getPaymentMode = new GetPaymentMode(this);
        getPaymentMode.getPaymentMode();

    }

//    private void uploadplan() {
//        int[] drawable = new int[]{R.drawable.car_loan, R.drawable.car_loan, R.drawable.car_loan, R.drawable.car_loan, R.drawable.car_loan, R.drawable.car_loan, R.drawable.car_loan};
//        String[] name = new String[]{"Vacation", "Education", "Family", "Emergency Funds", "Investment Or Business", "Retirement", "Home"};
//        String[] desc = new String[]{"Save up for that vacation or time out you've always wanted", "Save up funds for your education or for that of your loved ones", "Family is everything. Grow wealth for your future self and for your loved ones",
//                "Set aside savings to cater for emergencies.", "Save up for capital for your business idea.", "Retirement is sweet when you plan ahead. The earlier you start the better.", "Save up for rent or for your own apartment"};
//        String[] abbr = new String[]{"VAC", "EDU", "FII", "EMF", "IOB", "RET", "HOM"};
//
//        int count  = 0;
//        for (int i : drawable) {
//
//            final Map<String, Object> objectMap = new HashMap<>();
//            objectMap.put("savingsTypeName", name[count]);
//            objectMap.put("savingsTypeDescription", desc[count]);
//            objectMap.put("savingsTypeAbbreviation", abbr[count]);
//
//            final Bitmap main = BitmapUtil.getBitmapFromDrawable(i, this);
//            savingsPlanTypeQueries.uploadSavingsPlanTypeImage(main)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            objectMap.put("savingsTypeImageUri", taskSnapshot.getDownloadUrl().toString());
//
//                            savingsPlanTypeQueries.uploadSavingsPlanTypeImageThumb(main)
//                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                        @Override
//                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                            objectMap.put("savingsTypeImageThumbUri", taskSnapshot.getDownloadUrl().toString());
//                                            objectMap.put("lastUpdatedAt", new Date());
//                                            objectMap.put("timestamp", new Date());
//
//                                            savingsPlanTypeQueries.saveSavingsPlanType(objectMap)
//                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                                        @Override
//                                                        public void onSuccess(DocumentReference documentReference) {
//                                                            Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
//                                                        }
//                                                    });
//                                        }
//                                    });
//                        }
//                    });
//
//            count++;
//        }
//
//    }

    /**********Item selected on Navigation Drawer Actions*******/
    private boolean navActions(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_collections:
                startAnotherActivity(CollectionActivity.class);
                return true;
            case R.id.nav_dashboard:
                startAnotherActivity(Dashboard.class);
                return true;

            case R.id.nav_payment:
                startAnotherActivity(RepaymentActivity.class);
                return true;

            case R.id.nav_loans:
                startAnotherActivity(LoanActivity.class);
                return true;

            case R.id.nav_savings:
                startAnotherActivity(SavingsActivity.class);
                return true;

            case R.id.nav_borrower:
                startAnotherActivity(BorrowerActivity.class);
                return true;



            case R.id.nav_branches:
                startAnotherActivity(BranchesActivity.class);
                return true;

            case R.id.nav_settings:
                startAnotherActivity(SettingsActivity.class);
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

    private void startAnotherActivity(Class newActivity){
        Intent newActivityIntent = new Intent(this, newActivity);
        startActivity(newActivityIntent);
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

        }else if(fragment != null && fragment.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED) {

            fragment.hidePanel();

        }else{
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }

    }

}
