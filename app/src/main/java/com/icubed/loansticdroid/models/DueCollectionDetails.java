package com.icubed.loansticdroid.models;

public class DueCollectionDetails {

    private String firstName, lastName, workAddress, businessName, dueCollectionDate, imageUri, imageUriThumb;
    private int collectionNumber;
    private double dueAmount;
    private Boolean isDueCollected;
    private String groupName;
    private byte[] imageByteArray;
    private double latitude, longitude;

    public DueCollectionDetails(){}

    public DueCollectionDetails(String firstName, String lastName, String workAddress, String businessName, String dueCollectionDate, String imageUri, String imageUriThumb, int collectionNumber, double dueAmount, Boolean isDueCollected, String groupName, byte[] imageByteArray, double latitude, double longitude) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.workAddress = workAddress;
        this.businessName = businessName;
        this.dueCollectionDate = dueCollectionDate;
        this.imageUri = imageUri;
        this.imageUriThumb = imageUriThumb;
        this.collectionNumber = collectionNumber;
        this.dueAmount = dueAmount;
        this.isDueCollected = isDueCollected;
        this.groupName = groupName;
        this.imageByteArray = imageByteArray;
        this.latitude = latitude;
        this.longitude = longitude;
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

    @Override
    public String toString() {
        return "DueCollectionDetails{" + "firstName='" + firstName + '\'' + ", lastName='" + lastName + '\'' + ", workAddress='" + workAddress + '\'' + ", businessName='" + businessName + '\'' + ", dueCollectionDate='" + dueCollectionDate + '\'' + ", collectionNumber=" + collectionNumber + ", dueAmount=" + dueAmount + ", isDueCollected=" + isDueCollected + '}';
    }
}
