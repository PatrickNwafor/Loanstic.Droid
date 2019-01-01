package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class LoansTable {

    @Unique
    private String loanId;

    private String borrowerId;
    @Id(autoincrement = true)
    private Long id;
    @Generated(hash = 748239530)
    public LoansTable(String loanId, String borrowerId, Long id) {
        this.loanId = loanId;
        this.borrowerId = borrowerId;
        this.id = id;
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
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
}
