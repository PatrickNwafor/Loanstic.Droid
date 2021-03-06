package com.icubed.loansticdroid.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.cloudqueries.Account;
import com.icubed.loansticdroid.util.AndroidUtils;
import com.icubed.loansticdroid.util.FormUtil;

public class ResetPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ResetPasswordActivity";
    private EditText emailResetEditText;
    private Account account;
    private FormUtil formUtil;

    private AlertDialog.Builder digAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        account = new Account();
        formUtil = new FormUtil();

        digAlert = new AlertDialog.Builder(this);

        emailResetEditText = findViewById(R.id.emailResetView);
        Button resetBtn = findViewById(R.id.sendReset);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(AndroidUtils.isMobileDataEnabled(getApplicationContext())) {
                    resetPassword();
                }else{
                    Toast.makeText(ResetPasswordActivity.this, "Request failed, Please try again later", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    /*********Reset Password*****************/
    private void resetPassword(){

        final String resetEmail = emailResetEditText.getText().toString();

        //Checking form
        if(formUtil.isSingleFormEmpty(emailResetEditText)){
            emailResetEditText.setError("Email is required");
            emailResetEditText.requestFocus();
            return;
        }
        if(!formUtil.isValidEmail(resetEmail)){
            emailResetEditText.setError("Invalid Email format");
            emailResetEditText.requestFocus();
            return;
        }

        emailResetEditText.setError(null);

        account.resetPassword(resetEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    String message = "An email with a link to reset your password has been sent to you";
                    resetMessage(message, true);
                    Log.d(TAG, "Email sent.");

                }else{

                    Log.d(TAG, "Email sending failed.");
                    resetMessage( task.getException().getMessage(), false);

                }

            }
        });
    }

    /************Alert Dialog Message************/
    private void resetMessage(String message, final Boolean isEmailSent){
        digAlert.setTitle("Reset Password");
        digAlert.setMessage(message);
        digAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(isEmailSent) {
                    dialog.dismiss();
                    Intent loginIntent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                }else{
                    dialog.dismiss();
                }
            }
        });
        digAlert.create();
        digAlert.show();
    }
}
