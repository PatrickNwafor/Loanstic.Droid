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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.util.FormUtil;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.icubed.loansticdroid.fragments.AddNewBorrowerFragment.BorrowerFilesFragment.OTHER_DOC;
import static com.icubed.loansticdroid.fragments.AddNewBorrowerFragment.BorrowerFilesFragment.OTHER_DOC_DESC;
import static com.icubed.loansticdroid.fragments.AddNewBorrowerFragment.BorrowerFilesFragment.PASSPORT;

public class BorrowerFileOtherDocuments extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView imageView;
    private final int CAMERA_REQUEST_CODE = 2494;
    private Button takePhotoBtn, doneBtn;
    private TextView fileTextViewDesc;
    private EditText fileDesc;
    private ArrayList<String> otherFileDesc;
    private ArrayList<Bitmap> otherFile;
    private FormUtil formUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrower_file_other_documents);
        toolbar = findViewById(R.id.ID_document_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Other documents");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imageView = findViewById(R.id.image);
        takePhotoBtn = findViewById(R.id.takeaphoto);
        fileDesc = findViewById(R.id.fileDesc);
        doneBtn = findViewById(R.id.done);
        fileTextViewDesc = findViewById(R.id.document_desc);

        otherFile = getIntent().getParcelableArrayListExtra("files");
        otherFileDesc = getIntent().getStringArrayListExtra("files_desc");

        if(otherFileDesc == null && otherFile == null) {
            otherFile = new ArrayList<>();
            otherFileDesc = new ArrayList<>();
        }

        formUtil = new FormUtil();

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(otherFile.isEmpty()){
                    finish();
                }else {
                    Intent resultIntent = new Intent();
                    resultIntent.putParcelableArrayListExtra(OTHER_DOC, otherFile);
                    resultIntent.putStringArrayListExtra(OTHER_DOC_DESC, otherFileDesc);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
        });

        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!formUtil.isSingleFormEmpty(fileDesc)) {
                    fileDesc.setError(null);

                    //to check that a new file description is entered
                    if(otherFileDesc != null){
                        if(otherFileDesc.contains(fileDesc.getText().toString())){
                            fileDesc.setError("This file description has already been used");
                            return;
                        }
                    }
                    dispatchTakePictureIntent(CAMERA_REQUEST_CODE);
                }else{
                    fileDesc.setError("This field is required before taking picture");
                }
            }
        });

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
                    otherFile.add(imageBitmap);
                    otherFileDesc.add(fileDesc.getText().toString());
                    fileTextViewDesc.setText(fileDesc.getText().toString());
                    imageView.setImageBitmap(imageBitmap);
                    fileDesc.setText("");
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
