package com.icubed.loansticdroid.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.icubed.loansticdroid.R;

import java.io.ByteArrayOutputStream;

import static com.icubed.loansticdroid.fragments.AddNewBorrowerFragment.BorrowerFilesFragment.DRIVER_LICENSE;
import static com.icubed.loansticdroid.fragments.AddNewBorrowerFragment.BorrowerFilesFragment.PASSPORT;

public class BorrowerFilesPassport extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView imageView, editImageView;
    private final int CAMERA_REQUEST_CODE = 2494;
    private Button takePhotoBtn;
    private String passport;
    private Bitmap passportBitMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrower_files_passport);
        toolbar = findViewById(R.id.ID_document_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Passport");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imageView = findViewById(R.id.image);
        takePhotoBtn = findViewById(R.id.takeaphoto);
        editImageView = findViewById(R.id.start_camera_button);

        passportBitMap = getIntent().getParcelableExtra("image");

        if(passportBitMap != null){
            imageView.setImageBitmap(passportBitMap);
            passport = BitMapToString(passportBitMap);
            takePhotoBtn.setText("Done");
        }

        editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent(CAMERA_REQUEST_CODE);
            }
        });

        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passport == null) {
                    dispatchTakePictureIntent(CAMERA_REQUEST_CODE);
                }else{
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(PASSPORT, passport);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
        });

    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    /***************Calls up Up Phone camera********************/
    private void dispatchTakePictureIntent(int CAMERA_CODE) {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, CAMERA_CODE);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (CAMERA_REQUEST_CODE) : {
                if(resultCode == RESULT_OK){
                    //Borrower profile image result
                    Bundle extras = data.getExtras();
                    //Bitmap returned from camera
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    takePhotoBtn.setText("Done");
                    passport = BitMapToString(imageBitmap);
                    imageView.setImageBitmap(imageBitmap);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }
}
