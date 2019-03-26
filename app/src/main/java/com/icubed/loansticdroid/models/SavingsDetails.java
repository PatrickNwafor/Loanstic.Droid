package com.icubed.loansticdroid.models;

import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.SavingsTable;

public class SavingsDetails {

    private BorrowersTable borrowersTable;
    private SavingsTable savingsTable;

    public SavingsDetails() {
    }

    public SavingsDetails(BorrowersTable borrowersTable, SavingsTable savingsTable) {
        this.borrowersTable = borrowersTable;
        this.savingsTable = savingsTable;
    }

    public BorrowersTable getBorrowersTable() {
        return borrowersTable;
    }

    public void setBorrowersTable(BorrowersTable borrowersTable) {
        this.borrowersTable = borrowersTable;
    }

    public SavingsTable getSavingsTable() {
        return savingsTable;
    }

    public void setSavingsTable(SavingsTable savingsTable) {
        this.savingsTable = savingsTable;
    }

    @Override
    public String toString() {
        return "SavingsDetails{" + "borrowersTable=" + borrowersTable + ", savingsTable=" + savingsTable + '}';
    }
}
