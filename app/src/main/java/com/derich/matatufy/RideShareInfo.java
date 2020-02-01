package com.derich.matatufy;

public class RideShareInfo {
   public String driverName;
    public String carModel;
    public String driverPhone;
    public String date;
    public String from;
    public String destination;
    public String time;
    public String amount;

    public String getDriverName() {
        return driverName;
    }

    public String getCarModel() {
        return carModel;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public String getDate() {
        return date;
    }

    public String getFrom() {
        return from;
    }

    public String getDestination() {
        return destination;
    }

    public String getTime() {
        return time;
    }

    public String getAmount() {
        return amount;
    }

    public RideShareInfo(){

    }

    public RideShareInfo(String driverName, String carModel, String driverPhone, String date, String from, String destination, String time, String amount) {
        this.driverName = driverName;
        this.carModel = carModel;
        this.driverPhone = driverPhone;
        this.date = date;
        this.from = from;
        this.destination = destination;
        this.time = time;
        this.amount = amount;
    }
}