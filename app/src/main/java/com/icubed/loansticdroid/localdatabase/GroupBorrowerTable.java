package com.icubed.loansticdroid.localdatabase;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class GroupBorrowerTable implements Parcelable {

    @Unique
    private String groupId;

    @Id(autoincrement = true)
    private Long id;
    private String groupName;
    private String groupLeaderId;
    private String loanOfficerId;
    private int numberOfGroupMembers;
    private Boolean isGroupApproved;
    private String approvedBy;
    private String assignedBy;
    private Date lastUpdatedAt;
    private String meetingLocation;
    private double groupLocationLatitude, groupLocationLongitude;
    private Date timestamp;
    @Generated(hash = 921088956)
    public GroupBorrowerTable(String groupId, Long id, String groupName, String groupLeaderId, String loanOfficerId, int numberOfGroupMembers, Boolean isGroupApproved, String approvedBy, String assignedBy, Date lastUpdatedAt, String meetingLocation, double groupLocationLatitude, double groupLocationLongitude, Date timestamp) {
        this.groupId = groupId;
        this.id = id;
        this.groupName = groupName;
        this.groupLeaderId = groupLeaderId;
        this.loanOfficerId = loanOfficerId;
        this.numberOfGroupMembers = numberOfGroupMembers;
        this.isGroupApproved = isGroupApproved;
        this.approvedBy = approvedBy;
        this.assignedBy = assignedBy;
        this.lastUpdatedAt = lastUpdatedAt;
        this.meetingLocation = meetingLocation;
        this.groupLocationLatitude = groupLocationLatitude;
        this.groupLocationLongitude = groupLocationLongitude;
        this.timestamp = timestamp;
    }
    @Generated(hash = 232308116)
    public GroupBorrowerTable() {
    }
    public String getGroupId() {
        return this.groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getGroupName() {
        return this.groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public String getGroupLeaderId() {
        return this.groupLeaderId;
    }
    public void setGroupLeaderId(String groupLeaderId) {
        this.groupLeaderId = groupLeaderId;
    }
    public String getLoanOfficerId() {
        return this.loanOfficerId;
    }
    public void setLoanOfficerId(String loanOfficerId) {
        this.loanOfficerId = loanOfficerId;
    }
    public int getNumberOfGroupMembers() {
        return this.numberOfGroupMembers;
    }
    public void setNumberOfGroupMembers(int numberOfGroupMembers) {
        this.numberOfGroupMembers = numberOfGroupMembers;
    }
    public Boolean getIsGroupApproved() {
        return this.isGroupApproved;
    }
    public void setIsGroupApproved(Boolean isGroupApproved) {
        this.isGroupApproved = isGroupApproved;
    }
    public String getApprovedBy() {
        return this.approvedBy;
    }
    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }
    public String getAssignedBy() {
        return this.assignedBy;
    }
    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }
    public String getMeetingLocation() {
        return this.meetingLocation;
    }
    public void setMeetingLocation(String meetingLocation) {
        this.meetingLocation = meetingLocation;
    }
    public double getGroupLocationLatitude() {
        return this.groupLocationLatitude;
    }
    public void setGroupLocationLatitude(double groupLocationLatitude) {
        this.groupLocationLatitude = groupLocationLatitude;
    }
    public double getGroupLocationLongitude() {
        return this.groupLocationLongitude;
    }
    public void setGroupLocationLongitude(double groupLocationLongitude) {
        this.groupLocationLongitude = groupLocationLongitude;
    }
    public Date getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "GroupBorrowerTable{" + "groupId='" + groupId + '\'' + ", id=" + id + ", groupName='" + groupName + '\'' + ", groupLeaderId='" + groupLeaderId + '\'' + ", loanOfficerId='" + loanOfficerId + '\'' + ", numberOfGroupMembers=" + numberOfGroupMembers + ", isGroupApproved=" + isGroupApproved + ", approvedBy='" + approvedBy + '\'' + ", assignedBy='" + assignedBy + '\'' + ", meetingLocation='" + meetingLocation + '\'' + ", groupLocationLatitude=" + groupLocationLatitude + ", groupLocationLongitude=" + groupLocationLongitude + ", timestamp=" + timestamp + '}';
    }

    // Parcelling part
    public GroupBorrowerTable(Parcel in){
        String[] data = new String[12];

        in.readStringArray(data);
        this.groupId = data[0];
        this.loanOfficerId = data[1];
        this.id = Long.valueOf(data[2]);
        this.groupName = data[3];
        this.groupLeaderId = data[4];
        this.numberOfGroupMembers = Integer.parseInt(data[5]);
        this.assignedBy = data[6];
        this.isGroupApproved = Boolean.valueOf(data[7]);
        this.approvedBy = data[8];
        this.meetingLocation = data[9];
        this.groupLocationLatitude = Double.parseDouble(data[10]);
        this.groupLocationLongitude = Double.parseDouble(data[11]);

    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.groupId,
                this.loanOfficerId, String.valueOf(this.id),
                this.groupName,
                this.groupLeaderId, String.valueOf(this.numberOfGroupMembers),
                this.assignedBy, String.valueOf(this.isGroupApproved),
                this.approvedBy,
                this.meetingLocation, String.valueOf(this.groupLocationLatitude), String.valueOf(this.groupLocationLongitude)});
    }
    public Date getLastUpdatedAt() {
        return this.lastUpdatedAt;
    }
    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public GroupBorrowerTable createFromParcel(Parcel in) {
            return new GroupBorrowerTable(in);
        }

        public GroupBorrowerTable[] newArray(int size) {
            return new GroupBorrowerTable[size];
        }
    };
}
