package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class AccountTable {
    @Unique
    private String accountId;
    @Id(autoincrement = true)
    private Long id;

    private String firstName, middleName, lastName, branchName
            ,profileImageUri, profileImageThumbUri, sex, dateOfBirth
            ,homeAddress, nationality, state, accountType;

    private Date timestamp;
    private Date lastUpdatedAt;

    @Generated(hash = 932989188)
    public AccountTable(String accountId, Long id, String firstName,
            String middleName, String lastName, String branchName,
            String profileImageUri, String profileImageThumbUri, String sex,
            String dateOfBirth, String homeAddress, String nationality,
            String state, String accountType, Date timestamp, Date lastUpdatedAt) {
        this.accountId = accountId;
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.branchName = branchName;
        this.profileImageUri = profileImageUri;
        this.profileImageThumbUri = profileImageThumbUri;
        this.sex = sex;
        this.dateOfBirth = dateOfBirth;
        this.homeAddress = homeAddress;
        this.nationality = nationality;
        this.state = state;
        this.accountType = accountType;
        this.timestamp = timestamp;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    @Generated(hash = 528739196)
    public AccountTable() {
    }

    public String getAccountId() {
        return this.accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getBranchName() {
        return this.branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
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

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getHomeAddress() {
        return this.homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getNationality() {
        return this.nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAccountType() {
        return this.accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getLastUpdatedAt() {
        return this.lastUpdatedAt;
    }

    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
}
