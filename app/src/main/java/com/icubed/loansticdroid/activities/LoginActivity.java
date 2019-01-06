package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.cloudqueries.Account;
import com.icubed.loansticdroid.util.FormUtil;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText emailTextView;
    private EditText passwordTextView;
    private Button loginBtn;
    private ProgressBar loginProgressBar;
    private TextView errorTextView;
    Animation frombottom,frombottom1,frombottom2;

    private Account account;
    private FormUtil formUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailTextView = findViewById(R.id.emailTextView);
        passwordTextView = findViewById(R.id.passwordTextView);
        loginBtn = findViewById(R.id.loginAcctBtn);
        loginProgressBar = findViewById(R.id.loginProgressBar);
        errorTextView = findViewById(R.id.errorTextView);
        TextView forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);

        formUtil = new FormUtil();
        account = new Account();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginToAccount();
            }
        });
        frombottom = AnimationUtils.loadAnimation( this,R.anim.frombottom);
        frombottom1 = AnimationUtils.loadAnimation( this,R.anim.frombottom1);
        frombottom2 = AnimationUtils.loadAnimation( this,R.anim.frombottom2);

        emailTextView.setAnimation(frombottom);
        passwordTextView.setAnimation(frombottom);
        loginBtn.setAnimation(frombottom1);
       forgotPasswordTextView.setAnimation(frombottom1);

       forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent forgetPasswordIntent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
               startActivity(forgetPasswordIntent);
           }
       });
    }

    /**************Singing In to Account***************/
    private void loginToAccount(){

        errorTextView.setVisibility(View.GONE);
        errorTextView.setText("");

        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();

        //Checking form
        EditText[] editTexts = new EditText[]{emailTextView, passwordTextView};
        if(isAnyFormEmpty(editTexts))
            return;
        //Checking if email format is valid
        if(!formUtil.isValidEmail(emailTextView.getText().toString())) {
            emailTextView.setError("Invalid email format");
            return;
        }

        loginProgressBar.setVisibility(View.VISIBLE);
        loginBtn.setEnabled(false);

        Task<AuthResult> resultTask = account.signIntoAccount(email, password);

        resultTask.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Account Login of "+task.getResult().getUser().getEmail()+ " Successful", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onComplete: Account Login successful");

                    Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(mainActivityIntent);
                    finish();
                }else{

                    loginProgressBar.setVisibility(View.GONE);
                    loginBtn.setEnabled(true);

                    //Determine the type of login error message
                    loginError(task.getException().getMessage());
                }
            }
        });

    }

    /****************To put out error message incase of a failed Login***************/
    private void loginError(String message){

        if(message.toLowerCase().contains("identifier".toLowerCase())){

            errorTextView.setVisibility(View.VISIBLE);
            errorTextView.setText("Account does not exist");

        }else if(message.toLowerCase().contains("password".toLowerCase())){

            errorTextView.setVisibility(View.VISIBLE);
            errorTextView.setText("Invalid Password");

        }else if(message.toLowerCase().contains("network".toLowerCase())){

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        }else {

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        }

    }

    /*************To check forms*******************/
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
}
