package com.bitcoder.bd;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by rajat on 30/1/17.
 */

public class DonorNearby implements Serializable{
    private String phoneno,name,bloodGroup,address,city,details,requestedOn;
    private double latitude,longitude;
    public DonorNearby(){

    }

    public DonorNearby(String address, String bloodGroup, String city, String details, double latitude,
                       double longitude, String name, String phoneno, String  requestedOn) {
        this.address = address;
        this.bloodGroup = bloodGroup;
        this.city = city;
        this.details = details;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.phoneno = phoneno;
        this.requestedOn = requestedOn;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getRequestedOn() {
        return requestedOn;
    }

    public void setRequestedOn(String requestedOn) {
        this.requestedOn = requestedOn;
    }
}
