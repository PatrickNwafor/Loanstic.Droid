package com.icubed.loansticdroid.localdatabase;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class GroupPhotoValidationTable implements Parcelable {

    @Unique
    private String groupPhotoValidationId;

    private String groupId, photoUri, photoThumbUri;
    private double photoLatitude, photoLongitude;

    private byte[] imageByteArray;

    @Id(autoincrement = true)
    private Long id;
    private Date timestamp;
    @Generated(hash = 1325425027)
    public GroupPhotoValidationTable(String groupPhotoValidationId, String groupId, String photoUri,
            String photoThumbUri, double photoLatitude, double photoLongitude, byte[] imageByteArray, Long id,
            Date timestamp) {
        this.groupPhotoValidationId = groupPhotoValidationId;
        this.groupId = groupId;
        this.photoUri = photoUri;
        this.photoThumbUri = photoThumbUri;
        this.photoLatitude = photoLatitude;
        this.photoLongitude = photoLongitude;
        this.imageByteArray = imageByteArray;
        this.id = id;
        this.timestamp = timestamp;
    }
    @Generated(hash = 392511556)
    public GroupPhotoValidationTable() {
    }
    public String getGroupPhotoValidationId() {
        return this.groupPhotoValidationId;
    }
    public void setGroupPhotoValidationId(String groupPhotoValidationId) {
        this.groupPhotoValidationId = groupPhotoValidationId;
    }
    public String getGroupId() {
        return this.groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
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
    public GroupPhotoValidationTable(Parcel in){
        String[] data = new String[6];

        in.readStringArray(data);
        this.groupPhotoValidationId = data[0];
        this.groupId = data[1];
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
        dest.writeStringArray(new String[] {this.groupPhotoValidationId,
                this.groupId, this.photoUri,
                this.photoThumbUri, String.valueOf(this.photoLatitude), String.valueOf(this.photoLongitude)});
    }
    public byte[] getImageByteArray() {
        return this.imageByteArray;
    }
    public void setImageByteArray(byte[] imageByteArray) {
        this.imageByteArray = imageByteArray;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public GroupPhotoValidationTable createFromParcel(Parcel in) {
            return new GroupPhotoValidationTable(in);
        }

        public GroupPhotoValidationTable[] newArray(int size) {
            return new GroupPhotoValidationTable[size];
        }
    };
}
