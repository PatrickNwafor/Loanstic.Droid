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

public class SelectAddType extends AppCompatActivity {
    Button addSingleBorrower, addGroup;
    TextView selectNote;
    Animation frombottom;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_new_user_type);
        addSingleBorrower = findViewById(R.id.single_borrower);
        selectNote = findViewById(R.id.selectNote);
        addGroup = findViewById(R.id.submit_group);
        frombottom = AnimationUtils.loadAnimation( this,R.anim.frombottom);

        addSingleBorrower.setAnimation(frombottom);
        addGroup.setAnimation(frombottom);
        selectNote.setAnimation(frombottom);
    }

    public void add_single_borrower(View view) {
        Intent addBorrowerIntent = new Intent(SelectAddType.this, AddSingleBorrower.class);
        startActivity(addBorrowerIntent);
    }


    public void add_group(View view) {
        Intent addGroupBorrowerIntent = new Intent(SelectAddType.this, AddGroupBorrower.class);
        startActivity(addGroupBorrowerIntent);
    }
}
