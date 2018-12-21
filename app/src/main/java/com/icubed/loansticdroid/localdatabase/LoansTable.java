package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class LoansTable {

    @Unique
    private String loanId;

    private String borrowerId;

    @Generated(hash = 1922571217)
    public LoansTable(String loanId, String borrowerId) {
        this.loanId = loanId;
        this.borrowerId = borrowerId;
    }

    @Generated(hash = 1106604234)
    public LoansTable() {
    }

    public String getLoanId() {
        return this.loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getBorrowerId() {
        return this.borrowerId;
    }

    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }

}
