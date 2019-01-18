package com.icubed.loansticdroid.cloudqueries;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.icubed.loansticdroid.localdatabase.AccountTable;

import java.util.HashMap;
import java.util.Map;

public class Account {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;

    public Account() {
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
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

    public Task<Void> saveDeviceToken(){

        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("device_token", deviceToken);

        return firebaseFirestore.collection("Account")
                .document(getCurrentUserId())
                .set(objectMap, SetOptions.merge());
    }

    /****************Sign Out of Account******************/
    public void signOutAccount(){
        auth.signOut();
    }

    /**
     * to save account details to database.
     * this is usually called after creating a new account
     * @param accountTable
     */
    public Task<Void> saveAccountToDatabase(AccountTable accountTable){
        return firebaseFirestore.collection("Account")
                .document(getCurrentUserId())
                .set(accountTable);
    }

    public Task<Void> saveAccountToDatabase(Map<String, Object> accountMap){
        return firebaseFirestore.collection("Account")
                .document(getCurrentUserId())
                .set(accountMap);
    }

    /**
     * retrieve an account details
     * @param accountId
     * @return
     */
    public Task<DocumentSnapshot> retrieveSingleAccount(String accountId){
        return firebaseFirestore.collection("Account")
                .document(accountId)
                .get();
    }

    /**
     * retrieve a all accounts details
     * @return
     */
    public Task<QuerySnapshot> retrieveAllAccounts(){
        return firebaseFirestore.collection("Account")
                .get();
    }

    /***********retrieves all loan officers accounts only*************/
    public Task<QuerySnapshot> retrieveAllLoanOfficersAccount(){
        return firebaseFirestore.collection("Account")
                .whereEqualTo("accountType", "loanOfficer")
                .get();
    }

    /***********retrieves all branch manager accounts only*************/
    public Task<QuerySnapshot> retrieveAllBranchManagerAccount(){
        return firebaseFirestore.collection("Account")
                .whereEqualTo("accountType", "branchManager")
                .get();
    }

    /***********Update an account*****************/
    public Task<Void> updateAccountDetails(String accountId, Map<String, Object> accountMap){
        return firebaseFirestore.collection("Account")
                .document(accountId)
                .update(accountMap);
    }

    public Task<Void> deleteAccountDetailsFromDatabase(String accountId){
        return firebaseFirestore.collection("Account")
                .document(accountId)
                .delete();
    }
}