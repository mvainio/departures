package com.home.mikkovainio.departures;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MikkoVainio on 12.1.2018.
 */

public class DigiTrafficDepartureStationParser implements DepartureStationParser {

    private static final String STATION_ID = "stationShortCode";
    private static final String STATION_NAME = "stationName";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String PASSENGER_TRAFFIC = "passengerTraffic";

    @Override
    public List<DepartureStation> parseStations(String data) {
        ArrayList<DepartureStation> departureStations = new ArrayList<>();
        if (data == null || data.isEmpty()) return departureStations;
        try {
            JSONArray stations = new JSONArray(data);
            for (int i = 0; i < stations.length(); i++) {
                JSONObject stationData = stations.getJSONObject(i);
                DepartureStation station = new DepartureStation(stationData.getString(STATION_ID), parseStationName(stationData.getString(STATION_NAME)), stationData.getDouble(LATITUDE), stationData.getDouble(LONGITUDE), stationData.getBoolean(PASSENGER_TRAFFIC));
                departureStations.add(station);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return departureStations;
    }

    private String parseStationName(String name) {
        String[] parts = name.split(" ");
        if (parts.length > 1 && "asema".equals(parts[1])) {
            return parts[0];
        }
        return name;
    }
}
