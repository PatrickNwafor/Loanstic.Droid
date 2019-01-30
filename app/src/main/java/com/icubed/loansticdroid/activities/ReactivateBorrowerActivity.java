package com.icubed.loansticdroid.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.Index;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.UploadTask;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.adapters.FilesRecyclerAdapter;
import com.icubed.loansticdroid.cloudqueries.Account;
import com.icubed.loansticdroid.cloudqueries.ActivityCycleQueries;
import com.icubed.loansticdroid.cloudqueries.BorrowerFilesQueries;
import com.icubed.loansticdroid.cloudqueries.BorrowersQueries;
import com.icubed.loansticdroid.notification.BorrowerPendingApprovalNotificationTable;
import com.icubed.loansticdroid.notification.BorrowerPendingApprovalNotificationTableQueries;
import com.icubed.loansticdroid.util.AndroidUtils;
import com.icubed.loansticdroid.util.LocationProviderUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class ReactivateBorrowerActivity extends AppCompatActivity {

    private String borrowerId;
    private Toolbar toolbar;

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
    Context context;
    private Button submitButton;
    public TextView addFileTextView;
    private ActivityCycleQueries activityCycleQueries;
    private BorrowerFilesQueries borrowerFilesQueries;
    private ProgressBar reg_progress_bar;
    private RecyclerView filesRecyclerView;
    private FilesRecyclerAdapter filesRecyclerAdapter;
    private ArrayList<String> filesDescription;
    Bundle bundle;
    LinearLayout idLayout, driverLayout, passportLayout, otherLayout;
    public Bitmap frontId, backId, driverLicense, passport = null;
    public ArrayList<Bitmap> otherFile;
    public ArrayList<String> otherFileDesc;
    private int otherFilesCount = 0;
    private Account account;
    private BorrowerPendingApprovalNotificationTableQueries borrowerPendingApprovalNotificationTableQueries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reactivate_borrower);

        toolbar = findViewById(R.id.activate_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Upload Documents");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        borrowerId = getIntent().getStringExtra("borrowerId");
        account = new Account();
        borrowerPendingApprovalNotificationTableQueries = new BorrowerPendingApprovalNotificationTableQueries();

        filesRecyclerView = findViewById(R.id.add_files_list);
        filesDescription = new ArrayList<>();
        filesRecyclerAdapter = new FilesRecyclerAdapter(filesDescription, this);
        filesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        filesRecyclerView.setAdapter(filesRecyclerAdapter);

        otherFile = new ArrayList<>();
        otherFileDesc = new ArrayList<>();

        activityCycleQueries = new ActivityCycleQueries();

        //Changing action bar title
        addFileTextView = findViewById(R.id.addFileTextView);
        reg_progress_bar = findViewById(R.id.reg_progress_bar);
        submitButton = findViewById(R.id.submit);
        idLayout = findViewById(R.id.idLayout);
        driverLayout = findViewById(R.id.driverLayout);
        passportLayout = findViewById(R.id.passportLayout);
        otherLayout = findViewById(R.id.otherLayout);
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
                Intent i = new Intent(getApplicationContext(),BorrowerFilesIdCard.class);
                i.putExtra("idFront", frontId);
                i.putExtra("idBack", backId);
                startActivityForResult(i, ID_CARD_FILE);
            }
        });
        driverLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BorrrowerFileDriverLicense.class);
                intent.putExtra("image", driverLicense);
                startActivityForResult(intent, DRIVER_LICENSE_FILE);
            }
        });
        passportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BorrowerFilesPassport.class);
                intent.putExtra("image", passport);
                startActivityForResult(intent, PASSPORT_FILE);
            }
        });
        otherLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BorrowerFileOtherDocuments.class);
                intent.putStringArrayListExtra("files_desc", otherFileDesc);
                intent.putExtra("files", otherFile);
                startActivityForResult(intent, OTHER_FILES);
            }
        });
    }

    private void startSubmission(){
        if(AndroidUtils.isMobileDataEnabled(this)) {
            if(frontId != null || backId !=null || driverLicense != null
                    || passport != null || !otherFile.isEmpty()) {
                reg_progress_bar.setVisibility(View.VISIBLE);
                submitButton.setEnabled(false);
                createActivityCycle();
            }else{
                Toast.makeText(context, "Please upload at least one file", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context, "Request Failed, Please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void createActivityCycle() {
        Map<String, Object> activityCycleMap = new HashMap<>();
        activityCycleMap.put("isActive", false);
        activityCycleMap.put("borrowerId", borrowerId);
        activityCycleMap.put("startCycleTime", new Date());
        activityCycleMap.put("endCycleTime", null);

        activityCycleQueries.createNewActivityCycle(activityCycleMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "onSuccess: new borrower activity cycle created");

                        //Send notification
                        sendNotification(borrowerId);

                        //Upload files if available
                        uploadImageFile(borrowerId, documentReference.getId());
                        uploadImageOtherFiles(borrowerId, documentReference.getId());
                        goToBusinessVerification(borrowerId, documentReference.getId());
                    }
                });
    }

    private void sendNotification(String id) {
        BorrowerPendingApprovalNotificationTable borrowerPendingApprovalNotificationTable = new BorrowerPendingApprovalNotificationTable();
        borrowerPendingApprovalNotificationTable.setBorrowerId(id);
        borrowerPendingApprovalNotificationTable.setTimestamp(new Date());

        borrowerPendingApprovalNotificationTableQueries.sendNotification(
                borrowerPendingApprovalNotificationTable, account.getCurrentUserId()
        ).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "onComplete: notification sent");
                }else{
                    Log.d("Notification", "failed to send borrower pending approval notification");
                    Log.d(TAG, "onComplete: "+task.getException().getMessage());
                }
            }
        });
    }

    /******************Uploads borrower image file*******************/
    private void uploadImageFile(final String borrowerId, String activityCycleId) {

        final Bitmap[] allFilesBitMap = new Bitmap[]{frontId, backId, driverLicense, passport};
        String[] decs = new String[]{"Identification card front", "Identification card back", "Drivers license", "Passport"};

        for(int i = 0; i < allFilesBitMap.length; i++){

            if(allFilesBitMap[i] != null) {
                final Map<String, Object> filesMap = new HashMap<>();
                filesMap.put("fileDescription", decs[i]);
                filesMap.put("activityCycleId", activityCycleId);

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

    /*******************Uploads files uri to cloud firestore************************/
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

    /*****************Uploads other files to storage********************/
    private void uploadImageOtherFiles(final String borrowerId, String activityCycleId){

        if(otherFile != null){

            if(otherFile.isEmpty()){
                return;
            }

            for(final Bitmap bitmap : otherFile){

                final Map<String, Object> filesMap = new HashMap<>();
                filesMap.put("fileDescription", otherFileDesc.get(otherFilesCount));
                filesMap.put("activityCycleId", activityCycleId);

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
                otherFilesCount++;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ID_CARD_FILE && resultCode == RESULT_OK){
            frontId = StringToBitMap(data.getStringExtra(ID_CARD_FILE_FRONT));
            backId = StringToBitMap(data.getStringExtra(ID_CARD_FILE_BACK));

            //update recycler view
            if(!filesDescription.contains("Identification Card")){
                filesDescription.add("Identification Card");
                filesRecyclerAdapter.notifyDataSetChanged();
            }
        }else if(requestCode == DRIVER_LICENSE_FILE && resultCode == RESULT_OK){
            driverLicense = StringToBitMap(data.getStringExtra(DRIVER_LICENSE));

            //update recycler view
            if(!filesDescription.contains("Drivers license")) {
                filesDescription.add("Drivers license");
                filesRecyclerAdapter.notifyDataSetChanged();
            }
        }else if(requestCode == PASSPORT_FILE && resultCode == RESULT_OK){
            passport = StringToBitMap(data.getStringExtra(PASSPORT));

            //update recycler view
            if(!filesDescription.contains("Passport")) {
                filesDescription.add("Passport");
                filesRecyclerAdapter.notifyDataSetChanged();
            }
        }else if(requestCode == OTHER_FILES && resultCode == RESULT_OK){
            otherFile = data.getParcelableArrayListExtra(OTHER_DOC);
            otherFileDesc = data.getStringArrayListExtra(OTHER_DOC_DESC);

            //update recycler view{
            if(otherFileDesc != null) {
                for (String desc : otherFileDesc) {
                    if (!filesDescription.contains(desc)) {
                        filesDescription.add(desc);
                        filesRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
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

    public void goToBusinessVerification(String borrowerId, String activityCycleId){
        Intent addBorrowerIntent = new Intent(this, BusinessVerification.class);
        addBorrowerIntent.putExtra("borrowerId", borrowerId);
        addBorrowerIntent.putExtra("activityCycleId", activityCycleId);
        startActivity(addBorrowerIntent);
        finish();
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
