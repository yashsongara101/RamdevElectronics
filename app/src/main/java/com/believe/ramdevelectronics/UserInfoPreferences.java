package com.believe.ramdevelectronics;

import android.app.Activity;
import android.content.SharedPreferences;

public class UserInfoPreferences {

    private Activity activity;
    private String userName, userEmail, userId, userProfileImageUrl, userPhoneNumber, primaryAddress, primaryAddressKey, primaryAddressType;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public UserInfoPreferences() {
    }

    public UserInfoPreferences(Activity activity) {
        this.activity = activity;
        sharedPreferences = activity.getSharedPreferences("UserData",activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        userName = sharedPreferences.getString("userName","");
        userEmail = sharedPreferences.getString("userEmail","");
        userId = sharedPreferences.getString("userId","");
        userProfileImageUrl = sharedPreferences.getString("userProfileImageUrl","");
        userPhoneNumber = sharedPreferences.getString("userPhoneNumber","");
        primaryAddress = sharedPreferences.getString("primaryAddress","");
        primaryAddressKey = sharedPreferences.getString("primaryAddressKey","");
        primaryAddressType = sharedPreferences.getString("primaryAddressType","");
    }

    public UserInfoPreferences(Activity activity, String userName, String userEmail, String userId, String userProfileImageUrl,
                               String userPhoneNumber, String primaryAddress, String primaryAddressKey, String primaryAddressType) {
        this.activity = activity;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userId = userId;
        this.userProfileImageUrl = userProfileImageUrl;
        this.userPhoneNumber = userPhoneNumber;
        this.primaryAddress = primaryAddress;
        this.primaryAddressKey = primaryAddressKey;
        this.primaryAddressType = primaryAddressType;

        sharedPreferences = activity.getSharedPreferences("UserData", activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.putString("userName", userName);
        editor.putString("userEmail", userEmail);
        editor.putString("userId", userId);
        editor.putString("userProfileImageUrl", userProfileImageUrl);
        editor.putString("userPhoneNumber", userPhoneNumber);
        editor.putString("primaryAddress",primaryAddress);
        editor.putString("primaryAddressKey",primaryAddressKey);
        editor.putString("primaryAddressType",primaryAddressType);
        editor.apply();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        editor.putString("userName", userName);
        editor.apply();
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        editor.putString("userEmail", userEmail);
        editor.apply();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        editor.putString("userId", userId);
        editor.apply();
    }

    public String getUserProfileImageUrl() {
        return userProfileImageUrl;
    }

    public void setUserProfileImageUrl(String userProfileImageUrl) {
        editor.putString("userProfileImageUrl", userProfileImageUrl);
        editor.apply();
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        editor.putString("userPhoneNumber", userPhoneNumber);
        editor.apply();
    }

    public String getPrimaryAddress() {
        return primaryAddress;
    }

    public void setPrimaryAddress(String primaryAddress) {
        editor.putString("primaryAddress", primaryAddress);
        editor.apply();
    }

    public String getPrimaryAddressKey() {
        return primaryAddressKey;
    }

    public void setPrimaryAddressKey(String primaryAddressKey) {
        editor.putString("primaryAddressKey", primaryAddressKey);
        editor.apply();
    }

    public String getPrimaryAddressType() {
        return primaryAddressType;
    }

    public void setPrimaryAddressType(String primaryAddressType) {
        editor.putString("primaryAddressType", primaryAddressType);
        editor.apply();
    }
}
