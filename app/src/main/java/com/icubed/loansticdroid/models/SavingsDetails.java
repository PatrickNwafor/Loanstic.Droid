package com.icubed.loansticdroid.models;

import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.SavingsPlanTypeTable;
import com.icubed.loansticdroid.localdatabase.SavingsTable;

public class SavingsDetails {

    private BorrowersTable borrowersTable;
    private SavingsTable savingsTable;
    private SavingsPlanTypeTable savingsPlanTypeTable;

    public SavingsDetails() {
    }

    public SavingsDetails(BorrowersTable borrowersTable, SavingsTable savingsTable, SavingsPlanTypeTable savingsPlanTypeTable) {
        this.borrowersTable = borrowersTable;
        this.savingsTable = savingsTable;
        this.savingsPlanTypeTable = savingsPlanTypeTable;
    }

    public SavingsPlanTypeTable getSavingsPlanTypeTable() {
        return savingsPlanTypeTable;
    }

    public void setSavingsPlanTypeTable(SavingsPlanTypeTable savingsPlanTypeTable) {
        this.savingsPlanTypeTable = savingsPlanTypeTable;
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
