package com.icubed.loansticdroid.localdatabase;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class BorrowerFilesTable implements Parcelable {

    @Unique
    private String filesId;

    @Id(autoincrement = true)
    private Long id;

    private String fileImageUri, fileImageUriThumb, fileDescription, activityCycleId;
    private Date timestamp;

    @Generated(hash = 2060090756)
    public BorrowerFilesTable(String filesId, Long id, String fileImageUri,
            String fileImageUriThumb, String fileDescription, String activityCycleId,
            Date timestamp) {
        this.filesId = filesId;
        this.id = id;
        this.fileImageUri = fileImageUri;
        this.fileImageUriThumb = fileImageUriThumb;
        this.fileDescription = fileDescription;
        this.activityCycleId = activityCycleId;
        this.timestamp = timestamp;
    }

    @Generated(hash = 1402127370)
    public BorrowerFilesTable() {
    }

    public String getFilesId() {
        return this.filesId;
    }

    public void setFilesId(String filesId) {
        this.filesId = filesId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileImageUri() {
        return this.fileImageUri;
    }

    public void setFileImageUri(String fileImageUri) {
        this.fileImageUri = fileImageUri;
    }

    public String getFileImageUriThumb() {
        return this.fileImageUriThumb;
    }

    public void setFileImageUriThumb(String fileImageUriThumb) {
        this.fileImageUriThumb = fileImageUriThumb;
    }

    public String getFileDescription() {
        return this.fileDescription;
    }

    public void setFileDescription(String fileDescription) {
        this.fileDescription = fileDescription;
    }

    public String getActivityCycleId() {
        return this.activityCycleId;
    }

    public void setActivityCycleId(String activityCycleId) {
        this.activityCycleId = activityCycleId;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "BorrowerFilesTable{" + "filesId='" + filesId + '\'' + ", id=" + id + ", fileImageUri='" + fileImageUri + '\'' + ", fileImageUriThumb='" + fileImageUriThumb + '\'' + ", fileDescription='" + fileDescription + '\'' + ", activityCycleId='" + activityCycleId + '\'' + ", timestamp=" + timestamp + '}';
    }

    // Parcelling part
    public BorrowerFilesTable(Parcel in){
        String[] data = new String[5];

        in.readStringArray(data);
        this.fileDescription = data[0];
        this.fileImageUri = data[1];
        this.fileImageUriThumb = data[2];
        this.filesId = data[3];
        this.activityCycleId = data[4];

    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.fileDescription,
                this.fileImageUri,
                this.fileImageUriThumb,
                this.filesId, this.activityCycleId});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public BorrowerFilesTable createFromParcel(Parcel in) {
            return new BorrowerFilesTable(in);
        }

        public BorrowerFilesTable[] newArray(int size) {
            return new BorrowerFilesTable[size];
        }
    };
}
