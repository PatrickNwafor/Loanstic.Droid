package com.icubed.loansticdroid.activities;

import android.Manifest;
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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.models.Account;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.util.FormUtil;
import com.icubed.loansticdroid.util.LocationProviderUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddSingleBorrower extends AppCompatActivity {


    LinearLayout layout1,layout2,layout3,layout4,layout5,layout6,layout7;
    Button next,next1,previous,takePhoto;
    ImageView retakePhoto;

    private static final String TAG = ".AddSingleBorrower";
    private static final int CAMERA_REQUEST_CODE = 335;
    Spinner sexDrp,citizenship;
    private static final String DEFAULT_LOCAL = "Nigeria";

    //for location
    private static final int REQUEST_CODE_FILES = 222;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private ProgressBar reg_progress_bar;
    private BorrowersQueries borrowersQueries;
    private Account account;
    private String selectedSex, borrowerImageUri, borrowerImageThumbUri;
    private String selectedCountry;
    private EditText firstNameTextView, middleNameTextView, lastNameTextView
            ,phoneNumberTextView, emailTextView, dateOfBirthTextView
            ,homeAddressTextView, businessAddressTextView, cityTextView
            ,stateTextView, zipCodeTextView, businessNameTextView, businessDescTextView;

    private Button submitBorrowerBtn;
    private Button borrowerFileBtn;
    private CircleImageView borrowerImageView;

    private Bitmap borrowerImage = null;
    private LatLng borrowerLatLng;
    private Index index;

    private FormUtil formUtil;
    private LocationProviderUtil locationProviderUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_single_borrower);

        //Algolia search initiation
        Client client = new Client("HGQ25JRZ8Y", "d4453ddf82775ee2324c47244b30a7c7");
        index = client.getIndex("Borrowers");

        locationProviderUtil = new LocationProviderUtil(this);

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
        reg_progress_bar = findViewById(R.id.reg_progressBar);


        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);
        layout4 = findViewById(R.id.layout4);
        layout5 = findViewById(R.id.layout5);
        layout6 = findViewById(R.id.layout6);
        layout7 = findViewById(R.id.layout7);

        next = findViewById(R.id.next);
        next1 = findViewById(R.id.next1);
        previous = findViewById(R.id.previous);
        takePhoto = findViewById(R.id.verify);
        retakePhoto = findViewById(R.id.start_camera_button);

        ArrayAdapter<CharSequence> adapterSex;
        String[] sexArr = {"Male", "Female"};
        adapterSex = new ArrayAdapter<CharSequence>(this,android.R.layout.simple_spinner_item,sexArr);
        adapterSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexDrp.setAdapter(adapterSex);
        selectedSex = sexDrp.getSelectedItem().toString();

        borrowersQueries = new BorrowersQueries(this);
        account = new Account();

        formUtil = new FormUtil();

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

        //Get location permission
        locationProviderUtil.getLocationPermission();
    }

    public void submit_borrower(View view) {
        submitBorrowerBtn.setEnabled(false);
        reg_progress_bar.setVisibility(View.VISIBLE);
        uploadBorrowerPicture(borrowerImage);
    }

    private void getCameraPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            Log.d(TAG, "getCameraPermission: permission not granted");
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }else{
            dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE);
            Log.d(TAG, "getCameraPermission: permission already granted");
        }
    }

    /*************Adding borrower to search for indexing***********/
    private void createBorrowerSearch(DocumentReference borrowersDocRef){

        borrowersDocRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isComplete()){
                            BorrowersTable borrowersTable = task.getResult().toObject(BorrowersTable.class);
                            borrowersTable.setBorrowersId(task.getResult().getId());

                            Map<String, Object> searchMap = new HashMap<>();
                            searchMap.put("lastName", borrowersTable.getLastName());
                            searchMap.put("firstName", borrowersTable.getFirstName());
                            searchMap.put("businessName", borrowersTable.getBusinessName());
                            searchMap.put("profileImageUri", borrowersTable.getProfileImageUri());
                            searchMap.put("profileImageThumbUri", borrowersTable.getProfileImageThumbUri());

                            JSONObject object = new JSONObject(searchMap);
                            index.addObjectAsync(object, borrowersTable.getBorrowersId(), null);

                            reg_progress_bar.setVisibility(View.GONE);
                            submitBorrowerBtn.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "New borrower Added Successfully", Toast.LENGTH_SHORT).show();

                            Intent addBorrowerIntent = new Intent(AddSingleBorrower.this, LetsVerifyBusiness.class);
                            addBorrowerIntent.putExtra("borrowerId", borrowersTable.getBorrowersId());
                            startActivity(addBorrowerIntent);

                        }else{
                            reg_progress_bar.setVisibility(View.GONE);
                            submitBorrowerBtn.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "Failure to create search index for new borrower", Toast.LENGTH_SHORT).show();
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


    public void start_camera(View view) {
        dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE);
        next1.setVisibility(View.VISIBLE);
        takePhoto.setVisibility(View.INVISIBLE);
        retakePhoto.setVisibility(View.VISIBLE);
    }

    /***************Calls up Up Phone camera********************/
    private void dispatchTakePictureIntent(int CAMERA_CODE) {
        getCameraPermission();
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

        Log.d(TAG, "addNewBorrower: called");

        //Assigning borrower parameter
        Map<String, Object> borrowerMap = new HashMap<>();
        borrowerMap.put("borrowerLocationLongitude", borrowerLatLng.longitude);
        borrowerMap.put("borrowerLocationLatitude", borrowerLatLng.latitude);
        borrowerMap.put("profileImageUri", borrowerImageUri);
        borrowerMap.put("profileImageThumbUri", borrowerImageThumbUri);
        borrowerMap.put("firstName", firstNameTextView.getText().toString());
        borrowerMap.put("lastName", lastNameTextView.getText().toString());
        borrowerMap.put("middleName", middleNameTextView.getText().toString());
        borrowerMap.put("workAddress", businessAddressTextView.getText().toString());
        borrowerMap.put("homeAddress", homeAddressTextView.getText().toString());
        borrowerMap.put("city", cityTextView.getText().toString());
        borrowerMap.put("businessDescription", businessDescTextView.getText().toString());
        borrowerMap.put("businessName", businessNameTextView.getText().toString());
        borrowerMap.put("phoneNumber", Long.parseLong(phoneNumberTextView.getText().toString()));
        borrowerMap.put("email", emailTextView.getText().toString());
        borrowerMap.put("zipcode", Long.parseLong(zipCodeTextView.getText().toString()));
        borrowerMap.put("dateOfBirth", dateOfBirthTextView.getText().toString());
        borrowerMap.put("loanOfficerId", account.getCurrentUserId());
        borrowerMap.put("state", stateTextView.getText().toString());
        borrowerMap.put("sex", selectedSex);
        borrowerMap.put("nationality", selectedCountry);
        borrowerMap.put("timestamp", new Date());

        //Adding new borrower to database
        borrowersQueries.addNewBorrower(borrowerMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful() && task.isComplete()) {
                            //Adding borrower to search index
                            createBorrowerSearch(task.getResult());
                        }else{
                            reg_progress_bar.setVisibility(View.GONE);
                            submitBorrowerBtn.setEnabled(true);
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

                                        //getCurrentLocation
                                        getCurrentLocation();
                                    }else{
                                        reg_progress_bar.setVisibility(View.GONE);
                                        submitBorrowerBtn.setEnabled(true);
                                        Toast.makeText(getApplicationContext(), "Failed 2", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    reg_progress_bar.setVisibility(View.GONE);
                    submitBorrowerBtn.setEnabled(true);
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

    public void next_layout(View view) {
        if(layout1.getVisibility() == View.VISIBLE){

            //Checking form
            EditText[] editTexts = new EditText[]{firstNameTextView, lastNameTextView};
            if (isAnyFormEmpty(editTexts)) {
                return;
            }
            layout1.setVisibility(View.INVISIBLE);
            layout2.setVisibility(View.VISIBLE);
            layout3.setVisibility(View.INVISIBLE);
            layout4.setVisibility(View.INVISIBLE);
            layout5.setVisibility(View.INVISIBLE);
            previous.setVisibility(View.VISIBLE);
            next1.setVisibility(View.VISIBLE);
            next.setVisibility(View.INVISIBLE);

        }
        else if (layout2.getVisibility()== View.VISIBLE)
        {
            //checking form
            if(formUtil.isSingleFormEmpty(dateOfBirthTextView)){
                dateOfBirthTextView.setError("This Field is required");
                dateOfBirthTextView.requestFocus();
                return;
            }else{
                dateOfBirthTextView.setError(null);
            }

            layout1.setVisibility(View.INVISIBLE);
            layout2.setVisibility(View.INVISIBLE);
            layout3.setVisibility(View.VISIBLE);
            layout4.setVisibility(View.INVISIBLE);
            layout5.setVisibility(View.INVISIBLE);

        }
        else if (layout3.getVisibility()== View.VISIBLE)
        {
            //Checking form
            EditText[] editTexts = new EditText[]{cityTextView, stateTextView, zipCodeTextView};
            if (isAnyFormEmpty(editTexts))
                return;
            if(!doesFieldContainNumberOnly(zipCodeTextView))
                return;

            layout1.setVisibility(View.INVISIBLE);
            layout2.setVisibility(View.INVISIBLE);
            layout3.setVisibility(View.INVISIBLE);
            layout4.setVisibility(View.VISIBLE);
            layout5.setVisibility(View.INVISIBLE);
        }
        else if (layout4.getVisibility()== View.VISIBLE)
        {

            //Checking form
            EditText[] editTexts = new EditText[]{phoneNumberTextView, emailTextView, homeAddressTextView};
            if (isAnyFormEmpty(editTexts))
                return;
            //Checking phone numbers
            if(!doesFieldContainNumberOnly(phoneNumberTextView))
                return;
            //Checking if email format is valid
            if(!formUtil.isValidEmail(emailTextView.getText().toString())) {
                emailTextView.setError("Invalid email format");
                return;
            }

            layout1.setVisibility(View.INVISIBLE);
            layout2.setVisibility(View.INVISIBLE);
            layout3.setVisibility(View.INVISIBLE);
            layout4.setVisibility(View.INVISIBLE);
            layout5.setVisibility(View.VISIBLE);
        }
        else if (layout5.getVisibility()== View.VISIBLE)
        {

            //Checking form
            EditText[] editTexts = new EditText[]{businessNameTextView, businessDescTextView, businessAddressTextView};
            if (isAnyFormEmpty(editTexts))
                return;

            layout1.setVisibility(View.INVISIBLE);
            layout2.setVisibility(View.INVISIBLE);
            layout3.setVisibility(View.INVISIBLE);
            layout4.setVisibility(View.INVISIBLE);
            layout5.setVisibility(View.INVISIBLE);
            layout6.setVisibility(View.VISIBLE);
            takePhoto.setVisibility(View.VISIBLE);
            next1.setVisibility(View.INVISIBLE);
        }
        else if (layout6.getVisibility()== View.VISIBLE)
        {
            if(borrowerImage == null){
                Toast.makeText(this, "Cannot procede without snapping borrower", Toast.LENGTH_SHORT).show();
                return;
            }

            layout1.setVisibility(View.INVISIBLE);
            layout2.setVisibility(View.INVISIBLE);
            layout3.setVisibility(View.INVISIBLE);
            layout4.setVisibility(View.INVISIBLE);
            layout5.setVisibility(View.INVISIBLE);
            layout6.setVisibility(View.INVISIBLE);
            layout7.setVisibility(View.VISIBLE);
            takePhoto.setVisibility(View.INVISIBLE);
            next1.setVisibility(View.INVISIBLE);
            submitBorrowerBtn.setVisibility(View.VISIBLE);
            previous.setVisibility(View.INVISIBLE);
        }


    }

    public void previous_layout(View view) {
        if(layout6.getVisibility()==View.VISIBLE)
        {
            layout1.setVisibility(View.INVISIBLE);
            layout2.setVisibility(View.INVISIBLE);
            layout3.setVisibility(View.INVISIBLE);
            layout4.setVisibility(View.INVISIBLE);
            layout5.setVisibility(View.VISIBLE);
            layout6.setVisibility(View.INVISIBLE);
            next1.setVisibility(View.VISIBLE);
            takePhoto.setVisibility(View.INVISIBLE);
            retakePhoto.setVisibility(View.INVISIBLE);
        }
        else if(layout5.getVisibility()==View.VISIBLE)
        {
            layout1.setVisibility(View.INVISIBLE);
            layout2.setVisibility(View.INVISIBLE);
            layout3.setVisibility(View.INVISIBLE);
            layout4.setVisibility(View.VISIBLE);
            layout5.setVisibility(View.INVISIBLE);
            layout6.setVisibility(View.INVISIBLE);
        }
        else if (layout4.getVisibility()==View.VISIBLE)
        {
            layout1.setVisibility(View.INVISIBLE);
            layout2.setVisibility(View.INVISIBLE);
            layout3.setVisibility(View.VISIBLE);
            layout4.setVisibility(View.INVISIBLE);
            layout5.setVisibility(View.INVISIBLE);
            layout6.setVisibility(View.INVISIBLE);
        }
        else if (layout3.getVisibility()==View.VISIBLE)
        {
            layout1.setVisibility(View.INVISIBLE);
            layout2.setVisibility(View.VISIBLE);
            layout3.setVisibility(View.INVISIBLE);
            layout4.setVisibility(View.INVISIBLE);
            layout5.setVisibility(View.INVISIBLE);
            layout6.setVisibility(View.INVISIBLE);
        }
        else if (layout2.getVisibility()==View.VISIBLE)
        {
            layout1.setVisibility(View.VISIBLE);
            layout2.setVisibility(View.INVISIBLE);
            layout3.setVisibility(View.INVISIBLE);
            layout4.setVisibility(View.INVISIBLE);
            layout5.setVisibility(View.INVISIBLE);
            layout6.setVisibility(View.INVISIBLE);
            previous.setVisibility(View.INVISIBLE);
            next.setVisibility(View.VISIBLE);
            next1.setVisibility(View.INVISIBLE);
        }
    }

    private Boolean isAnyFormEmpty(EditText[] forms){
        Boolean isFormEmpty = false;
        boolean[] listOfFormsEmpty = formUtil.isListOfFormsEmpty(forms);

        for(int i = 0; i < forms.length; i++){
            if(listOfFormsEmpty[i]){
                forms[i].setError("Field is required");

                if(!isFormEmpty) {
                    forms[i].requestFocus();
                }

                isFormEmpty = true;
            }else{
                forms[i].setError(null);
            }
        }

        return isFormEmpty;
    }

    private Boolean doesFieldContainNumberOnly(EditText editText){
        if(!formUtil.doesFormContainNumbersOnly(editText)){
            editText.setError("Only numbers are allowed");
            return false;
        }else {
            editText.setError(null);
        }

        return true;
    }

}
