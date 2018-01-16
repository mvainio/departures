package com.home.mikkovainio.departures;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MikkoVainio on 13.1.2018.
 */

public class DigiTrafficDepartureDataParser implements DepartureDataParser {
    @Override
    public List<DepartureData> parseDepartureData(String data, String station) {
        ArrayList<DepartureData> departureList = new ArrayList<>();
        if (data == null || data.isEmpty()) return departureList;

        try {
            JSONArray departures = new JSONArray(data);

            for (int j = 0; j < departures.length(); j++) {
                DepartureData departureData = new DepartureData();
                JSONObject departure = departures.getJSONObject(j);
                departureData.setTrain(parseTrain(departure));
                JSONArray stationList = departure.getJSONArray("timeTableRows");
                int stationListLegth = stationList.length();
                for (int i = 0; i < stationListLegth; i++) {
                    JSONObject stationData = stationList.getJSONObject(i);
                    parseTrackAndDepartureTime(station, departureData, stationData);
                    if (i == stationListLegth - 1) {
                        departureData.setDestination(stationData.getString("stationShortCode"));
                    }
                }
                departureList.add(departureData);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return departureList;
    }

    private void parseTrackAndDepartureTime(String station, DepartureData departureData, JSONObject stationData) throws JSONException {
        String stationCode = stationData.getString("stationShortCode");
        String type = stationData.getString("type");

        if (station.equals(stationCode) && type.equals("DEPARTURE")) {
            departureData.setTrack(stationData.getString("commercialTrack"));
            departureData.setScheduledDeparture(stationData.getString("scheduledTime"));
            if (stationData.has("differenceInMinutes")){
                departureData.setEstimatedDepartureDifference(stationData.getInt("differenceInMinutes"));
            }
        }
    }

    @NonNull
    private String parseTrain(JSONObject departure) throws JSONException {
        String train;
        train = departure.getString("commuterLineID");
        if (train.isEmpty()) {
            train = departure.getString("trainType") + departure.getString("trainNumber");
        }
        return train;
    }
}
