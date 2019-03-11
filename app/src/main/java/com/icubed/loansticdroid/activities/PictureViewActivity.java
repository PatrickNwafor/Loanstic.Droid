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
import com.icubed.loansticdroid.util.BitmapUtil;

public class PictureViewActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView imageView;
    private BorrowerFilesTable borrowerFilesTable;
    private GroupPhotoValidationTable groupPhotoValidationTable;
    private BorrowerPhotoValidationTable borrowerPhotoValidationTable;
    private byte[] fileByte, borrowerValidByte, groupValidByte = null;
    private Bitmap paymentBitMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_view);

        borrowerFilesTable = getIntent().getParcelableExtra("file");
        fileByte = getIntent().getByteArrayExtra("file_byte");
        groupPhotoValidationTable = getIntent().getParcelableExtra("group_photo_valid");
        groupValidByte = getIntent().getByteArrayExtra("group_photo_valid_byte");
        borrowerPhotoValidationTable = getIntent().getParcelableExtra("borrower_photo_valid");
        borrowerValidByte = getIntent().getByteArrayExtra("borrower_photo_valid_byte");
        paymentBitMap = getIntent().getParcelableExtra("payment");

        toolbar = findViewById(R.id.picture_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imageView = findViewById(R.id.image);

        if(borrowerFilesTable != null) {
            getSupportActionBar().setTitle(borrowerFilesTable.getFileDescription());
            borrowerFilesTable.setImageByteArray(fileByte);
            if(borrowerFilesTable.getImageByteArray() == null) loadImage(borrowerFilesTable.getFileImageUri(), borrowerFilesTable.getFileImageUriThumb());
            else loadImageFromByte(borrowerFilesTable.getImageByteArray());
        }else if(groupPhotoValidationTable != null){
            getSupportActionBar().setTitle("Business Verification Photo");
            groupPhotoValidationTable.setImageByteArray(groupValidByte);
            if(groupPhotoValidationTable.getImageByteArray() == null) loadImage(groupPhotoValidationTable.getPhotoUri(), groupPhotoValidationTable.getPhotoThumbUri());
            else loadImageFromByte(groupPhotoValidationTable.getImageByteArray());
        }else if(borrowerPhotoValidationTable != null){
            getSupportActionBar().setTitle("Business Verification Photo");
            borrowerPhotoValidationTable.setImageByteArray(borrowerValidByte);
            if(borrowerPhotoValidationTable.getImageByteArray() == null) loadImage(borrowerPhotoValidationTable.getPhotoUri(), borrowerPhotoValidationTable.getPhotoThumbUri());
            else loadImageFromByte(borrowerPhotoValidationTable.getImageByteArray());
        }else if(paymentBitMap != null){
            getSupportActionBar().setTitle("Payment Receipt Photo");
            imageView.setImageBitmap(paymentBitMap);
        }
    }

    private void loadImage(String imageUri, String imageThumbUri){
        BitmapUtil.getImageAndThumbnailWithGlide(this, imageUri, imageThumbUri).into(imageView);
    }

    private void loadImageFromByte(byte[] bytes){
        imageView.setImageBitmap(BitmapUtil.getBitMapFromBytes(bytes));
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
