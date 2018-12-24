package com.icubed.loansticdroid.localdatabase;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

public class BorrowerCloudDetails {

    private String loanOfficerId, firstName, middleName, lastName, businessName
            ,profileImageUri, profileImageThumbUri, nationality, workAddress, sex
            ,homeAddress, state, city, dateOfBirth;

    private GeoPoint borrowerGeoPoint;

    public BorrowerCloudDetails(){}

    public BorrowerCloudDetails(String loanOfficerId, String firstName, String middleName, String lastName, String businessName, String profileImageUri, String profileImageThumbUri, String nationality, String workAddress, String sex, String homeAddress, String state, String city, String dateOfBirth, GeoPoint borrowerGeoPoint) {
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
        this.borrowerGeoPoint = borrowerGeoPoint;
    }

    public String getLoanOfficerId() {
        return loanOfficerId;
    }

    public void setLoanOfficerId(String loanOfficerId) {
        this.loanOfficerId = loanOfficerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    public String getProfileImageThumbUri() {
        return profileImageThumbUri;
    }

    public void setProfileImageThumbUri(String profileImageThumbUri) {
        this.profileImageThumbUri = profileImageThumbUri;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public GeoPoint getBorrowerGeoPoint() {
        return borrowerGeoPoint;
    }

    public void setBorrowerGeoPoint(GeoPoint borrowerGeoPoint) {
        this.borrowerGeoPoint = borrowerGeoPoint;
    }

    @Override
    public String toString() {
        return "BorrowerCloudDetails{" + "loanOfficerId='" + loanOfficerId + '\'' + ", firstName='" + firstName + '\'' + ", middleName='" + middleName + '\'' + ", lastName='" + lastName + '\'' + ", businessName='" + businessName + '\'' + ", profileImageUri='" + profileImageUri + '\'' + ", profileImageThumbUri='" + profileImageThumbUri + '\'' + ", nationality='" + nationality + '\'' + ", workAddress='" + workAddress + '\'' + ", sex='" + sex + '\'' + ", homeAddress='" + homeAddress + '\'' + ", state='" + state + '\'' + ", city='" + city + '\'' + ", dateOfBirth='" + dateOfBirth + '\'' + ", borrowerGeoPoint=" + borrowerGeoPoint + '}';
    }
}
