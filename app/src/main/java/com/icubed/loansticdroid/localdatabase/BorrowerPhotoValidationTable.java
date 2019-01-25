package com.icubed.loansticdroid.localdatabase;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class BorrowerPhotoValidationTable implements Parcelable {

    @Unique
    private String borrowerPhotoValidationId;

    private String borrowerId, photoUri, photoThumbUri;
    private double photoLatitude, photoLongitude;

    @Id(autoincrement = true)
    private Long id;
    private Date timestamp;
    @Generated(hash = 1038683510)
    public BorrowerPhotoValidationTable(String borrowerPhotoValidationId,
            String borrowerId, String photoUri, String photoThumbUri,
            double photoLatitude, double photoLongitude, Long id, Date timestamp) {
        this.borrowerPhotoValidationId = borrowerPhotoValidationId;
        this.borrowerId = borrowerId;
        this.photoUri = photoUri;
        this.photoThumbUri = photoThumbUri;
        this.photoLatitude = photoLatitude;
        this.photoLongitude = photoLongitude;
        this.id = id;
        this.timestamp = timestamp;
    }
    @Generated(hash = 1299040209)
    public BorrowerPhotoValidationTable() {
    }
    public String getBorrowerPhotoValidationId() {
        return this.borrowerPhotoValidationId;
    }
    public void setBorrowerPhotoValidationId(String borrowerPhotoValidationId) {
        this.borrowerPhotoValidationId = borrowerPhotoValidationId;
    }
    public String getBorrowerId() {
        return this.borrowerId;
    }
    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }
    public String getPhotoUri() {
        return this.photoUri;
    }
    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
    public String getPhotoThumbUri() {
        return this.photoThumbUri;
    }
    public void setPhotoThumbUri(String photoThumbUri) {
        this.photoThumbUri = photoThumbUri;
    }
    public double getPhotoLatitude() {
        return this.photoLatitude;
    }
    public void setPhotoLatitude(double photoLatitude) {
        this.photoLatitude = photoLatitude;
    }
    public double getPhotoLongitude() {
        return this.photoLongitude;
    }
    public void setPhotoLongitude(double photoLongitude) {
        this.photoLongitude = photoLongitude;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Date getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    // Parcelling part
    public BorrowerPhotoValidationTable(Parcel in){
        String[] data = new String[6];

        in.readStringArray(data);
        this.borrowerPhotoValidationId = data[0];
        this.borrowerId = data[1];
        this.photoUri = data[2];
        this.photoThumbUri = data[3];
        this.photoLatitude = Double.parseDouble(data[4]);
        this.photoLongitude = Double.parseDouble(data[5]);

    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.borrowerPhotoValidationId,
                this.borrowerId, this.photoUri,
                this.photoThumbUri, String.valueOf(this.photoLatitude), String.valueOf(this.photoLongitude)});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public BorrowerPhotoValidationTable createFromParcel(Parcel in) {
            return new BorrowerPhotoValidationTable(in);
        }

        public BorrowerPhotoValidationTable[] newArray(int size) {
            return new BorrowerPhotoValidationTable[size];
        }
    };
}
