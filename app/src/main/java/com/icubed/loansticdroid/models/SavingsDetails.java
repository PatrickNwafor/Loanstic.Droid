package com.icubed.loansticdroid.models;

import com.icubed.loansticdroid.localdatabase.BorrowersTable;
import com.icubed.loansticdroid.localdatabase.SavingsPlanCollectionTable;
import com.icubed.loansticdroid.localdatabase.SavingsPlanTypeTable;
import com.icubed.loansticdroid.localdatabase.SavingsTable;

public class SavingsDetails {

    private BorrowersTable borrowersTable;
    private SavingsTable savingsTable;
    private SavingsPlanTypeTable savingsPlanTypeTable;
    private SavingsPlanCollectionTable savingsPlanCollectionTable;

    public SavingsDetails() {
    }

    public SavingsDetails(BorrowersTable borrowersTable, SavingsTable savingsTable, SavingsPlanTypeTable savingsPlanTypeTable, SavingsPlanCollectionTable savingsPlanCollectionTable) {
        this.borrowersTable = borrowersTable;
        this.savingsTable = savingsTable;
        this.savingsPlanTypeTable = savingsPlanTypeTable;
        this.savingsPlanCollectionTable = savingsPlanCollectionTable;
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

    public SavingsPlanCollectionTable getSavingsPlanCollectionTable() {
        return savingsPlanCollectionTable;
    }

    public void setSavingsPlanCollectionTable(SavingsPlanCollectionTable savingsPlanCollectionTable) {
        this.savingsPlanCollectionTable = savingsPlanCollectionTable;
    }

    @Override
    public String toString() {
        return "SavingsDetails{" + "borrowersTable=" + borrowersTable + ", savingsTable=" + savingsTable + ", savingsPlanTypeTable=" + savingsPlanTypeTable + ", savingsPlanCollectionTable=" + savingsPlanCollectionTable + '}';
    }
}
