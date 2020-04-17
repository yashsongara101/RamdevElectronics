package com.believe.ramdevelectronics.Model;

public class AddressData {

    private String userAddress, userAddressType, userAddressKey;

    public AddressData() {
    }

    public AddressData(String userAddress, String userAddressType, String userAddressKey) {
        this.userAddress = userAddress;
        this.userAddressType = userAddressType;
        this.userAddressKey = userAddressKey;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserAddressType() {
        return userAddressType;
    }

    public void setUserAddressType(String userAddressType) {
        this.userAddressType = userAddressType;
    }

    public String getUserAddressKey() {
        return userAddressKey;
    }

    public void setUserAddressKey(String userAddressKey) {
        this.userAddressKey = userAddressKey;
    }
}
