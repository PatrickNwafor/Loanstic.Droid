package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class DepositPhotoValidationTable {

    @Unique
    private String depositPhotoValidationId;

    @Id(autoincrement = true)
    private Long id;

    private String depositId, imageUri, imageUriThumb;
    private byte[] imageByteArray;
    private Date timestamp;
    @Generated(hash = 1493510955)
    public DepositPhotoValidationTable(String depositPhotoValidationId, Long id,
            String depositId, String imageUri, String imageUriThumb,
            byte[] imageByteArray, Date timestamp) {
        this.depositPhotoValidationId = depositPhotoValidationId;
        this.id = id;
        this.depositId = depositId;
        this.imageUri = imageUri;
        this.imageUriThumb = imageUriThumb;
        this.imageByteArray = imageByteArray;
        this.timestamp = timestamp;
    }
    @Generated(hash = 35915159)
    public DepositPhotoValidationTable() {
    }
    public String getDepositPhotoValidationId() {
        return this.depositPhotoValidationId;
    }
    public void setDepositPhotoValidationId(String depositPhotoValidationId) {
        this.depositPhotoValidationId = depositPhotoValidationId;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDepositId() {
        return this.depositId;
    }
    public void setDepositId(String depositId) {
        this.depositId = depositId;
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
