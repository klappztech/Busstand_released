package com.klappztech.platform;

/**
 * Created by mahc on 10/7/2015.
 */
public class BusDatabaseObject {

    public String busNo, destination, platform;

    // constructor for adding sample data
    public BusDatabaseObject(String busNo, String destination, String platform){

        this.busNo          = busNo;
        this.destination    = destination;
        this.platform       = platform;
    }

}
