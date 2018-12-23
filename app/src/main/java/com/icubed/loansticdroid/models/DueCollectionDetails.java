package com.icubed.loansticdroid.models;

public class DueCollectionDetails {

    private String firstName, lastName, workAddress, businessName, dueCollectionDate;
    private int collectionNumber;
    private double dueAmount;
    private Boolean isDueCollected;

    public DueCollectionDetails(){}

    public DueCollectionDetails(String firstName, String lastName, String workAddress, String businessName, String dueCollectionDate, int collectionNumber, double dueAmount, Boolean isDueCollected) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.workAddress = workAddress;
        this.businessName = businessName;
        this.dueCollectionDate = dueCollectionDate;
        this.collectionNumber = collectionNumber;
        this.dueAmount = dueAmount;
        this.isDueCollected = isDueCollected;
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
