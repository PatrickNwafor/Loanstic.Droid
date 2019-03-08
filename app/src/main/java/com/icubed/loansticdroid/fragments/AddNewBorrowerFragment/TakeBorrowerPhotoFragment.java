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
import com.icubed.loansticdroid.util.BitmapUtil;

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
    Context context;
    Bundle bundle;

    private Bitmap borrowerImage = null;
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
        bundle = getArguments();

        Bitmap bitmap = StringToBitMap(bundle.getString("borrowerImage"));
        if(bitmap == null) ((AddSingleBorrower) getContext()).next.setText("Take Photo");
        else {
            ((AddSingleBorrower) getContext()).next.setText("Next");
            borrowerImageView.setImageBitmap(bitmap);
        }

        ((AddSingleBorrower) getContext()).title.setText("Borrower Photo");

        editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE);
            }
        });

        ((AddSingleBorrower) getContext()).previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous();
            }
        });

        ((AddSingleBorrower) getContext()).next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
    }

    public Bitmap StringToBitMap(String encodedString){
        return BitmapUtil.stringToBitMap(encodedString);
    }

    private void previous() {
        ((AddSingleBorrower) context).startFragment(((AddSingleBorrower) context).businessFragment, "business");
    }

    private void next() {
        if(borrowerImage != null){
            Bundle bundle1 = bundle;
            bundle1.putString("borrowerImage", BitMapToString(borrowerImage));
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
            ((AddSingleBorrower) getContext()).next.setText("Next");
            borrowerImage = imageBitmap;

        }
    }
}
