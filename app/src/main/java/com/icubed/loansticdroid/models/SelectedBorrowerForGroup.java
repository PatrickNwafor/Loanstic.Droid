package com.icubed.loansticdroid.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.icubed.loansticdroid.localdatabase.BorrowersTable;

public class SelectedBorrowerForGroup implements Parcelable {
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

    @Override
    public String toString() {
        return "SelectedBorrowerForGroup{" + "imageUri='" + imageUri + '\'' + ", imageThumbUri='" + imageThumbUri + '\'' + ", selectedImageView=" + selectedImageView + ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' + ", borrowersId='" + borrowersId + '\'' + '}';
    }

    // Parcelling part
    public SelectedBorrowerForGroup(Parcel in){
        String[] data = new String[5];

        in.readStringArray(data);
        this.borrowersId = data[0];
        this.firstName = data[1];
        this.lastName = data[2];
        this.imageUri = data[3];
        this.imageThumbUri = data[4];

    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.borrowersId,
                this.firstName, this.lastName, this.imageUri, this.imageThumbUri });
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SelectedBorrowerForGroup createFromParcel(Parcel in) {
            return new SelectedBorrowerForGroup(in);
        }

        public SelectedBorrowerForGroup[] newArray(int size) {
            return new SelectedBorrowerForGroup[size];
        }
    };
}
