package com.home.mikkovainio.departures;

import android.location.Location;

/**
 * Created by MikkoVainio on 12.1.2018.
 */

public class DepartureStation {

    private final String id;
    private final String stationName;
    private final double latitude;
    private final double longitude;
    private final boolean passengerTraffic;
    private double distance;

    public DepartureStation(String Id, String stationName, double latitude, double longitude, boolean passengerTraffic)
    {
        this.id =Id;
        this.stationName = stationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.passengerTraffic = passengerTraffic;
    }

    public String getId()
    {
        return id;
    }

    public String getStationName() { return stationName; }

    public double getLatitude()
    {
        return latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public Location getLocation() {
        Location loc = new Location("");
        loc.setLongitude(longitude);
        loc.setLatitude(latitude);

        return loc;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() { return distance; }

    public boolean isPassengerTraffic() {
        return passengerTraffic;
    }
}
