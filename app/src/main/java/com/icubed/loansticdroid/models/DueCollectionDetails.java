package com.icubed.loansticdroid.models;

public class DueCollectionDetails {

    private String borrowerName, borrowerJob;
    private double dueAmount;
    private Boolean isDueCollected;

    public DueCollectionDetails(){}

    public DueCollectionDetails(String borrowerName, String borrowerJob, double dueAmount, Boolean isDueCollected) {
        this.borrowerName = borrowerName;
        this.borrowerJob = borrowerJob;
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
        return "DueCollectionDetails{" + "borrowerName='" + borrowerName + '\'' + ", borrowerJob='" + borrowerJob + '\'' + ", dueAmount=" + dueAmount + ", isDueCollected=" + isDueCollected + '}';
    }
}
