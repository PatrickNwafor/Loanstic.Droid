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
import com.icubed.loansticdroid.localdatabase.BorrowersTable;

public class PictureViewActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView imageView;
    private BorrowerFilesTable borrowerFilesTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_view);

        borrowerFilesTable = getIntent().getParcelableExtra("file");

        toolbar = findViewById(R.id.picture_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(borrowerFilesTable.getFileDescription());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imageView = findViewById(R.id.image);

        Glide.with(this)
                .load(borrowerFilesTable.getFileImageUri())
                .thumbnail(
                Glide.with(this)
                        .load(borrowerFilesTable.getFileImageUriThumb())
        ).into(imageView);
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
