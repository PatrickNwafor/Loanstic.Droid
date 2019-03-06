package com.icubed.loansticdroid.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.icubed.loansticdroid.localdatabase.BorrowersTable;

import java.util.Arrays;

public class SelectedBorrowerForGroup implements Parcelable {
    private String imageUri;
    private String imageThumbUri, businessName;
    private ImageView selectedImageView;
    private String firstName, lastName;
    private String borrowersId;
    private boolean belongsToGroup;
    private byte[] imageByteArray;

    public SelectedBorrowerForGroup() {
    }

    public SelectedBorrowerForGroup(String imageUri, String imageThumbUri, String businessName, ImageView selectedImageView, String firstName, String lastName, String borrowersId, boolean belongsToGroup, byte[] imageByteArray) {
        this.imageUri = imageUri;
        this.imageThumbUri = imageThumbUri;
        this.businessName = businessName;
        this.selectedImageView = selectedImageView;
        this.firstName = firstName;
        this.lastName = lastName;
        this.borrowersId = borrowersId;
        this.belongsToGroup = belongsToGroup;
        this.imageByteArray = imageByteArray;
    }

    public byte[] getImageByteArray() {
        return imageByteArray;
    }

    public void setImageByteArray(byte[] imageByteArray) {
        this.imageByteArray = imageByteArray;
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

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public boolean isBelongsToGroup() {
        return belongsToGroup;
    }

    public void setBelongsToGroup(boolean belongsToGroup) {
        this.belongsToGroup = belongsToGroup;
    }

    @Override
    public String toString() {
        return "SelectedBorrowerForGroup{" + "imageUri='" + imageUri + '\'' + ", imageThumbUri='" + imageThumbUri + '\'' + ", businessName='" + businessName + '\'' + ", selectedImageView=" + selectedImageView + ", firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' + ", borrowersId='" + borrowersId + '\'' + ", belongsToGroup=" + belongsToGroup + ", imageByteArray=" + Arrays.toString(imageByteArray) + '}';
    }

    // Parcelling part
    public SelectedBorrowerForGroup(Parcel in){
        this.borrowersId = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.businessName = in.readString();
        this.imageUri = in.readString();
        this.imageThumbUri = in.readString();
        this.belongsToGroup = Boolean.parseBoolean(in.readString());
        imageByteArray = new byte[in.readInt()];
        in.readByteArray(imageByteArray);
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.borrowersId);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.businessName);
        dest.writeString(this.imageUri);
        dest.writeString(this.imageThumbUri);
        dest.writeString(String.valueOf(this.belongsToGroup));
        dest.writeInt(imageByteArray.length);
        dest.writeByteArray(imageByteArray);
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
