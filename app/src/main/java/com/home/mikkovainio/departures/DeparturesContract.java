package com.home.mikkovainio.departures;

import android.content.ContentResolver;
import android.net.Uri;

/**
 * Created by MikkoVainio on 13.1.2018.
 */

public final class DeparturesContract {

    public static final String AUTHORITY = "com.home.mikkovainio.departures.DeparturesProvider";
    public static final String STATIONS = "stations";
    public static final String DEPARTURES ="departures";

    public static final String STATIONS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."+AUTHORITY+"."+STATIONS;
    public static final String DEPARTURES_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."+AUTHORITY+"."+DEPARTURES;

    public static final Uri STATIONS_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTHORITY + "/" + STATIONS);
    public static final Uri DEPARTURES_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTHORITY + "/" + DEPARTURES);

    public static final class StationColumns {
        public static final String STATION_ID = "stationId";
        public static final String STATION_NAME = "name";
    }

    public static final String STATION_SORT_BY_LOCATION = "SORT_BY_LOCATION";

    public static final class DepartureColumns {
        public static final String TRAIN = "train";
        public static final String TRACK = "track";
        public static final String DESTINATION = "destination";
        public static final String SCHEDULED_DEPARTURE = "scheduledDeparture";
        public static final String ESTIMATED_DEPARTURE = "estimatedDeparture";
    }
}
