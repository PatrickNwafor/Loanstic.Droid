package com.icubed.loansticdroid.localdatabase;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Arrays;
import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class PaymentPhotoVerificationTable implements Parcelable {

    @Unique
    private String paymentVerificationPhotoId;

    @Id(autoincrement = true)
    private Long id;

    private String paymentId, imageUri, imageUriThumb;
    private byte[] imageByteArray;
    private Date timestamp;
    @Generated(hash = 1965794865)
    public PaymentPhotoVerificationTable(String paymentVerificationPhotoId, Long id,
            String paymentId, String imageUri, String imageUriThumb,
            byte[] imageByteArray, Date timestamp) {
        this.paymentVerificationPhotoId = paymentVerificationPhotoId;
        this.id = id;
        this.paymentId = paymentId;
        this.imageUri = imageUri;
        this.imageUriThumb = imageUriThumb;
        this.imageByteArray = imageByteArray;
        this.timestamp = timestamp;
    }
    @Generated(hash = 1125316040)
    public PaymentPhotoVerificationTable() {
    }
    public String getPaymentVerificationPhotoId() {
        return this.paymentVerificationPhotoId;
    }
    public void setPaymentVerificationPhotoId(String paymentVerificationPhotoId) {
        this.paymentVerificationPhotoId = paymentVerificationPhotoId;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPaymentId() {
        return this.paymentId;
    }
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
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

    @Override
    public String toString() {
        return "PaymentPhotoVerificationTable{" + "paymentVerificationPhotoId='" + paymentVerificationPhotoId + '\'' + ", id=" + id + ", paymentId='" + paymentId + '\'' + ", imageUri='" + imageUri + '\'' + ", imageUriThumb='" + imageUriThumb + '\'' + ", imageByteArray=" + Arrays.toString(imageByteArray) + ", timestamp=" + timestamp + '}';
    }

    // Parcelling part
    public PaymentPhotoVerificationTable(Parcel in){
        this.paymentVerificationPhotoId = in.readString();
        this.paymentId = in.readString();
        this.imageUri = in.readString();
        this.imageUriThumb = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.paymentVerificationPhotoId);
        dest.writeString(this.paymentId);
        dest.writeString(this.imageUri);
        dest.writeString(this.imageUriThumb);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PaymentPhotoVerificationTable createFromParcel(Parcel in) {
            return new PaymentPhotoVerificationTable(in);
        }

        public PaymentPhotoVerificationTable[] newArray(int size) {
            return new PaymentPhotoVerificationTable[size];
        }
    };
}
