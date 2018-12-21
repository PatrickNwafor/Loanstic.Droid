package com.icubed.loansticdroid.localdatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity()
public class BorrowersTable {

    @Unique
    private String BorrowersId;

    private String name;

    private String business;

    private String profileImageUri;

    @Generated(hash = 628926127)
    public BorrowersTable(String BorrowersId, String name, String business,
            String profileImageUri) {
        this.BorrowersId = BorrowersId;
        this.name = name;
        this.business = business;
        this.profileImageUri = profileImageUri;
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBusiness() {
        return this.business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getProfileImageUri() {
        return this.profileImageUri;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    @Override
    public String toString() {
        return "BorrowersTable{" + "BorrowersId='" + BorrowersId + '\'' + ", name='" + name + '\'' + ", business='" + business + '\'' + ", profileImageUri='" + profileImageUri + '\'' + '}';
    }
}
