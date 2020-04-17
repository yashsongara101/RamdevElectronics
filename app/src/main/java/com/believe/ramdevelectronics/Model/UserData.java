package com.believe.ramdevelectronics.Model;

public class UserData {

    private String userName, userEmail, userId, userProfileImageUrl, userPhoneNumber, primaryAddress, primaryAddressKey, primaryAddressType;

    public UserData() {    }

    public UserData(String userName, String userEmail, String userId, String userProfileImageUrl,
                    String userPhoneNumber, String primaryAddress, String primaryAddressKey, String primaryAddressType) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userId = userId;
        this.userProfileImageUrl = userProfileImageUrl;
        this.userPhoneNumber = userPhoneNumber;
        this.primaryAddress = primaryAddress;
        this.primaryAddressKey = primaryAddressKey;
        this.primaryAddressType = primaryAddressType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserProfileImageUrl() {
        return userProfileImageUrl;
    }

    public void setUserProfileImageUrl(String userProfileImageUrl) {
        this.userProfileImageUrl = userProfileImageUrl;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getPrimaryAddress() {
        return primaryAddress;
    }

    public void setPrimaryAddress(String primaryAddress) {
        this.primaryAddress = primaryAddress;
    }

    public String getPrimaryAddressKey() {
        return primaryAddressKey;
    }

    public void setPrimaryAddressKey(String primaryAddressKey) {
        this.primaryAddressKey = primaryAddressKey;
    }

    public String getPrimaryAddressType() {
        return primaryAddressType;
    }

    public void setPrimaryAddressType(String primaryAddressType) {
        this.primaryAddressType = primaryAddressType;
    }
}
