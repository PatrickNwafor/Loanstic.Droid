package com.icubed.loansticdroid.fragments.AddNewBorrowerFragment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.AddSingleBorrower;
import com.icubed.loansticdroid.activities.LetsVerifyBusiness;
import com.icubed.loansticdroid.cloudqueries.Account;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.fragments.HomeFragments.MapFragment;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.util.LocationProviderUtil;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.support.constraint.Constraints.TAG;
import static com.icubed.loansticdroid.util.LocationProviderUtil.LOCATION_PERMISSION_REQUEST_CODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class BorrowerFilesFragment extends Fragment {

    private static final int REQUEST_CODE_FILES = 8282;
    private static final int CAMERA_REQUEST_CODE = 335;
    Context context;
    private Button submitButton;
    private BorrowersQueries borrowersQueries;
    private String borrowerImageUri;
    private String borrowerImageThumbUri;
    private LocationProviderUtil locationProviderUtil;
    private LatLng borrowerLatLng;
    private Account account;
    private Index index;
    private ProgressBar reg_progress_bar;
    Bundle bundle;

    public BorrowerFilesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = container.getContext();
        return inflater.inflate(R.layout.fragment_borrower_files, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Algolia search initiation
        Client client = new Client("HGQ25JRZ8Y", "d4453ddf82775ee2324c47244b30a7c7");
        index = client.getIndex("Borrowers");

        bundle = getArguments();
        Log.d(TAG, "onViewCreated: "+bundle.toString());

        borrowersQueries = new BorrowersQueries(context);
        locationProviderUtil = new LocationProviderUtil(context);
        account = new Account();

        reg_progress_bar = view.findViewById(R.id.reg_progress_bar);
        submitButton = view.findViewById(R.id.submit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSubmission();
            }
        });
    }

    private void startSubmission(){
        reg_progress_bar.setVisibility(View.VISIBLE);
        submitButton.setEnabled(false);
        Bitmap bitmap = StringToBitMap(bundle.getString("borrowerImage"));
        uploadBorrowerPicture(bitmap);
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    /********************Upload borrower profile image****************/
    private void uploadBorrowerPicture(final Bitmap bitmap){
        borrowersQueries.uploadBorrowerImage(bitmap).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){

                    borrowerImageUri = task.getResult().getDownloadUrl().toString();

                    borrowersQueries.uploadBorrowerImageThumb(bitmap)
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()){
                                        borrowerImageThumbUri = task.getResult().getDownloadUrl().toString();

                                        //getCurrentLocation
                                        getCurrentLocation();
                                    }else{
                                        reg_progress_bar.setVisibility(View.GONE);
                                        submitButton.setEnabled(true);
                                        Toast.makeText(context, "Failed 2", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    submitButton.setEnabled(true);
                    reg_progress_bar.setVisibility(View.GONE);
                    Toast.makeText(context, "failed 1", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /********************get current location******************/
    private void getCurrentLocation() {
        locationProviderUtil.requestSingleUpdate(new LocationProviderUtil.LocationCallback() {
            @Override
            public void onNewLocationAvailable(LocationProviderUtil.GPSCoordinates location) {
                Location currentLocal = location.getLocation;
                borrowerLatLng = new LatLng(currentLocal.getLatitude(), currentLocal.getLongitude());
                addNewBorrower();
            }
        });
    }

    /************Adds new borrower***************/
    public void addNewBorrower(){

        Log.d(TAG, "addNewBorrower: called");

        //Assigning borrower parameter
        Map<String, Object> borrowerMap = new HashMap<>();
        borrowerMap.put("borrowerLocationLongitude", borrowerLatLng.longitude);
        borrowerMap.put("borrowerLocationLatitude", borrowerLatLng.latitude);
        borrowerMap.put("profileImageUri", borrowerImageUri);
        borrowerMap.put("profileImageThumbUri", borrowerImageThumbUri);
        borrowerMap.put("firstName", bundle.getString("firstName"));
        borrowerMap.put("lastName", bundle.getString("lastName"));
        borrowerMap.put("middleName", bundle.getString("middleName"));
        borrowerMap.put("workAddress", bundle.getString("businessAddress"));
        borrowerMap.put("homeAddress", bundle.getString("homeAddress"));
        borrowerMap.put("city", bundle.getString("city"));
        borrowerMap.put("businessDescription", bundle.getString("businessDesc"));
        borrowerMap.put("businessName", bundle.getString("businessName"));
        borrowerMap.put("phoneNumber", Long.parseLong(bundle.getString("phoneNumber")));
        borrowerMap.put("email", bundle.getString("email"));
        borrowerMap.put("zipcode", Long.parseLong(bundle.getString("zipcode")));
        borrowerMap.put("dateOfBirth", bundle.getString("DOB"));
        borrowerMap.put("loanOfficerId", account.getCurrentUserId());
        borrowerMap.put("state", bundle.getString("state"));
        borrowerMap.put("sex", bundle.getString("sex"));
        borrowerMap.put("belongsToGroup",  false);
        borrowerMap.put("nationality", bundle.getString("nationality"));
        borrowerMap.put("timestamp", new Date());

        //Adding new borrower to database
        borrowersQueries.addNewBorrower(borrowerMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful() && task.isComplete()) {
                            //Adding borrower to search index
                            createBorrowerSearch(task.getResult().getId());
                        }else{
                            reg_progress_bar.setVisibility(View.GONE);
                            submitButton.setEnabled(true);
                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createBorrowerSearch(final String borrowerId) {
        Map<String, Object> searchMap = new HashMap<>();
        searchMap.put("lastName", bundle.getString("lastName"));
        searchMap.put("firstName", bundle.getString("firstName"));
        searchMap.put("businessName", bundle.getString("businessName"));
        searchMap.put("profileImageUri", borrowerImageUri);
        searchMap.put("profileImageThumbUri", borrowerImageThumbUri);

        JSONObject object = new JSONObject(searchMap);
        index.addObjectAsync(object, borrowerId, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject jsonObject, AlgoliaException e) {
                if(e == null) {
                    reg_progress_bar.setVisibility(View.GONE);
                    submitButton.setEnabled(true);
                    Toast.makeText(context, "New borrower Added Successfully", Toast.LENGTH_SHORT).show();

                    //moving to business verification page
                    ((AddSingleBorrower) context).goToBusinessVerification(borrowerId);
                }else{
                    reg_progress_bar.setVisibility(View.GONE);
                    Toast.makeText(context, "Failed to register borrower for search", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "requestCompleted: "+e.getMessage());
                }
            }
        });
    }

    /************Accepting Permission*************/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult: called");

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onRequestPermissionResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionResult: permission granted");
                    //initialize our map
                }
            }
        }
    }

    /**
     * The las four method is related to the borrower files to get the image and save
     * it isnt used for now as the borrowers file page is still worked on
     */
    /******************Uploads borrower image file*******************/
    private void uploadImageFile(final Bitmap bitmap) {
        borrowersQueries.uploadImageFiles(bitmap).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    borrowersQueries.uploadThumbImageFiles(bitmap)
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(context, "Failed 2", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(context, "Failed 1", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FILES && resultCode == RESULT_OK) {

            //Borrower profile image result
            Bundle extras = data.getExtras();
            //Bitmap returned from camera
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //uploadImageFile(imageBitmap);

        }
    }

    /***************Calls up Up Phone camera********************/
    private void dispatchTakePictureIntent(int CAMERA_CODE) {
        getCameraPermission();
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

    private void getCameraPermission(){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            Log.d(TAG, "getCameraPermission: permission not granted");
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }else{
            dispatchTakePictureIntent(REQUEST_CODE_FILES);
            Log.d(TAG, "getCameraPermission: permission already granted");
        }
    }
}
