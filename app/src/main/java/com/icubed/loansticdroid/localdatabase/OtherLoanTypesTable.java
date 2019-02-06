package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class OtherLoanTypesTable{

    @Unique
    private String otherLoanTypeId;

    private String branchId;

    @Id(autoincrement = true)
    private Long Id;

    private String otherloanTypeName;
    private String otherloanTypeDescription;

    private String otherloanTypeImageUri;
    private String otherloanTypeImageThumbUri;
    private Date timestamp;
    private byte[] otherloanTypeImageByteArray;
    @Generated(hash = 688702785)
    public OtherLoanTypesTable(String otherLoanTypeId, String branchId, Long Id,
            String otherloanTypeName, String otherloanTypeDescription,
            String otherloanTypeImageUri, String otherloanTypeImageThumbUri,
            Date timestamp, byte[] otherloanTypeImageByteArray) {
        this.otherLoanTypeId = otherLoanTypeId;
        this.branchId = branchId;
        this.Id = Id;
        this.otherloanTypeName = otherloanTypeName;
        this.otherloanTypeDescription = otherloanTypeDescription;
        this.otherloanTypeImageUri = otherloanTypeImageUri;
        this.otherloanTypeImageThumbUri = otherloanTypeImageThumbUri;
        this.timestamp = timestamp;
        this.otherloanTypeImageByteArray = otherloanTypeImageByteArray;
    }
    @Generated(hash = 1150651568)
    public OtherLoanTypesTable() {
    }
    public String getOtherLoanTypeId() {
        return this.otherLoanTypeId;
    }
    public void setOtherLoanTypeId(String otherLoanTypeId) {
        this.otherLoanTypeId = otherLoanTypeId;
    }
    public String getBranchId() {
        return this.branchId;
    }
    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }
    public Long getId() {
        return this.Id;
    }
    public void setId(Long Id) {
        this.Id = Id;
    }
    public String getOtherloanTypeName() {
        return this.otherloanTypeName;
    }
    public void setOtherloanTypeName(String otherloanTypeName) {
        this.otherloanTypeName = otherloanTypeName;
    }
    public String getOtherloanTypeDescription() {
        return this.otherloanTypeDescription;
    }
    public void setOtherloanTypeDescription(String otherloanTypeDescription) {
        this.otherloanTypeDescription = otherloanTypeDescription;
    }
    public String getOtherloanTypeImageUri() {
        return this.otherloanTypeImageUri;
    }
    public void setOtherloanTypeImageUri(String otherloanTypeImageUri) {
        this.otherloanTypeImageUri = otherloanTypeImageUri;
    }
    public String getOtherloanTypeImageThumbUri() {
        return this.otherloanTypeImageThumbUri;
    }
    public void setOtherloanTypeImageThumbUri(String otherloanTypeImageThumbUri) {
        this.otherloanTypeImageThumbUri = otherloanTypeImageThumbUri;
    }
    public Date getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    public byte[] getOtherloanTypeImageByteArray() {
        return this.otherloanTypeImageByteArray;
    }
    public void setOtherloanTypeImageByteArray(byte[] otherloanTypeImageByteArray) {
        this.otherloanTypeImageByteArray = otherloanTypeImageByteArray;
    }
}
