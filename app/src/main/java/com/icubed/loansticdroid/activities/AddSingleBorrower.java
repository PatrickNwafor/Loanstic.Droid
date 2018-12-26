package com.icubed.loansticdroid.activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.UploadTask;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.models.Account;
import com.icubed.loansticdroid.models.BorrowersQueries;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class AddSingleBorrower extends AppCompatActivity {
    private static final String TAG = ".AddSingleBorrower";
    Spinner sexDrp,citizenship;
    private static final String DEFAULT_LOCAL = "Nigeria";

    //for location
    private LocationManager mLocationManager;
    public static final int LOCATION_UPDATE_MIN_DISTANCE = 10;
    public static final int LOCATION_UPDATE_MIN_TIME = 5000;
    private static final int REQUEST_CODE_FILES = 222;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private BorrowersQueries borrowersQueries;
    private Account account;
    private String selectedSex, borrowerImageUri, borrowerImageThumbUri;
    private String selectedCountry;
    private TextView firstNameTextView, middleNameTextView, lastNameTextView
            ,phoneNumberTextView, emailTextView, dateOfBirthTextView
            ,homeAddressTextView, businessAddressTextView, cityTextView
            ,stateTextView, zipCodeTextView, businessNameTextView, businessDescTextView;

    private Button submitBorrowerBtn;
    private Button borrowerFileBtn;
    private ImageView borrowerImageView;

    private Bitmap borrowerImage;
    private LatLng borrowerLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_single_borrower);

        sexDrp =findViewById(R.id.spSex);
        citizenship = findViewById(R.id.input_citizenship);
        firstNameTextView = findViewById(R.id.first_name);
        middleNameTextView = findViewById(R.id.middle_name);
        lastNameTextView = findViewById(R.id.last_name);
        phoneNumberTextView = findViewById(R.id.mobile_number);
        emailTextView = findViewById(R.id.email_address);
        dateOfBirthTextView = findViewById(R.id.date_of_birth);
        homeAddressTextView = findViewById(R.id.home_address);
        businessAddressTextView = findViewById(R.id.business_address);
        cityTextView = findViewById(R.id.city);
        stateTextView = findViewById(R.id.state);
        zipCodeTextView = findViewById(R.id.zip_code);
        businessNameTextView = findViewById(R.id.business_name);
        businessDescTextView = findViewById(R.id.description);
        submitBorrowerBtn = findViewById(R.id.submit);
        borrowerFileBtn = findViewById(R.id.borrower_files);
        borrowerImageView = findViewById(R.id.borrower_image);

        submitBorrowerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitBorrower();
            }
        });
        
        borrowerFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent(REQUEST_CODE_FILES);
            }
        });

        ArrayAdapter<CharSequence> adapterSex;
        String[] sexArr = {"Male", "Female"};
        adapterSex = new ArrayAdapter<CharSequence>(this,android.R.layout.simple_spinner_item,sexArr);
        adapterSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexDrp.setAdapter(adapterSex);
        selectedSex = sexDrp.getSelectedItem().toString();

        borrowersQueries = new BorrowersQueries(this);
        account = new Account();

        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        String country;
        for( Locale loc : locale ){
            country = loc.getDisplayCountry();
            if( country.length() > 0 && !countries.contains(country) ){
                countries.add( country );
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, countries);
        citizenship.setAdapter(adapter);
        citizenship.setSelection(adapter.getPosition(DEFAULT_LOCAL));
        selectedCountry = citizenship.getSelectedItem().toString();

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //get location permission
        getLocationPermission();
        getCurrentLocation();
    }

    private void submitBorrower() {
        uploadBorrowerPicture(borrowerImage);
    }

    /***************Listener to get user location***********************/
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.d(TAG, "getCurrentLocation: Lat: " + location.getLatitude() + " Long: " + location.getLongitude());
                borrowerLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mLocationManager.removeUpdates(mLocationListener);
            } else {
                Log.d(TAG, "onLocationChanged: Location is null");
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    /************Requesting Location Permission**********/
    private void getLocationPermission(){
        Log.d(TAG, "checking for permission");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "Permission already granted");
        }else{
            Log.d(TAG, "No permission yet");
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
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


    /********************get current location******************/
    private void getCurrentLocation() {
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location location = null;
        if (!(isGPSEnabled || isNetworkEnabled)) Toast.makeText(this, "Please enable location service", Toast.LENGTH_SHORT).show();
        else {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            if (isNetworkEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
                location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (isGPSEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
                location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        if (location != null) {
            Log.d(TAG, "getCurrentLocation: Lat: "+location.getLatitude()+" Long: "+location.getLongitude());
            //Do something here
        }
    }


    public void start_camera(View view) {
        dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE);
    }

    /***************Calls up Up Phone camera********************/
    private void dispatchTakePictureIntent(int CAMERA_CODE) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            //Borrower profile image result
            Bundle extras = data.getExtras();
            //Bitmap returned from camera
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            borrowerImageView.setImageBitmap(imageBitmap);
            borrowerImage = imageBitmap;

        } else if (requestCode == REQUEST_CODE_FILES && resultCode == RESULT_OK) {

            //Borrower profile image result
            Bundle extras = data.getExtras();
            //Bitmap returned from camera
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //uploadImageFile(imageBitmap);

        }
    }

    /************Adds new borrower***************/
    public void addNewBorrower(){
        BorrowersTable borrowersTable = new BorrowersTable();

        //Assigning borrower parameters
        borrowersTable.setBorrowerLocationLongitude(borrowerLatLng.longitude);
        borrowersTable.setBorrowerLocationLatitude(borrowerLatLng.latitude);
        borrowersTable.setProfileImageUri(borrowerImageUri);
        borrowersTable.setProfileImageThumbUri(borrowerImageThumbUri);
        borrowersTable.setFirstName(firstNameTextView.getText().toString());
        borrowersTable.setLastName(lastNameTextView.getText().toString());
        borrowersTable.setMiddleName(middleNameTextView.getText().toString());
        borrowersTable.setWorkAddress(businessAddressTextView.getText().toString());
        borrowersTable.setHomeAddress(homeAddressTextView.getText().toString());
        borrowersTable.setCity(cityTextView.getText().toString());
        borrowersTable.setBusinessDescription(businessDescTextView.getText().toString());
        borrowersTable.setBusinessName(businessNameTextView.getText().toString());

        try {
            borrowersTable.setPhoneNumber(Long.parseLong(phoneNumberTextView.getText().toString()));
        } catch (NumberFormatException e) {
            Log.d(TAG, "addNewBorrower: "+e.getMessage());
        }

        borrowersTable.setEmail(emailTextView.getText().toString());
        borrowersTable.setDateOfBirth(dateOfBirthTextView.getText().toString());
        borrowersTable.setLoanOfficerId(account.getCurrentUserId());
        borrowersTable.setState(stateTextView.getText().toString());
        borrowersTable.setSex(selectedSex);
        borrowersTable.setNationality(selectedCountry);
        borrowersTable.setTimestamp(new Date());

        //Adding new borrower to database
        borrowersQueries.addNewBorrower(borrowersTable)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddSingleBorrower.this, "Done", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(AddSingleBorrower.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

                                        //Add borrower to cloud
                                        addNewBorrower();
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Failed 2", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(getApplicationContext(), "failed 1", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

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
                                        Toast.makeText(AddSingleBorrower.this, "Done", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(AddSingleBorrower.this, "Failed 2", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(AddSingleBorrower.this, "Failed 1", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
