package com.example.ch3;


public class Marketing {
    public static boolean isHotmailAddress(EmailAddress address) {
        //address.setDomain("Test");
        return address.getDomain().equalsIgnoreCase("hotmail.com");
    }
}