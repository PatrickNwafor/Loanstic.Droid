package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.models.PaymentQueries;

public class PaymentActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private PaymentQueries paymentQueries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

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

    private void uploadPaymentPicture(final Bitmap bitmap, final String paymentId){
        paymentQueries.uploadImage(bitmap, paymentId).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){

                    final String paymentImageUri = task.getResult().getDownloadUrl().toString();

                    paymentQueries.uploadImageThumb(bitmap, paymentId)
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()){
                                        String paymentImageThumbUri = task.getResult().getDownloadUrl().toString();

                                        saveImageUri(paymentId, paymentImageUri, paymentImageThumbUri);
                                    }else{
                                        Toast.makeText(PaymentActivity.this, "Failed 2", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(PaymentActivity.this, "failed 1", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveImageUri(String paymentId, final String paymentImageUri, String paymentImageThumbUri){
        paymentQueries.storeUriToFirestore(paymentId, paymentImageUri, paymentImageThumbUri)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(PaymentActivity.this, "Done", Toast.LENGTH_SHORT).show();

                        }else{
                            Toast.makeText(PaymentActivity.this, "Failed 3", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
