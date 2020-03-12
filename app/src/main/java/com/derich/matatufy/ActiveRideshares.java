package com.derich.matatufy;

public class ActiveRideshares {
    String rideSharerEmail;
    String rideShareeName;
    String rideShareDate;
    String phone;
    String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    Boolean status;
public ActiveRideshares() {

}
    public ActiveRideshares(String rideSharerEmail, String rideShareeName, String rideShareDate,String time, String phone, Boolean status) {
        this.rideSharerEmail = rideSharerEmail;
        this.rideShareeName = rideShareeName;
        this.rideShareDate = rideShareDate;
        this.phone = phone;
        this.time = time;
        this.status = status;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRideSharerEmail() {
        return rideSharerEmail;
    }

    public void setRideSharerEmail(String rideSharerEmail) {
        this.rideSharerEmail = rideSharerEmail;
    }

    public String getRideShareeName() {
        return rideShareeName;
    }

    public void setRideShareeName(String rideShareeName) {
        this.rideShareeName = rideShareeName;
    }

    public String getRideShareDate() {
        return rideShareDate;
    }

    public void setRideShareDate(String rideShareDate) {
        this.rideShareDate = rideShareDate;
    }

}
