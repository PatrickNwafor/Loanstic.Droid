package com.icubed.loansticdroid.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.models.Account;

public class ResetPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ResetPasswordActivity";
    private EditText emailResetEditText;
    private Button resetBtn;

    private Account account;

    private AlertDialog.Builder digAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        account = new Account();

        digAlert = new AlertDialog.Builder(this);

        emailResetEditText = findViewById(R.id.emailResetView);
        resetBtn = findViewById(R.id.sendReset);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resetPassword();

            }
        });
    }

    /*********Reset Password*****************/
    private void resetPassword(){

        final String resetEmail = emailResetEditText.getText().toString();

        if(isFormProperlyFilled(resetEmail)){

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

    /************Checks if form is filled properly**************/
    private Boolean isFormProperlyFilled(String email){

        if(TextUtils.isEmpty(email)){

            emailResetEditText.setError("Email is required");
            emailResetEditText.requestFocus();
            return false;

        }

        if(!account.isValidEmail(email)){

            emailResetEditText.setError("Invalid Email format");
            emailResetEditText.requestFocus();
            return false;

        }

        return true;

    }
}
