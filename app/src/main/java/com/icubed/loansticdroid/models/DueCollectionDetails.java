package com.icubed.loansticdroid.models;

public class DueCollectionDetails {

    private String borrowerName, borrowerJob, dueCollectionDate;
    private int collectionNumber;
    private double dueAmount;
    private Boolean isDueCollected;

    public DueCollectionDetails(){}

    public DueCollectionDetails(String borrowerName, String borrowerJob, String dueCollectionDate, int collectionNumber, double dueAmount, Boolean isDueCollected) {
        this.borrowerName = borrowerName;
        this.borrowerJob = borrowerJob;
        this.dueCollectionDate = dueCollectionDate;
        this.collectionNumber = collectionNumber;
        this.dueAmount = dueAmount;
        this.isDueCollected = isDueCollected;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public String getBorrowerJob() {
        return borrowerJob;
    }

    public void setBorrowerJob(String borrowerJob) {
        this.borrowerJob = borrowerJob;
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

    public Boolean getDueCollected() {
        return isDueCollected;
    }

    public void setDueCollected(Boolean dueCollected) {
        isDueCollected = dueCollected;
    }

    @Override
    public String toString() {
        return "DueCollectionDetails{" + "borrowerName='" + borrowerName + '\'' + ", borrowerJob='" + borrowerJob + '\'' + ", dueCollectionDate='" + dueCollectionDate + '\'' + ", collectionNumber=" + collectionNumber + ", dueAmount=" + dueAmount + ", isDueCollected=" + isDueCollected + '}';
    }
}
