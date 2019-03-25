package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class WithdrawalPhotoValidationTable {
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
}
