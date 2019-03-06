package com.icubed.loansticdroid.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.UploadTask;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.BusinessVerificationRecyclerViewAdapter;
import com.icubed.loansticdroid.cloudqueries.BorrowerPhotoValidationQueries;
import com.icubed.loansticdroid.util.BitmapUtil;
import com.icubed.loansticdroid.util.LocationProviderUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.PendingIntent.getActivity;

public class BusinessVerification extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "BusinessVerification";

    //vars
    private MenuItem menuItem;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private BusinessVerificationRecyclerViewAdapter adapter;
    private ArrayList<Bitmap> mImageBitmap;
    private ImageView  selectedImageView;
    private LottieAnimationView takePictureImageView;
    private static final int CAMERA_REQUEST_CODE = 335;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
   // public MarkerOptions markerOptions;

    private LocationProviderUtil locationProviderUtil;
    private MapView mMapView;
    GoogleMap mGoogleMap;

    private String borrowerId, photoUri, photoThumbUri, groupId, activityCycleId;
    private BorrowerPhotoValidationQueries photoValidationQueries;
    private double photoLatitude;
    private double photoLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_verification);

        takePictureImageView = findViewById(R.id.takepicture);
        selectedImageView = findViewById(R.id.selectedImage);
        startCamera();

        mMapView = findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        locationProviderUtil = new LocationProviderUtil(this);

        mImageBitmap = new ArrayList<>();
        photoValidationQueries = new BorrowerPhotoValidationQueries(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView = findViewById(R.id.busiVerifRecyclerView);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BusinessVerificationRecyclerViewAdapter(mImageBitmap);
        recyclerView.setAdapter(adapter);

        borrowerId = getIntent().getStringExtra("borrowerId");
        activityCycleId = getIntent().getStringExtra("activityCycleId");
        groupId = getIntent().getStringExtra("groupId");
        Log.d(TAG, "onCreate: "+groupId);

        toolbar = findViewById(R.id.business_verification_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Business Verification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void startCamera() {
        takePictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE);
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

    /********************get current location******************/
    private void getCurrentLocation() {
        locationProviderUtil.requestContinousUpdate(new LocationProviderUtil.LocationCallback() {
            @Override
            public void onNewLocationAvailable(LocationProviderUtil.GPSCoordinates location) {
                Location currentLocal = location.getLocation;
                photoLatitude = currentLocal.getLatitude();
                photoLongitude = currentLocal.getLongitude();
                drawMarker(currentLocal);
                Log.d(TAG, "onNewLocationAvailable: "+currentLocal.toString());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            //Borrower photo image validation result
            Bundle extras = data.getExtras();
            //Bitmap returned from camera
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //Set big image view
            showBigImage(imageBitmap);
            //Adds image to the horizontal scroll
            addImageToList(imageBitmap);
            //Uploads image to cloud
            uploadImageFile(imageBitmap);

        }
    }

    public void showBigImage(Bitmap bitmap){
        selectedImageView.setImageBitmap(bitmap);
    }

    private void addImageToList(Bitmap bitmap){
        mImageBitmap.add(0,bitmap);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        getCurrentLocation();
    }

    /****************************set marker on map******************************/
    private void drawMarker(Location location) {
        if (mGoogleMap != null) {
            mGoogleMap.clear();
            LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());

            mGoogleMap.addMarker(new MarkerOptions()
                    .position(gps)
                    .title("Current Position"));

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gps, 15));
        }

    }

    /*****
     * uploads verification photo to firebase storage
     * @param bitmap
     */
    private void uploadImageFile(final Bitmap bitmap) {
        photoValidationQueries.uploadImage(bitmap).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    photoUri = task.getResult().getDownloadUrl().toString();
                    photoValidationQueries.uploadThumbImage(bitmap)
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()){
                                        photoThumbUri = task.getResult().getDownloadUrl().toString();
                                        addPhotoToCloud();
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Failed to upload thumbnail", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Adds photo verification data to cloud
     */
    private void addPhotoToCloud() {
        Map<String, Object> photoMap = new HashMap<>();
        if(borrowerId != null) {
            photoMap.put("borrowerId", borrowerId);
            photoMap.put("activityCycleId", activityCycleId);
        }else{
            photoMap.put("groupId", groupId);
        }
        photoMap.put("photoUri", photoUri);
        photoMap.put("photoThumbUri", photoThumbUri);
        photoMap.put("photoLatitude", photoLatitude);
        photoMap.put("photoLongitude", photoLongitude);
        photoMap.put("timestamp", new Date());

        photoValidationQueries.addNewValidationPhoto(photoMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(BusinessVerification.this, "Done", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(BusinessVerification.this, "Failed to register photo details", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.loanee_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItem = menu.findItem(R.id.next_to_loan_terms);
        menuItem.setTitle("Finish");
        menuItem.setVisible(true);
        menu.findItem(R.id.search_loan).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.next_to_loan_terms:
                Intent intent = new Intent(this, BorrowerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //finish();


            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        getCurrentLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void done(View view) {
        Intent intent = new Intent(this, BorrowerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        //finish();
    }
}
