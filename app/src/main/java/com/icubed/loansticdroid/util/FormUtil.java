package com.icubed.loansticdroid.util;

import android.text.TextUtils;
import android.widget.EditText;

public class FormUtil {

    public FormUtil(){}

    /*********Check if a single form field is empty********/
    public boolean isSingleFormEmpty(EditText editText){
        if(TextUtils.isEmpty(editText.getText().toString())){
            return true;
        }
        return false;
    }

    /*********To check if email format is correct**********/
    public boolean isValidEmail(CharSequence email) {
        if (email == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    /************Check if an array of form field is empty*********/
    public boolean[] isListOfFormsEmpty(EditText[] editTextList){
        boolean[] booleans = new boolean[editTextList.length];
        for(int i = 0; i < editTextList.length; i++){
            if (TextUtils.isEmpty(editTextList[i].getText().toString())) {
                booleans[i]  = true;
            }else{
                booleans[i] = false;
            }
        }

        return booleans;
    }

    /**********Check if a form only contains numbers***********/
    public boolean doesFormContainNumbersOnly(EditText editText){
        if (editText.getText().toString().matches("[0-9]+")
                && editText.getText().toString().length() > 2) {
            return true;
        }

        return false;
    }

    public boolean doesFormContainDoublesOnly(EditText editText){
        try {
            Double.parseDouble(editText.getText().toString());
            editText.setError(null);
            return true;
        } catch (NumberFormatException e) {
            editText.setError("Only numbers are allowed");
            return false;
        }
    }

    public boolean doesFormContainIntegersOnly(EditText editText){
        try {
            Integer.parseInt(editText.getText().toString());
            editText.setError(null);
            return true;
        } catch (NumberFormatException e) {
            editText.setError("Only integer numbers are allowed");
            return false;
        }
    }

}
