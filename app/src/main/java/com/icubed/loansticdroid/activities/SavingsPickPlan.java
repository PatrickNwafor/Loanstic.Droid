package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.LifeGoals.LifeGoalsSetup1GoalName;
import com.icubed.loansticdroid.adapters.SavingsPlanTypeRecyclerAdapter;
import com.icubed.loansticdroid.adapters.SavingsRecyclerAdapter;
import com.icubed.loansticdroid.cloudqueries.SavingsPlanTypeQueries;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.localdatabase.SavingsPlanTypeTable;
import com.icubed.loansticdroid.localdatabase.SavingsPlanTypeTableQueries;
import com.icubed.loansticdroid.localdatabase.SavingsTable;
import com.icubed.loansticdroid.util.BitmapUtil;

import java.util.Date;
import java.util.List;

public class SavingsPickPlan extends AppCompatActivity {

    private static final String TAG = ".SavingsPickPlan";
    private Toolbar toolbar;
    public String savingsPlanName = null;

    public static final String LIFE_GOALS = "Life goals";
    public static final String PERIODIC_PLAN = "Periodic plan";
    public static final String FIXED_INVESTMENT = "Fixed investment";
    public static final String SAVE_AS_YOU_EARN = "Save as you earn";

    LottieAnimationView view1, view2, view3, view4;
    CardView cd1, cd2, cd3, cd4;
    LottieAnimationView lastCheck = null;
    BorrowersTable borrower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_plan);

        toolbar = findViewById(R.id.pick_plan_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select Savings Plan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        borrower = getIntent().getParcelableExtra("borrower");

        cd1 = findViewById(R.id.cd1);
        cd2 = findViewById(R.id.cd2);
        cd3 = findViewById(R.id.cd3);
        cd4 = findViewById(R.id.cd4);

        view1 = findViewById(R.id.check_loan_type);
        view2 = findViewById(R.id.check_loan_type1);
        view3 = findViewById(R.id.check_loan_type2);
        view4 = findViewById(R.id.check_loan_type3);

        cd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPlan(view1);
            }
        });

        cd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPlan(view2);
            }
        });

        cd3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPlan(view3);
            }
        });

        cd4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPlan(view4);
            }
        });


    }

    private void selectPlan(LottieAnimationView view) {
        if(view.getVisibility() == View.GONE){

            if(lastCheck != null){
                lastCheck.setVisibility(View.GONE);
            }

            view.setVisibility(View.VISIBLE);
            view.playAnimation();
            lastCheck = view;
            if(view == view1) savingsPlanName = LIFE_GOALS;
            else if(view == view2) savingsPlanName = PERIODIC_PLAN;
            else if(view == view3) savingsPlanName = FIXED_INVESTMENT;
            else if(view == view4) savingsPlanName = SAVE_AS_YOU_EARN;
            invalidateOptionsMenu();
        }else{
            lastCheck = null;
            savingsPlanName = null;
            view.setVisibility(View.GONE);
            invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.select_loan_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.next_to_loan_terms:
                if(savingsPlanName.equals(LIFE_GOALS)) startAnotherActivity(SavingsPlanLifeGoals.class);
                else startAnotherActivity(LifeGoalsSetup1GoalName.class);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem register = menu.findItem(R.id.next_to_loan_terms);
        if(savingsPlanName != null) register.setVisible(true);
        else register.setVisible(false);
        return true;
    }

    private void startAnotherActivity(Class newActivity){
        Intent newActivityIntent = new Intent(this, newActivity);
        newActivityIntent.putExtra("savings_plan_name", savingsPlanName);
        newActivityIntent.putExtra("borrower", borrower);
        //newActivityIntent.putExtra("savings", savingsTable);
        startActivity(newActivityIntent);
    }
}
