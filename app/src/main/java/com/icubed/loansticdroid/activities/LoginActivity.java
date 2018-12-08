package com.icubed.loansticdroid.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.firebase.auth.AuthResult;
import com.icubed.loansticdroid.R;
import com.icubed.loansticdroid.models.Account;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText emailTextView;
    private EditText passwordTextView;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailTextView = findViewById(R.id.emailTextView);
        passwordTextView = findViewById(R.id.passwordTextView);
        loginBtn = findViewById(R.id.loginAcctBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginToAccount();
            }
        });
    }

    /**************Singing In to Account***************/
    private void loginToAccount(){

        Account account = new Account();
        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && account.isValidEmail(email)) {

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

                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }

    }
}
