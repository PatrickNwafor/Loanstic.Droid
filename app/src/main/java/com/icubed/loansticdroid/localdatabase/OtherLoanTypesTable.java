package com.icubed.loansticdroid.localdatabase;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class OtherLoanTypesTable implements Parcelable {

    @Unique
    private String otherLoanTypeId;

    private String branchId;

    @Id(autoincrement = true)
    private Long Id;

    private String otherLoanTypeName;
    private String otherLoanTypeDescription;
    private Date lastUpdatedAt;

    private Date timestamp;

    @Generated(hash = 1138701537)
    public OtherLoanTypesTable(String otherLoanTypeId, String branchId, Long Id,
            String otherLoanTypeName, String otherLoanTypeDescription,
            Date lastUpdatedAt, Date timestamp) {
        this.otherLoanTypeId = otherLoanTypeId;
        this.branchId = branchId;
        this.Id = Id;
        this.otherLoanTypeName = otherLoanTypeName;
        this.otherLoanTypeDescription = otherLoanTypeDescription;
        this.lastUpdatedAt = lastUpdatedAt;
        this.timestamp = timestamp;
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

    public String getOtherLoanTypeName() {
        return this.otherLoanTypeName;
    }

    public void setOtherLoanTypeName(String otherLoanTypeName) {
        this.otherLoanTypeName = otherLoanTypeName;
    }

    public String getOtherLoanTypeDescription() {
        return this.otherLoanTypeDescription;
    }

    public void setOtherLoanTypeDescription(String otherLoanTypeDescription) {
        this.otherLoanTypeDescription = otherLoanTypeDescription;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getLastUpdatedAt() {
        return this.lastUpdatedAt;
    }

    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    @Override
    public String toString() {
        return "OtherLoanTypesTable{" + "otherLoanTypeId='" + otherLoanTypeId + '\'' + ", branchId='" + branchId + '\'' + ", Id=" + Id + ", otherLoanTypeName='" + otherLoanTypeName + '\'' + ", otherLoanTypeDescription='" + otherLoanTypeDescription + '\'' + ", lastUpdatedAt=" + lastUpdatedAt + ", timestamp=" + timestamp + '}';
    }


    // Parcelling part
    public OtherLoanTypesTable(Parcel in){
        String[] data = new String[4];

        in.readStringArray(data);
        this.otherLoanTypeId = data[0];
        this.otherLoanTypeName = data[1];
        this.otherLoanTypeDescription = data[2];
        this.branchId = data[3];
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.otherLoanTypeId,
                this.otherLoanTypeName,
                this.otherLoanTypeDescription,
                this.branchId});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public OtherLoanTypesTable createFromParcel(Parcel in) {
            return new OtherLoanTypesTable(in);
        }

        public OtherLoanTypesTable[] newArray(int size) {
            return new OtherLoanTypesTable[size];
        }
    };
}
