package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class BorrowerFilesTable {

    @Unique
    private String filesId;

    @Id(autoincrement = true)
    private Long id;

    private String fileImageUri, fileImageUriThumb, fileDescription;
    private Date timesstamp;
    @Generated(hash = 741742471)
    public BorrowerFilesTable(String filesId, Long id, String fileImageUri,
            String fileImageUriThumb, String fileDescription, Date timesstamp) {
        this.filesId = filesId;
        this.id = id;
        this.fileImageUri = fileImageUri;
        this.fileImageUriThumb = fileImageUriThumb;
        this.fileDescription = fileDescription;
        this.timesstamp = timesstamp;
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
    public Date getTimesstamp() {
        return this.timesstamp;
    }
    public void setTimesstamp(Date timesstamp) {
        this.timesstamp = timesstamp;
    }
}
