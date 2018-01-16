package com.home.mikkovainio.departures;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by MikkoVainio on 13.1.2018.
 */

public class DepartureData {

    private String train;
    private String track;
    private String destination;
    private String scheduledDeparture;
    private String estimatedDeparture;
    private int estimatedDepartureDifference;
    private Date departureDate;
    private final DateFormat displayDateFormat = new SimpleDateFormat("HH:mm");

    public DepartureData() {
    }

    public String getTrain() {
        return train;
    }
    public void setTrain(String train) { this.train = train; }

    public String getTrack() {
        return track;
    }
    public void setTrack(String track) { this.track = track; }

    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) { this.destination = destination; }

    public String getScheduledDeparture() {
        return scheduledDeparture;
    }
    public void setScheduledDeparture(String departure) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            departureDate = df.parse(departure);
            scheduledDeparture = displayDateFormat.format(departureDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public int getEstimatedDepartureDifference() {
        return estimatedDepartureDifference;
    }
    public void setEstimatedDepartureDifference(int difference) {
        estimatedDepartureDifference = difference;
        if (difference > 0 && departureDate != null) {
            long utcMS = departureDate.getTime();
            estimatedDeparture = displayDateFormat.format(new Date(utcMS + (difference * 60000)));
        }
    }

    public String getEstimatedDeparture() {
        return estimatedDeparture;
    }

    public Date getDepartureDate() {
        return departureDate;
    }
}
