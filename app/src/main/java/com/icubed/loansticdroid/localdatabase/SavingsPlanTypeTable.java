package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SavingsPlanTypeTable {

    @Unique
    private String savingsPlanTypeId;

    @Id(autoincrement = true)
    private Long Id;

    private String savingsTypeName;
    private Date lastUpdatedAt;
    private String savingsTypeDescription;

    private String savingsTypeImageUri, savingsTypeAbbreviation;
    private String savingsTypeImageThumbUri;
    private Date timestamp;
    private byte[] loanTypeImageByteArray;
    @Generated(hash = 1935641401)
    public SavingsPlanTypeTable(String savingsPlanTypeId, Long Id,
            String savingsTypeName, Date lastUpdatedAt,
            String savingsTypeDescription, String savingsTypeImageUri,
            String savingsTypeAbbreviation, String savingsTypeImageThumbUri,
            Date timestamp, byte[] loanTypeImageByteArray) {
        this.savingsPlanTypeId = savingsPlanTypeId;
        this.Id = Id;
        this.savingsTypeName = savingsTypeName;
        this.lastUpdatedAt = lastUpdatedAt;
        this.savingsTypeDescription = savingsTypeDescription;
        this.savingsTypeImageUri = savingsTypeImageUri;
        this.savingsTypeAbbreviation = savingsTypeAbbreviation;
        this.savingsTypeImageThumbUri = savingsTypeImageThumbUri;
        this.timestamp = timestamp;
        this.loanTypeImageByteArray = loanTypeImageByteArray;
    }
    @Generated(hash = 1278357353)
    public SavingsPlanTypeTable() {
    }
    public String getSavingsPlanTypeId() {
        return this.savingsPlanTypeId;
    }
    public void setSavingsPlanTypeId(String savingsPlanTypeId) {
        this.savingsPlanTypeId = savingsPlanTypeId;
    }
    public Long getId() {
        return this.Id;
    }
    public void setId(Long Id) {
        this.Id = Id;
    }
    public String getSavingsTypeName() {
        return this.savingsTypeName;
    }
    public void setSavingsTypeName(String savingsTypeName) {
        this.savingsTypeName = savingsTypeName;
    }
    public Date getLastUpdatedAt() {
        return this.lastUpdatedAt;
    }
    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
    public String getSavingsTypeDescription() {
        return this.savingsTypeDescription;
    }
    public void setSavingsTypeDescription(String savingsTypeDescription) {
        this.savingsTypeDescription = savingsTypeDescription;
    }
    public String getSavingsTypeImageUri() {
        return this.savingsTypeImageUri;
    }
    public void setSavingsTypeImageUri(String savingsTypeImageUri) {
        this.savingsTypeImageUri = savingsTypeImageUri;
    }
    public String getSavingsTypeAbbreviation() {
        return this.savingsTypeAbbreviation;
    }
    public void setSavingsTypeAbbreviation(String savingsTypeAbbreviation) {
        this.savingsTypeAbbreviation = savingsTypeAbbreviation;
    }
    public String getSavingsTypeImageThumbUri() {
        return this.savingsTypeImageThumbUri;
    }
    public void setSavingsTypeImageThumbUri(String savingsTypeImageThumbUri) {
        this.savingsTypeImageThumbUri = savingsTypeImageThumbUri;
    }
    public Date getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    public byte[] getLoanTypeImageByteArray() {
        return this.loanTypeImageByteArray;
    }
    public void setLoanTypeImageByteArray(byte[] loanTypeImageByteArray) {
        this.loanTypeImageByteArray = loanTypeImageByteArray;
    }

}
