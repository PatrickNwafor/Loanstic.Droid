package com.icubed.loansticdroid.activities;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.localdatabase.BorrowerFilesTable;
import com.icubed.loansticdroid.localdatabase.BorrowerPhotoValidationTable;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.GroupPhotoValidationTable;

public class PictureViewActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView imageView;
    private BorrowerFilesTable borrowerFilesTable;
    private GroupPhotoValidationTable groupPhotoValidationTable;
    private BorrowerPhotoValidationTable borrowerPhotoValidationTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_view);

        borrowerFilesTable = getIntent().getParcelableExtra("file");
        groupPhotoValidationTable = getIntent().getParcelableExtra("group_photo_valid");
        borrowerPhotoValidationTable = getIntent().getParcelableExtra("borrower_photo_valid");

        toolbar = findViewById(R.id.picture_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imageView = findViewById(R.id.image);

        if(borrowerFilesTable != null) {
            getSupportActionBar().setTitle(borrowerFilesTable.getFileDescription());
            loadImage(borrowerFilesTable.getFileImageUri(), borrowerFilesTable.getFileImageUriThumb());
        }else if(groupPhotoValidationTable != null){
            getSupportActionBar().setTitle("Business Verification Photo");
            loadImage(groupPhotoValidationTable.getPhotoUri(), groupPhotoValidationTable.getPhotoThumbUri());
        }else if(borrowerPhotoValidationTable != null){
            getSupportActionBar().setTitle("Business Verification Photo");
            loadImage(borrowerPhotoValidationTable.getPhotoUri(), borrowerPhotoValidationTable.getPhotoThumbUri());
        }
    }

    private void loadImage(String imageUri, String imageThumbUri){
        Glide.with(this).load(imageUri).thumbnail(Glide.with(this).load(imageThumbUri)).into(imageView);
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
