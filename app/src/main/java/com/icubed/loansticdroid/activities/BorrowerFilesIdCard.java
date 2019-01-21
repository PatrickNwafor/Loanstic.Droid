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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.icubed.loansticdroid.R;

import java.io.ByteArrayOutputStream;

import static com.icubed.loansticdroid.fragments.AddNewBorrowerFragment.BorrowerFilesFragment.ID_CARD_FILE_BACK;
import static com.icubed.loansticdroid.fragments.AddNewBorrowerFragment.BorrowerFilesFragment.ID_CARD_FILE_FRONT;
import static com.icubed.loansticdroid.fragments.AddNewBorrowerFragment.BorrowerFilesFragment.REQUEST_CODE_FILES;

public class BorrowerFilesIdCard extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView imageView, editImageBtn;
    private RadioButton frontRadioBtn, backRadioBtn;
    private final int CAMERA_REQUEST_CODE = 2494;
    private Button takePhotoBtn;
    private String bitmapFront, bitmapBack;
    private Bitmap front, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrower_files_id_card);
        toolbar = findViewById(R.id.ID_document_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Identity card");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imageView = findViewById(R.id.image);
        editImageBtn = findViewById(R.id.start_camera_button);
        frontRadioBtn = findViewById(R.id.radio_front);
        backRadioBtn = findViewById(R.id.radio_back);
        takePhotoBtn = findViewById(R.id.takeaphoto);

        frontRadioBtn.setChecked(true);
        front = getIntent().getParcelableExtra("idFront");
        back = getIntent().getParcelableExtra("idBack");

        if(front != null && back != null){
            imageView.setImageBitmap(front);
            takePhotoBtn.setText("Done");
            bitmapFront = BitMapToString(front);
            bitmapBack = BitMapToString(back);
        }

        frontRadioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(front != null)
                        imageView.setImageBitmap(front);
                }
            }
        });

        backRadioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(front != null)
                        imageView.setImageBitmap(back);
                }
            }
        });

        editImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent(CAMERA_REQUEST_CODE);
            }
        });

        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bitmapFront == null || bitmapBack == null) {
                    dispatchTakePictureIntent(CAMERA_REQUEST_CODE);
                }else{
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(ID_CARD_FILE_FRONT, bitmapFront);
                    resultIntent.putExtra(ID_CARD_FILE_BACK, bitmapBack);
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
                    if(frontRadioBtn.isChecked()) {
                        bitmapFront = BitMapToString(imageBitmap);
                        front = imageBitmap;
                    }else{
                        bitmapBack = BitMapToString(imageBitmap);
                        back = imageBitmap;
                    }

                    if(bitmapFront != null && bitmapBack != null){
                        takePhotoBtn.setText("Done");
                    }

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
