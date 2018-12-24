package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Date;

@Entity()
public class BorrowersTable {

    @Unique
    private String BorrowersId;

    private String loanOfficerId, firstName, middleName, lastName, businessName
            ,profileImageUri, profileImageThumbUri, nationality, workAddress, sex
            ,homeAddress, state, city, dateOfBirth;

    private Date timestamp;

    private double borrowerLocationLatitude, borrowerLocationLongitude;

    @Generated(hash = 1777052653)
    public BorrowersTable(String BorrowersId, String loanOfficerId,
            String firstName, String middleName, String lastName,
            String businessName, String profileImageUri,
            String profileImageThumbUri, String nationality, String workAddress,
            String sex, String homeAddress, String state, String city,
            String dateOfBirth, Date timestamp, double borrowerLocationLatitude,
            double borrowerLocationLongitude) {
        this.BorrowersId = BorrowersId;
        this.loanOfficerId = loanOfficerId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.businessName = businessName;
        this.profileImageUri = profileImageUri;
        this.profileImageThumbUri = profileImageThumbUri;
        this.nationality = nationality;
        this.workAddress = workAddress;
        this.sex = sex;
        this.homeAddress = homeAddress;
        this.state = state;
        this.city = city;
        this.dateOfBirth = dateOfBirth;
        this.timestamp = timestamp;
        this.borrowerLocationLatitude = borrowerLocationLatitude;
        this.borrowerLocationLongitude = borrowerLocationLongitude;
    }

    @Generated(hash = 1250401098)
    public BorrowersTable() {
    }

    public String getBorrowersId() {
        return this.BorrowersId;
    }

    public void setBorrowersId(String BorrowersId) {
        this.BorrowersId = BorrowersId;
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

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
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
    
}
