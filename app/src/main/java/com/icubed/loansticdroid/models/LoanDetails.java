package com.icubed.loansticdroid.models;

import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.GroupBorrowerTable;
import com.icubed.loansticdroid.localdatabase.LoanTypeTable;
import com.icubed.loansticdroid.localdatabase.LoansTable;
import com.icubed.loansticdroid.localdatabase.OtherLoanTypesTable;

public class LoanDetails {
    private LoansTable loansTable;
    private BorrowersTable borrowersTable;
    private LoanTypeTable loanTypeTable;
    private OtherLoanTypesTable otherLoanTypesTable;
    private GroupBorrowerTable groupBorrowerTable;

    public LoanDetails() {
    }

    public LoanDetails(LoansTable loansTable, BorrowersTable borrowersTable, LoanTypeTable loanTypeTable, OtherLoanTypesTable otherLoanTypesTable, GroupBorrowerTable groupBorrowerTable) {
        this.loansTable = loansTable;
        this.borrowersTable = borrowersTable;
        this.loanTypeTable = loanTypeTable;
        this.otherLoanTypesTable = otherLoanTypesTable;
        this.groupBorrowerTable = groupBorrowerTable;
    }

    public LoansTable getLoansTable() {
        return loansTable;
    }

    public void setLoansTable(LoansTable loansTable) {
        this.loansTable = loansTable;
    }

    public BorrowersTable getBorrowersTable() {
        return borrowersTable;
    }

    public void setBorrowersTable(BorrowersTable borrowersTable) {
        this.borrowersTable = borrowersTable;
    }

    public LoanTypeTable getLoanTypeTable() {
        return loanTypeTable;
    }

    public void setLoanTypeTable(LoanTypeTable loanTypeTable) {
        this.loanTypeTable = loanTypeTable;
    }

    public OtherLoanTypesTable getOtherLoanTypesTable() {
        return otherLoanTypesTable;
    }

    public void setOtherLoanTypesTable(OtherLoanTypesTable otherLoanTypesTable) {
        this.otherLoanTypesTable = otherLoanTypesTable;
    }

    public GroupBorrowerTable getGroupBorrowerTable() {
        return groupBorrowerTable;
    }

    public void setGroupBorrowerTable(GroupBorrowerTable groupBorrowerTable) {
        this.groupBorrowerTable = groupBorrowerTable;
    }

    @Override
    public String toString() {
        return "LoanDetails{" + "loansTable=" + loansTable + ", borrowersTable=" + borrowersTable + ", loanTypeTable=" + loanTypeTable + ", otherLoanTypesTable=" + otherLoanTypesTable + ", groupBorrowerTable=" + groupBorrowerTable + '}';
    }
}
