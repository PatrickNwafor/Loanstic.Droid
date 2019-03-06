package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.cloudqueries.PaymentQueries;

public class PaymentActivity extends AppCompatActivity {

    Button proceed;
    Animation frombottom;
    TextView paymentCollection,letsVerify;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private PaymentQueries paymentQueries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        paymentCollection = findViewById(R.id.textView4);
        letsVerify = findViewById(R.id.textView5);
        proceed = findViewById(R.id.proceed);
        frombottom = AnimationUtils.loadAnimation( this,R.anim.frombottom);




        proceed.setAnimation(frombottom);

        paymentQueries = new PaymentQueries();
    }

    /***************Calls up Up Phone camera********************/
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            //Bitmap returned from camera
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //imageView.setImageBitmap(imageBitmap);
        }
    }

    public void takeBorrowerPicture(View view) {
        dispatchTakePictureIntent();
    }
}
