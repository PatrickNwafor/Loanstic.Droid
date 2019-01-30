package com.icubed.loansticdroid.notification;

import java.util.Date;

public class BorrowerPendingApprovalNotificationTable {

    private String borrowerId;
    private Date timestamp;

    public BorrowerPendingApprovalNotificationTable(){}

    public BorrowerPendingApprovalNotificationTable(String borrowerId, Date timestamp) {
        this.borrowerId = borrowerId;
        this.timestamp = timestamp;
    }

    public String getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
