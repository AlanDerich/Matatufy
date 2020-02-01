package com.derich.matatufy;

public class MarkerInfo {
    public String longitude;
    public String latitude;
    public String sName;
    public String destination;
    public String price;
    public String openingT;
    public String closingT;
    public String days;

    public MarkerInfo(){
    }
    public MarkerInfo(String longitude, String latitude, String sName, String destination, String price, String openingT, String closingT, String days){
        this.latitude = latitude;
        this.longitude = longitude;
        this.sName = sName;
        this.destination = destination;
        this.price = price;
        this.openingT = openingT;
        this.closingT = closingT;
        this.days = days;
    }

}
