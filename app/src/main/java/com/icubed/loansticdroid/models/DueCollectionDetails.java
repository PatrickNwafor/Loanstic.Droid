package com.icubed.loansticdroid.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.icubed.loansticdroid.localdatabase.CollectionTable;

public class DueCollectionDetails implements Parcelable {

    private String firstName, lastName, workAddress, businessName, dueCollectionDate, imageUri, imageUriThumb;
    private int collectionNumber;
    private double dueAmount, amountPaid;
    private Boolean isDueCollected;
    private String groupName;
    private byte[] imageByteArray;
    private double latitude, longitude;
    private CollectionTable collectionTable;

    public DueCollectionDetails(){}

    public DueCollectionDetails(String firstName, String lastName, String workAddress, String businessName, String dueCollectionDate, String imageUri, String imageUriThumb, int collectionNumber, double dueAmount, double amountPaid, Boolean isDueCollected, String groupName, byte[] imageByteArray, double latitude, double longitude, CollectionTable collectionTable) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.workAddress = workAddress;
        this.businessName = businessName;
        this.dueCollectionDate = dueCollectionDate;
        this.imageUri = imageUri;
        this.imageUriThumb = imageUriThumb;
        this.collectionNumber = collectionNumber;
        this.dueAmount = dueAmount;
        this.amountPaid = amountPaid;
        this.isDueCollected = isDueCollected;
        this.groupName = groupName;
        this.imageByteArray = imageByteArray;
        this.latitude = latitude;
        this.longitude = longitude;
        this.collectionTable = collectionTable;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageUriThumb() {
        return imageUriThumb;
    }

    public void setImageUriThumb(String imageUriThumb) {
        this.imageUriThumb = imageUriThumb;
    }

    public byte[] getImageByteArray() {
        return imageByteArray;
    }

    public void setImageByteArray(byte[] imageByteArray) {
        this.imageByteArray = imageByteArray;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getDueCollectionDate() {
        return dueCollectionDate;
    }

    public void setDueCollectionDate(String dueCollectionDate) {
        this.dueCollectionDate = dueCollectionDate;
    }

    public int getCollectionNumber() {
        return collectionNumber;
    }

    public void setCollectionNumber(int collectionNumber) {
        this.collectionNumber = collectionNumber;
    }

    public double getDueAmount() {
        return dueAmount;
    }

    public void setDueAmount(double dueAmount) {
        this.dueAmount = dueAmount;
    }

    public Boolean getIsDueCollected() {
        return isDueCollected;
    }

    public void setIsDueCollected(Boolean dueCollected) {
        isDueCollected = dueCollected;
    }

    public CollectionTable getCollectionTable() {
        return collectionTable;
    }

    public void setCollectionTable(CollectionTable collectionTable) {
        this.collectionTable = collectionTable;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    @Override
    public String toString() {
        return "DueCollectionDetails{" + "firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' + ", workAddress='" + workAddress + '\'' + ", businessName='" + businessName + '\'' + ", dueCollectionDate='" + dueCollectionDate + '\'' + ", collectionNumber=" + collectionNumber + ", dueAmount=" + dueAmount + ", isDueCollected=" + isDueCollected + '}';
    }

    // Parcelling part
    public DueCollectionDetails(Parcel in){
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.workAddress = in.readString();
        this.businessName = in.readString();
        this.dueCollectionDate = in.readString();
        this.imageUri = in.readString();
        this.imageUriThumb = in.readString();
        this.collectionNumber = in.readInt();
        this.dueAmount = in.readDouble();
        this.isDueCollected = Boolean.valueOf(in.readString());
        this.groupName = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.amountPaid = in.readDouble();
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.workAddress);
        dest.writeString(this.businessName);
        dest.writeString(this.dueCollectionDate);
        dest.writeString(this.imageUri);
        dest.writeString(this.imageUriThumb);
        dest.writeInt(this.collectionNumber);
        dest.writeDouble(this.dueAmount);
        dest.writeString(String.valueOf(this.isDueCollected));
        dest.writeString(this.groupName);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.amountPaid);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DueCollectionDetails createFromParcel(Parcel in) {
            return new DueCollectionDetails(in);
        }

        public DueCollectionDetails[] newArray(int size) {
            return new DueCollectionDetails[size];
        }
    };
}
