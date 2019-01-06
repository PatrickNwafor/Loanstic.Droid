package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class BorrowerPhotoValidationTable {

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
}
