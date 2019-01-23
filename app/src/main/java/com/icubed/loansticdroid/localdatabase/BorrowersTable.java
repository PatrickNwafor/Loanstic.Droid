package com.icubed.loansticdroid.localdatabase;

import android.os.Parcel;
import android.os.Parcelable;

import com.icubed.loansticdroid.models.SelectedBorrowerForGroup;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

import java.util.ArrayList;
import java.util.Date;

@Entity
public class BorrowersTable implements Parcelable {

    @Unique
    private String borrowersId;

    private String loanOfficerId, firstName, middleName, lastName, businessName, assignedBy
            ,profileImageUri, profileImageThumbUri, nationality, workAddress, sex
            ,homeAddress, state, city, dateOfBirth, email, businessDescription, photovalidationId;

    private Date timestamp;
    private Long phoneNumber, zipcode;
    private boolean belongsToGroup, isBorrowerActive;

    @Id(autoincrement = true)
    private Long id;

    private double borrowerLocationLatitude, borrowerLocationLongitude;

    @Generated(hash = 606290059)
    public BorrowersTable(String borrowersId, String loanOfficerId, String firstName, String middleName,
            String lastName, String businessName, String assignedBy, String profileImageUri,
            String profileImageThumbUri, String nationality, String workAddress, String sex, String homeAddress,
            String state, String city, String dateOfBirth, String email, String businessDescription,
            String photovalidationId, Date timestamp, Long phoneNumber, Long zipcode, boolean belongsToGroup,
            boolean isBorrowerActive, Long id, double borrowerLocationLatitude,
            double borrowerLocationLongitude) {
        this.borrowersId = borrowersId;
        this.loanOfficerId = loanOfficerId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.businessName = businessName;
        this.assignedBy = assignedBy;
        this.profileImageUri = profileImageUri;
        this.profileImageThumbUri = profileImageThumbUri;
        this.nationality = nationality;
        this.workAddress = workAddress;
        this.sex = sex;
        this.homeAddress = homeAddress;
        this.state = state;
        this.city = city;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.businessDescription = businessDescription;
        this.photovalidationId = photovalidationId;
        this.timestamp = timestamp;
        this.phoneNumber = phoneNumber;
        this.zipcode = zipcode;
        this.belongsToGroup = belongsToGroup;
        this.isBorrowerActive = isBorrowerActive;
        this.id = id;
        this.borrowerLocationLatitude = borrowerLocationLatitude;
        this.borrowerLocationLongitude = borrowerLocationLongitude;
    }

    @Generated(hash = 1250401098)
    public BorrowersTable() {
    }

    public String getBorrowersId() {
        return this.borrowersId;
    }

    public void setBorrowersId(String borrowersId) {
        this.borrowersId = borrowersId;
    }

    public String getLoanOfficerId() {
        return this.loanOfficerId;
    }

    public void setLoanOfficerId(String loanOfficerId) {
        this.loanOfficerId = loanOfficerId;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return this.middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBusinessName() {
        return this.businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getAssignedBy() {
        return this.assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public String getProfileImageUri() {
        return this.profileImageUri;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    public String getProfileImageThumbUri() {
        return this.profileImageThumbUri;
    }

    public void setProfileImageThumbUri(String profileImageThumbUri) {
        this.profileImageThumbUri = profileImageThumbUri;
    }

    public String getNationality() {
        return this.nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getWorkAddress() {
        return this.workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getHomeAddress() {
        return this.homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBusinessDescription() {
        return this.businessDescription;
    }

    public void setBusinessDescription(String businessDescription) {
        this.businessDescription = businessDescription;
    }

    public String getPhotovalidationId() {
        return this.photovalidationId;
    }

    public void setPhotovalidationId(String photovalidationId) {
        this.photovalidationId = photovalidationId;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Long getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getZipcode() {
        return this.zipcode;
    }

    public void setZipcode(Long zipcode) {
        this.zipcode = zipcode;
    }

    public boolean getBelongsToGroup() {
        return this.belongsToGroup;
    }

    public void setBelongsToGroup(boolean belongsToGroup) {
        this.belongsToGroup = belongsToGroup;
    }

    public boolean getIsBorrowerActive() {
        return this.isBorrowerActive;
    }

    public void setIsBorrowerActive(boolean isBorrowerActive) {
        this.isBorrowerActive = isBorrowerActive;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getBorrowerLocationLatitude() {
        return this.borrowerLocationLatitude;
    }

    public void setBorrowerLocationLatitude(double borrowerLocationLatitude) {
        this.borrowerLocationLatitude = borrowerLocationLatitude;
    }

    public double getBorrowerLocationLongitude() {
        return this.borrowerLocationLongitude;
    }

    public void setBorrowerLocationLongitude(double borrowerLocationLongitude) {
        this.borrowerLocationLongitude = borrowerLocationLongitude;
    }

    @Override
    public String toString() {
        return "BorrowersTable{" + "borrowersId='" + borrowersId + '\'' + ", loanOfficerId='" + loanOfficerId + '\'' + ", firstName='" + firstName + '\'' + ", middleName='" + middleName + '\'' + ", lastName='" + lastName + '\'' + ", businessName='" + businessName + '\'' + ", assignedBy='" + assignedBy + '\'' + ", profileImageUri='" + profileImageUri + '\'' + ", profileImageThumbUri='" + profileImageThumbUri + '\'' + ", nationality='" + nationality + '\'' + ", workAddress='" + workAddress + '\'' + ", sex='" + sex + '\'' + ", homeAddress='" + homeAddress + '\'' + ", state='" + state + '\'' + ", city='" + city + '\'' + ", dateOfBirth='" + dateOfBirth + '\'' + ", email='" + email + '\'' + ", businessDescription='" + businessDescription + '\'' + ", photovalidationId='" + photovalidationId + '\'' + ", timestamp=" + timestamp + ", phoneNumber=" + phoneNumber + ", zipcode=" + zipcode + ", belongsToGroup=" + belongsToGroup + ", isBorrowerActive=" + isBorrowerActive + ", id=" + id + ", borrowerLocationLatitude=" + borrowerLocationLatitude + ", borrowerLocationLongitude=" + borrowerLocationLongitude + '}';
    }

    // Parcelling part
    public BorrowersTable(Parcel in){
        String[] data = new String[26];

        in.readStringArray(data);
        this.borrowersId = data[0];
        this.loanOfficerId = data[1];
        this.firstName = data[2];
        this.middleName = data[3];
        this.lastName = data[4];
        this.businessName = data[5];
        this.assignedBy = data[6];
        this.profileImageUri = data[7];
        this.profileImageThumbUri = data[8];
        this.nationality = data[9];
        this.workAddress = data[10];
        this.sex = data[11];
        this.homeAddress = data[12];
        this.state = data[13];
        this.city = data[14];
        this.dateOfBirth = data[15];
        this.email = data[16];
        this.businessDescription = data[17];
        this.photovalidationId = data[18];
        this.phoneNumber = Long.valueOf(data[19]);
        this.zipcode = Long.valueOf(data[20]);
        this.belongsToGroup = Boolean.parseBoolean(data[21]);
        this.id = Long.valueOf(data[22]);
        this.borrowerLocationLatitude = Double.parseDouble(data[23]);
        this.borrowerLocationLongitude = Double.parseDouble(data[24]);
        this.isBorrowerActive = Boolean.parseBoolean(data[25]);

    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.borrowersId,
                this.loanOfficerId,
                this.firstName,
                this.middleName,
                this.lastName,
                this.businessName,
                this.assignedBy,
                this.profileImageUri,
                this.profileImageThumbUri,
                this.nationality,
                this.workAddress,
                this.sex,
                this.homeAddress,
                this.state,
                this.city,
                this.dateOfBirth,
                this.email,
                this.businessDescription,
                this.photovalidationId,String.valueOf(this.phoneNumber), String.valueOf(this.zipcode),
                String.valueOf(this.belongsToGroup), String.valueOf(this.id),
                String.valueOf(this.borrowerLocationLatitude), String.valueOf(this.borrowerLocationLongitude)
                , String.valueOf(this.isBorrowerActive)});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public BorrowersTable createFromParcel(Parcel in) {
            return new BorrowersTable(in);
        }

        public BorrowersTable[] newArray(int size) {
            return new BorrowersTable[size];
        }
    };
}
