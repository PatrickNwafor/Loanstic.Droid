package com.icubed.loansticdroid.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.icubed.loansticdroid.R;


public class BorrowerPersonalInfo extends AppCompatActivity {

    LinearLayout layout1,layout2,layout3,layout4,layout5,layout6,layout7;
    Button next,next1,previous,takePhoto;
    ImageView retakePhoto;
    private Button submitBorrowerBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrower_personal_info);

        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);
        layout4 = findViewById(R.id.layout4);
        layout5 = findViewById(R.id.layout5);
        layout6 = findViewById(R.id.layout6);
        layout7 = findViewById(R.id.layout7);

        next = findViewById(R.id.next);
        next1 = findViewById(R.id.next1);
        previous = findViewById(R.id.previous);
        takePhoto = findViewById(R.id.verify);
        retakePhoto = findViewById(R.id.start_camera_button);
        submitBorrowerBtn = findViewById(R.id.submit);
    }

    public void next_layout(View view) {
        if(layout1.getVisibility() == View.VISIBLE){
            layout1.setVisibility(View.INVISIBLE);
            layout2.setVisibility(View.VISIBLE);
            layout3.setVisibility(View.INVISIBLE);
            layout4.setVisibility(View.INVISIBLE);
            layout5.setVisibility(View.INVISIBLE);
            previous.setVisibility(View.VISIBLE);
            next1.setVisibility(View.VISIBLE);
            next.setVisibility(View.INVISIBLE);

        }
        else if (layout2.getVisibility()== View.VISIBLE)
        {   layout1.setVisibility(View.INVISIBLE);
            layout2.setVisibility(View.INVISIBLE);
            layout3.setVisibility(View.VISIBLE);
            layout4.setVisibility(View.INVISIBLE);
            layout5.setVisibility(View.INVISIBLE);

        }
        else if (layout3.getVisibility()== View.VISIBLE)
        {
            layout1.setVisibility(View.INVISIBLE);
            layout2.setVisibility(View.INVISIBLE);
            layout3.setVisibility(View.INVISIBLE);
            layout4.setVisibility(View.VISIBLE);
            layout5.setVisibility(View.INVISIBLE);
        }
        else if (layout4.getVisibility()== View.VISIBLE)
        {
            layout1.setVisibility(View.INVISIBLE);
            layout2.setVisibility(View.INVISIBLE);
            layout3.setVisibility(View.INVISIBLE);
            layout4.setVisibility(View.INVISIBLE);
            layout5.setVisibility(View.VISIBLE);
        }
        else if (layout5.getVisibility()== View.VISIBLE)
        {
            layout1.setVisibility(View.INVISIBLE);
            layout2.setVisibility(View.INVISIBLE);
            layout3.setVisibility(View.INVISIBLE);
            layout4.setVisibility(View.INVISIBLE);
            layout5.setVisibility(View.INVISIBLE);
            layout6.setVisibility(View.VISIBLE);
            takePhoto.setVisibility(View.VISIBLE);
            next1.setVisibility(View.INVISIBLE);
        }
        else if (layout6.getVisibility()== View.VISIBLE)
        {
            layout1.setVisibility(View.INVISIBLE);
            layout2.setVisibility(View.INVISIBLE);
            layout3.setVisibility(View.INVISIBLE);
            layout4.setVisibility(View.INVISIBLE);
            layout5.setVisibility(View.INVISIBLE);
            layout6.setVisibility(View.INVISIBLE);
            layout7.setVisibility(View.VISIBLE);
            takePhoto.setVisibility(View.INVISIBLE);
            next1.setVisibility(View.INVISIBLE);
            submitBorrowerBtn.setVisibility(View.VISIBLE);
            previous.setVisibility(View.INVISIBLE);
        }


    }

    public void previous_layout(View view) {
        if(layout6.getVisibility()==View.VISIBLE)
        {
            layout1.setVisibility(View.INVISIBLE);
            layout2.setVisibility(View.INVISIBLE);
            layout3.setVisibility(View.INVISIBLE);
            layout4.setVisibility(View.INVISIBLE);
            layout5.setVisibility(View.VISIBLE);
            layout6.setVisibility(View.INVISIBLE);
            next1.setVisibility(View.VISIBLE);
            takePhoto.setVisibility(View.INVISIBLE);
            retakePhoto.setVisibility(View.INVISIBLE);
        }
        else if(layout5.getVisibility()==View.VISIBLE)
        {
            layout1.setVisibility(View.INVISIBLE);
            layout2.setVisibility(View.INVISIBLE);
            layout3.setVisibility(View.INVISIBLE);
            layout4.setVisibility(View.VISIBLE);
            layout5.setVisibility(View.INVISIBLE);
            layout6.setVisibility(View.INVISIBLE);
        }
        else if (layout4.getVisibility()==View.VISIBLE)
        {
            layout1.setVisibility(View.INVISIBLE);
            layout2.setVisibility(View.INVISIBLE);
            layout3.setVisibility(View.VISIBLE);
            layout4.setVisibility(View.INVISIBLE);
            layout5.setVisibility(View.INVISIBLE);
            layout6.setVisibility(View.INVISIBLE);
        }
        else if (layout3.getVisibility()==View.VISIBLE)
        {
            layout1.setVisibility(View.INVISIBLE);
            layout2.setVisibility(View.VISIBLE);
            layout3.setVisibility(View.INVISIBLE);
            layout4.setVisibility(View.INVISIBLE);
            layout5.setVisibility(View.INVISIBLE);
            layout6.setVisibility(View.INVISIBLE);
        }
        else if (layout2.getVisibility()==View.VISIBLE)
        {
            layout1.setVisibility(View.VISIBLE);
            layout2.setVisibility(View.INVISIBLE);
            layout3.setVisibility(View.INVISIBLE);
            layout4.setVisibility(View.INVISIBLE);
            layout5.setVisibility(View.INVISIBLE);
            layout6.setVisibility(View.INVISIBLE);
            previous.setVisibility(View.INVISIBLE);
            next.setVisibility(View.VISIBLE);
            next1.setVisibility(View.INVISIBLE);
        }
    }

    public void start_camera(View view) {
        next1.setVisibility(View.VISIBLE);
        takePhoto.setVisibility(View.INVISIBLE);
        retakePhoto.setVisibility(View.VISIBLE);
    }
}
