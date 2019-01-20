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
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.UploadTask;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.activities.AddSingleBorrower;
import com.icubed.loansticdroid.activities.BorrowerFileOtherDocuments;
import com.icubed.loansticdroid.activities.BorrowerFilesIdCard;
import com.icubed.loansticdroid.activities.BorrowerFilesPassport;
import com.icubed.loansticdroid.activities.BorrrowerFileDriverLicense;
import com.icubed.loansticdroid.cloudqueries.Account;
import com.icubed.loansticdroid.cloudqueries.BorrowerFilesQueries;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.util.AndroidUtils;
import com.icubed.loansticdroid.util.LocationProviderUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.support.constraint.Constraints.TAG;
import static com.icubed.loansticdroid.util.LocationProviderUtil.LOCATION_PERMISSION_REQUEST_CODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class BorrowerFilesFragment extends Fragment {

    public static final int REQUEST_CODE_FILES = 8282;
    private static final int ID_CARD_FILE = 2424;
    private static final int DRIVER_LICENSE_FILE = 722;
    private static final int PASSPORT_FILE = 2552;
    private static final int OTHER_FILES = 113;
    public static final String ID_CARD_FILE_FRONT = "id_front";
    public static final String ID_CARD_FILE_BACK = "id_back";
    public static final String DRIVER_LICENSE = "driver_license";
    public static final String PASSPORT = "passport";
    public static final String OTHER_DOC = "other_file";
    public static final String OTHER_DOC_DESC = "other_file_desc";
    private boolean gottenLocation = false;
    Context context;
    private Button submitButton;
    private BorrowersQueries borrowersQueries;
    private String borrowerImageUri;
    private String borrowerImageThumbUri;
    private LocationProviderUtil locationProviderUtil;
    private LatLng borrowerLatLng;
    private Account account;
    private Index index;
    private BorrowerFilesQueries borrowerFilesQueries;
    private ProgressBar reg_progress_bar;
    Bundle bundle;
    private Toolbar toolbar;
    LinearLayout idLayout, driverLayout, passportLayout, otherLayout;
    Bitmap frontId, backId, driverLicense, passport;
    private ArrayList<String> otherFile;
    private ArrayList<String> otherFileDesc;
    private int otherFilesCount = 0;

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

        otherFile = new ArrayList<>();
        otherFileDesc = new ArrayList<>();

        borrowersQueries = new BorrowersQueries(context);
        locationProviderUtil = new LocationProviderUtil(context);
        account = new Account();

        //Changing action bar title
        ((AddSingleBorrower) getContext()).actionBar.setTitle("ID Documents");

        reg_progress_bar = view.findViewById(R.id.reg_progress_bar);
        submitButton = view.findViewById(R.id.submit);
        toolbar = view.findViewById(R.id.ID_document_toolbar);
        idLayout = view.findViewById(R.id.idLayout);
        driverLayout = view.findViewById(R.id.driverLayout);
        passportLayout = view.findViewById(R.id.passportLayout);
        otherLayout = view.findViewById(R.id.otherLayout);
        borrowerFilesQueries = new BorrowerFilesQueries(context);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSubmission();
            }
        });
        idLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),BorrowerFilesIdCard.class);
                startActivityForResult(i, ID_CARD_FILE);
            }
        });
        driverLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BorrrowerFileDriverLicense.class);
                startActivityForResult(intent, DRIVER_LICENSE_FILE);
            }
        });
        passportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BorrowerFilesPassport.class);
                startActivityForResult(intent, PASSPORT_FILE);
            }
        });
        otherLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BorrowerFileOtherDocuments.class);
                startActivityForResult(intent, OTHER_FILES);
            }
        });
    }

    private void startSubmission(){
        if(AndroidUtils.isMobileDataEnabled(getContext())) {
            reg_progress_bar.setVisibility(View.VISIBLE);
            submitButton.setEnabled(false);
            Bitmap bitmap = StringToBitMap(bundle.getString("borrowerImage"));
            uploadBorrowerPicture(bitmap);
        }else{
            Toast.makeText(context, "Request Failed, Please try again", Toast.LENGTH_SHORT).show();
        }
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
                if(!gottenLocation) {
                    Location currentLocal = location.getLocation;
                    borrowerLatLng = new LatLng(currentLocal.getLatitude(), currentLocal.getLongitude());
                    addNewBorrower();
                    gottenLocation = true;
                }
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
        borrowerMap.put("assignedBy",  "");
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

                            //Upload files if available
                            uploadImageFile(task.getResult().getId());
                            uploadImageOtherFiles(task.getResult().getId());
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
                    Log.d(TAG, "onRequestPermissionResult: permission granted"  );
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
    private void uploadImageFile(final String borrowerId) {

        final Bitmap[] allFilesBitMap = new Bitmap[]{frontId, backId, driverLicense, passport};
        String[] decs = new String[]{"Identification card front", "Identification card back", "Drivers license", "Passport"};

        for(int i = 0; i < allFilesBitMap.length; i++){

            if(allFilesBitMap[i] != null) {
                final Map<String, Object> filesMap = new HashMap<>();
                filesMap.put("fileDescription", decs[i]);

                final int finalI = i;
                borrowerFilesQueries.uploadImageFiles(allFilesBitMap[i]).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            String imageUri = task.getResult().getDownloadUrl().toString();
                            filesMap.put("fileImageUri", imageUri);
                            Log.d(TAG, "onComplete: done uploading files to storage");

                            borrowerFilesQueries.uploadThumbImageFiles(allFilesBitMap[finalI]).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        String imageThumbUri = task.getResult().getDownloadUrl().toString();
                                        filesMap.put("fileImageUriThumb", imageThumbUri);
                                        filesMap.put("timestamp", new Date());
                                        Log.d(TAG, "onComplete: done uploading files thumb to storage");

                                        uploadImageFileToCloud(filesMap, borrowerId);

                                    } else {
                                        Toast.makeText(context, "Failed 2", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(context, "Failed uploading files", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        }
    }

    private void uploadImageOtherFiles(final String borrowerId){

        if(otherFile != null){

            for(final String bitmapString : otherFile){

                final Bitmap bitmap = StringToBitMap(bitmapString);

                final Map<String, Object> filesMap = new HashMap<>();
                filesMap.put("fileDescription", otherFileDesc.get(otherFilesCount));

                borrowerFilesQueries.uploadImageFiles(bitmap)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){

                                    String imageUri = task.getResult().getDownloadUrl().toString();
                                    filesMap.put("fileImageUri", imageUri);
                                    Log.d(TAG, "onComplete: done uploading files to storage");

                                    borrowerFilesQueries.uploadThumbImageFiles(bitmap)
                                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        if(task.isSuccessful()){

                                                            String imageThumbUri = task.getResult().getDownloadUrl().toString();
                                                            filesMap.put("fileImageUriThumb", imageThumbUri);
                                                            filesMap.put("timestamp", new Date());
                                                            Log.d(TAG, "onComplete: done uploading files thumb to storage");

                                                            uploadImageFileToCloud(filesMap, borrowerId);

                                                            otherFilesCount++;
                                                        }else{
                                                            Toast.makeText(context, "Failed uploading files thumb to storage", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }
                                            });

                                }else{
                                    Toast.makeText(context, "Failed uploading files to storage", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }

    private void uploadImageFileToCloud(Map<String, Object> filesMap, String borrowerId) {
        borrowerFilesQueries.saveFileToCloud(borrowerId, filesMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: done uploading files to cloud");
                        }else{
                            Toast.makeText(context, "Failed uploading files to cloud", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case (ID_CARD_FILE) : {
                if(resultCode == RESULT_OK){
                    frontId = StringToBitMap(data.getStringExtra(ID_CARD_FILE_FRONT));
                    backId = StringToBitMap(data.getStringExtra(ID_CARD_FILE_BACK));
                }
            }
            case DRIVER_LICENSE_FILE : {
                if(resultCode == RESULT_OK){
                    driverLicense = StringToBitMap(data.getStringExtra(DRIVER_LICENSE));
                }
            }
            case PASSPORT_FILE : {
                if(resultCode == RESULT_OK){
                    passport = StringToBitMap(data.getStringExtra(PASSPORT));
                }
            }
            case OTHER_FILES : {
                if (resultCode == RESULT_OK){
                    otherFile = data.getStringArrayListExtra(OTHER_DOC);
                    otherFileDesc = data.getStringArrayListExtra(OTHER_DOC_DESC);
                }
            }
        }
    }

}
