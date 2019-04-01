package com.icubed.loansticdroid.localdatabase;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class TransactionPhotoValidationTable implements Parcelable {
    @Unique
    private String transactionPhotoValidationId;

    @Id(autoincrement = true)
    private Long id;

    private String transactionId, imageUri, imageUriThumb;
    private byte[] imageByteArray;
    private Date timestamp;

    // Parcelling part
    public TransactionPhotoValidationTable(Parcel in){
        this.transactionPhotoValidationId = in.readString();
        this.transactionId = in.readString();
        this.imageUri = in.readString();
        this.imageUriThumb = in.readString();
        this.timestamp = new Date(in.readLong());
    }

    @Generated(hash = 1352965019)
    public TransactionPhotoValidationTable(String transactionPhotoValidationId,
            Long id, String transactionId, String imageUri, String imageUriThumb,
            byte[] imageByteArray, Date timestamp) {
        this.transactionPhotoValidationId = transactionPhotoValidationId;
        this.id = id;
        this.transactionId = transactionId;
        this.imageUri = imageUri;
        this.imageUriThumb = imageUriThumb;
        this.imageByteArray = imageByteArray;
        this.timestamp = timestamp;
    }

    @Generated(hash = 352783100)
    public TransactionPhotoValidationTable() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.transactionPhotoValidationId);
        dest.writeString(this.transactionId);
        dest.writeString(this.imageUri);
        dest.writeString(this.imageUriThumb);
        dest.writeLong(this.timestamp.getTime());
    }

    public String getTransactionPhotoValidationId() {
        return this.transactionPhotoValidationId;
    }

    public void setTransactionPhotoValidationId(
            String transactionPhotoValidationId) {
        this.transactionPhotoValidationId = transactionPhotoValidationId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public TransactionPhotoValidationTable createFromParcel(Parcel in) {
            return new TransactionPhotoValidationTable(in);
        }

        public TransactionPhotoValidationTable[] newArray(int size) {
            return new TransactionPhotoValidationTable[size];
        }
    };
}
