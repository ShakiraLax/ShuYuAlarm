package com.sypm.shuyualarm.utils;


import com.tumblr.remember.Remember;


public class RememberHelper {

    public static void saveUserInfo(String number, String password) {
        Remember.putString("number", number);
        Remember.putString("password", password);
    }

    public static void saveStoreName(String storeName) {
        Remember.putString("storeName", storeName);
    }

    public static String getNumber() {
        return Remember.getString("number", "");
    }

    public static String getPassword() {
        return Remember.getString("password", "");
    }

    public static String getStoreName() {
        return Remember.getString("storeName", "");
    }

}
