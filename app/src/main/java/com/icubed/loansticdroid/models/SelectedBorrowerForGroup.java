package com.icubed.loansticdroid.models;

import android.widget.ImageView;

import com.icubed.loansticdroid.localdatabase.BorrowersTable;

public class SelectedBorrowerForGroup {
    private String imageUri;
    private String imageThumbUri;
    private ImageView selectedImageView;
    private String firstName, lastName;
    private String borrowersId;

    public SelectedBorrowerForGroup() {
    }

    public SelectedBorrowerForGroup(String imageUri, String imageThumbUri, ImageView selectedImageView, String firstName, String lastName, String borrowersId) {
        this.imageUri = imageUri;
        this.imageThumbUri = imageThumbUri;
        this.selectedImageView = selectedImageView;
        this.firstName = firstName;
        this.lastName = lastName;
        this.borrowersId = borrowersId;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public ImageView getSelectedImageView() {
        return selectedImageView;
    }

    public void setSelectedImageView(ImageView selectedImageView) {
        this.selectedImageView = selectedImageView;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImageThumbUri() {
        return imageThumbUri;
    }

    public void setImageThumbUri(String imageThumbUri) {
        this.imageThumbUri = imageThumbUri;
    }

    public String getBorrowersId() {
        return borrowersId;
    }

    public void setBorrowersId(String borrowersId) {
        this.borrowersId = borrowersId;
    }
}
