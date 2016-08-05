package com.kcarante.app.firebasecontact.Constructor;

/**
 * Created by SARJ on 8/1/2016.
 */
public class Contacts {

    private String contactNumber;
    private String name;

    public Contacts() {
    }

    public Contacts(String name){
        this.name = name;
    }

    public Contacts(String contactNumber, String name){
        this.contactNumber = contactNumber;
        this.name = name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
