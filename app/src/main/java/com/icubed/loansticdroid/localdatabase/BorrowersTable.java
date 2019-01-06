package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Date;

@Entity
public class BorrowersTable {

    @Unique
    private String BorrowersId;

    private String loanOfficerId, firstName, middleName, lastName, businessName, assignedBy
            ,profileImageUri, profileImageThumbUri, nationality, workAddress, sex
            ,homeAddress, state, city, dateOfBirth, email, businessDescription, photovalidationId;

    private Date timestamp;
    private Long phoneNumber, zipcode;

    @Id(autoincrement = true)
    private Long id;

    private double borrowerLocationLatitude, borrowerLocationLongitude;

    @Generated(hash = 91502844)
    public BorrowersTable(String BorrowersId, String loanOfficerId, String firstName, String middleName,
            String lastName, String businessName, String assignedBy, String profileImageUri,
            String profileImageThumbUri, String nationality, String workAddress, String sex,
            String homeAddress, String state, String city, String dateOfBirth, String email,
            String businessDescription, String photovalidationId, Date timestamp, Long phoneNumber,
            Long zipcode, Long id, double borrowerLocationLatitude, double borrowerLocationLongitude) {
        this.BorrowersId = BorrowersId;
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
        this.id = id;
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
}
