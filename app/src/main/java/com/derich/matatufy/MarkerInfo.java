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
    public String from;



    public MarkerInfo(){
    }
    public MarkerInfo(String longitude, String latitude, String sName, String destination, String price, String openingT, String closingT, String days, String from) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.sName = sName;
        this.destination = destination;
        this.price = price;
        this.openingT = openingT;
        this.closingT = closingT;
        this.days = days;
        this.from = from;
    }

}
