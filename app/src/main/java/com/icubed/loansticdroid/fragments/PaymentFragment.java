package com.icubed.loansticdroid.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

//import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.models.PaymentQueries;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentFragment extends Fragment {

    private PaymentQueries paymentQueries;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    public PaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        paymentQueries = new PaymentQueries();

        return view;
    }

    /***************Calls up Up Phone camera********************/
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
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
                                        Toast.makeText(getContext(), "Failed 2", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(getContext(), "failed 1", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(getContext(), "Failed 3", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
