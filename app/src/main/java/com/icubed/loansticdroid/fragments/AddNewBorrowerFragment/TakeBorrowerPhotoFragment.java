package com.icubed.loansticdroid.fragments.AddNewBorrowerFragment;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.AddSingleBorrower;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class TakeBorrowerPhotoFragment extends Fragment {

    private CircleImageView borrowerImageView;
    private ImageView editImageView;
    private Button nextBtn, previousBtn;
    Context context;
    Bundle bundle;

    private Bitmap borrowerImage = null;
    private static final int CAMERA_REQUEST_CODE = 335;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    public TakeBorrowerPhotoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = container.getContext();
        return inflater.inflate(R.layout.fragment_take_borrower_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editImageView = view.findViewById(R.id.start_camera_button);
        borrowerImageView = view.findViewById(R.id.borrower_image);
        nextBtn = view.findViewById(R.id.next1);
        previousBtn = view.findViewById(R.id.previous);

        bundle = getArguments();

        editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous();
            }
        });
    }

    private void previous() {
        ((AddSingleBorrower) context).startFragment(((AddSingleBorrower) context).sexDobFragment, "business");
    }

    private void next() {

        Bundle bundle1 = bundle;
        bundle1.putString("borrowerImage", BitMapToString(borrowerImage));
        if(borrowerImage != null){
            ((AddSingleBorrower) context).startFragment(((AddSingleBorrower) context).borrowerFilesFragment, "borrower_files", bundle1);
        }else {
            dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE);
        }
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
        getCameraPermission();
        nextBtn.setText("Next");
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, CAMERA_CODE);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            //Borrower profile image result
            Bundle extras = data.getExtras();
            //Bitmap returned from camera
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            borrowerImageView.setImageBitmap(imageBitmap);
            borrowerImage = imageBitmap;

        }
    }

    private void getCameraPermission(){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            Log.d(TAG, "getCameraPermission: permission not granted");
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }else{
            dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE);
            Log.d(TAG, "getCameraPermission: permission already granted");
        }
    }

    /************Accepting Permission*************/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult: called");

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length > 0){
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "onRequestPermissionResult: permission failed");
                        return;
                    }
                    dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE);
                    Log.d(TAG, "onRequestPermissionResult: permission granted");
                    //initialize our map
                }
            }
        }
    }
}
