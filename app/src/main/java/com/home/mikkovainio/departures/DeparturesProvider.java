package com.home.mikkovainio.departures;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.location.Location;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class DeparturesProvider extends ContentProvider {


    private static final String STATION_URL = "https://rata.digitraffic.fi/api/v1/metadata/stations";
    public static final int STATIONS_DATA = 1;
    public static final int DEPARTURES_DATA = 2;

    private static final UriMatcher URIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URIMatcher.addURI(DeparturesContract.AUTHORITY, DeparturesContract.STATIONS, STATIONS_DATA);
        URIMatcher.addURI(DeparturesContract.AUTHORITY, DeparturesContract.DEPARTURES, DEPARTURES_DATA);
    }

    private HttpDataDownloader downloader;
    private DigiTrafficDepartureStationParser stationDataParser;
    private DigiTrafficDepartureDataParser departureDataParser;
    private HashMap<String, DepartureStation> stationCache;

    public DeparturesProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        int match = URIMatcher.match(uri);

        switch (match) {
            case STATIONS_DATA:
                return DeparturesContract.STATIONS_CONTENT_TYPE;
            case DEPARTURES_DATA:
                return DeparturesContract.DEPARTURES_CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI => "+ uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public boolean onCreate() {
        downloader = new HttpDataDownloader();
        stationDataParser = new DigiTrafficDepartureStationParser();
        departureDataParser = new DigiTrafficDepartureDataParser();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        int match = URIMatcher.match(uri);

        switch (match) {
            case STATIONS_DATA:
                if (stationCache == null) {
                    fillStationCache();
                }
                List<DepartureStation> stationData = new ArrayList<>(stationCache.values());
                if (DeparturesContract.STATION_SORT_BY_LOCATION.equals(sortOrder)) {
                    sortStationsByLocation(selectionArgs, stationData);
                }
                return createStationCursor(stationData);
            case DEPARTURES_DATA:
                List<DepartureData> departureData = departureDataParser.parseDepartureData(downloader.DownloadData(makeDepartureDataUrl(selectionArgs)), selectionArgs[0]);
                Collections.sort(departureData, new Comparator<DepartureData>() {
                    @Override
                    public int compare(DepartureData data1, DepartureData data2) {
                        return data1.getDepartureDate().compareTo(data2.getDepartureDate());
                    }
                });
                return createDepartureCursor(departureData);
            default:
                throw new IllegalArgumentException("Unknown URI => "+ uri);
        }
    }

    private void fillStationCache() {
        List<DepartureStation> stationList = stationDataParser.parseStations(downloader.DownloadData(STATION_URL));
        stationCache = new HashMap<>();
        for(DepartureStation station : stationList) {
            stationCache.put(station.getId(), station);
        }
    }

    private void sortStationsByLocation(String[] selectionArgs, List<DepartureStation> stationData) {
        Location currentLocation = new Location("");
        currentLocation.setLatitude(Double.parseDouble(selectionArgs[0]));
        currentLocation.setLongitude(Double.parseDouble(selectionArgs[1]));
        for(DepartureStation station : stationData) {
            station.setDistance(currentLocation.distanceTo(station.getLocation()));
        }
        Collections.sort(stationData, new Comparator<DepartureStation>() {
            @Override
            public int compare(DepartureStation departureStation1, DepartureStation departureStation2) {
                return (int) (departureStation1.getDistance() - departureStation2.getDistance());
            }
        });
    }

    private String makeDepartureDataUrl(String[] selectionArgs) {
        Uri.Builder builder = new Uri.Builder();
        return builder.scheme("https").authority("rata.digitraffic.fi").appendPath("api").appendPath("v1").appendPath("live-trains").appendPath("station").appendPath(selectionArgs[0])
                .appendQueryParameter("arrived_trains", "0").appendQueryParameter("arriving_trains", "0").appendQueryParameter("departed_trains", "0").appendQueryParameter("departing_trains", "20")
                .appendQueryParameter("include_nonstopping", "false").build().toString();
    }

    private Cursor createDepartureCursor(List<DepartureData> data) {
        MatrixCursor cursor = new MatrixCursor(new String[]{BaseColumns._ID, DeparturesContract.DepartureColumns.TRAIN,
                DeparturesContract.DepartureColumns.TRACK,
                DeparturesContract.DepartureColumns.SCHEDULED_DEPARTURE, DeparturesContract.DepartureColumns.ESTIMATED_DEPARTURE,
                DeparturesContract.DepartureColumns.DESTINATION});

        MatrixCursor.RowBuilder builder;
        int id = 1;
        for (DepartureData departure: data) {
            builder = cursor.newRow();
            builder.add(id++).add(departure.getTrain()).add(departure.getTrack())
                    .add(departure.getScheduledDeparture())
                    .add(departure.getEstimatedDeparture()).add(stationCache.get(departure.getDestination()).getStationName());
        }
        return cursor;
    }

    private Cursor createStationCursor(List<DepartureStation> data) {
        MatrixCursor cursor = new MatrixCursor(new String[]{BaseColumns._ID, DeparturesContract.StationColumns.STATION_ID, DeparturesContract.StationColumns.STATION_NAME});
        MatrixCursor.RowBuilder builder;
        int id = 1;
        for (DepartureStation station: data) {
            if (station.isPassengerTraffic()) {
                builder = cursor.newRow();
                builder.add(id++).add(station.getId()).add(station.getStationName());
            }
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0;
    }
}
