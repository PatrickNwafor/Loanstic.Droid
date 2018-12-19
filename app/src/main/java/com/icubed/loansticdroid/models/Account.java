package com.icubed.loansticdroid.models;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Account {

    private FirebaseAuth auth;


    public Account() {
        auth = FirebaseAuth.getInstance();
    }

    /*********To check if email format is correct**********/
    public boolean isValidEmail(CharSequence email) {
        if (email == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    /********************Reset Account Password*****************/
    public Task<Void> resetPassword(String email){
        return auth.sendPasswordResetEmail(email);
    }

    /*********************To verify email address*******************/
    public Task<Void> verifyEmail(){
        return getCurrentUser().sendEmailVerification();
    }

    /************To check if user is already logged in**************/
    public boolean isUserLoggedIn(){
        FirebaseUser currentUser = auth.getCurrentUser();

        if(currentUser == null){
            return false;
        }else{
            return true;
        }
    }

    /***************Creating a new Account***************/
    public Task<AuthResult> createNewAccount(String email, String password){
        return auth.createUserWithEmailAndPassword(email, password);
    }

    /************Signing in into an already existing account***************/
    public Task<AuthResult> signIntoAccount(String email, String password){
        return auth.signInWithEmailAndPassword(email, password);
    }

    /***************Get User****************/
    public FirebaseUser getCurrentUser(){
        return auth.getCurrentUser();
    }

    /****************Get Current User Id****************/
    public String getCurrentUserId(){
        return auth.getCurrentUser().getUid();
    }

    /****************Sign Out of Account******************/
    public void signOutAccount(){
        auth.signOut();
    }

}