package com.icubed.loansticdroid.localdatabase;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class WithdrawalPhotoValidationTable implements Parcelable {
    @Unique
    private String withdawalPhotoValidationId;

    @Id(autoincrement = true)
    private Long id;

    private String withdrawalId, imageUri, imageUriThumb;
    private byte[] imageByteArray;
    private Date timestamp;
    @Generated(hash = 1278314806)
    public WithdrawalPhotoValidationTable(String withdawalPhotoValidationId,
            Long id, String withdrawalId, String imageUri, String imageUriThumb,
            byte[] imageByteArray, Date timestamp) {
        this.withdawalPhotoValidationId = withdawalPhotoValidationId;
        this.id = id;
        this.withdrawalId = withdrawalId;
        this.imageUri = imageUri;
        this.imageUriThumb = imageUriThumb;
        this.imageByteArray = imageByteArray;
        this.timestamp = timestamp;
    }
    @Generated(hash = 1876525132)
    public WithdrawalPhotoValidationTable() {
    }
    public String getWithdawalPhotoValidationId() {
        return this.withdawalPhotoValidationId;
    }
    public void setWithdawalPhotoValidationId(String withdawalPhotoValidationId) {
        this.withdawalPhotoValidationId = withdawalPhotoValidationId;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getWithdrawalId() {
        return this.withdrawalId;
    }
    public void setWithdrawalId(String withdrawalId) {
        this.withdrawalId = withdrawalId;
    }
    public String getImageUri() {
        return this.imageUri;
    }
    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
    public String getImageUriThumb() {
        return this.imageUriThumb;
    }
    public void setImageUriThumb(String imageUriThumb) {
        this.imageUriThumb = imageUriThumb;
    }
    public byte[] getImageByteArray() {
        return this.imageByteArray;
    }
    public void setImageByteArray(byte[] imageByteArray) {
        this.imageByteArray = imageByteArray;
    }
    public Date getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    // Parcelling part
    public WithdrawalPhotoValidationTable(Parcel in){
        this.withdawalPhotoValidationId = in.readString();
        this.withdrawalId = in.readString();
        this.imageUri = in.readString();
        this.imageUriThumb = in.readString();
        this.timestamp = new Date(in.readLong());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.withdawalPhotoValidationId);
        dest.writeString(this.withdrawalId);
        dest.writeString(this.imageUri);
        dest.writeString(this.imageUriThumb);
        dest.writeLong(this.timestamp.getTime());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public WithdrawalPhotoValidationTable createFromParcel(Parcel in) {
            return new WithdrawalPhotoValidationTable(in);
        }

        public WithdrawalPhotoValidationTable[] newArray(int size) {
            return new WithdrawalPhotoValidationTable[size];
        }
    };
}
