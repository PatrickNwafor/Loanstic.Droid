package com.icubed.loansticdroid.notification;

import java.util.Date;

public class LoanRequestNotificationTable {
    private String loanId;
    private Date timestamp;

    public LoanRequestNotificationTable() {
    }

    public LoanRequestNotificationTable(String loanId, Date timestamp) {
        this.loanId = loanId;
        this.timestamp = timestamp;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
