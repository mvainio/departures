package com.home.mikkovainio.departures;

import java.util.List;

/**
 * Created by MikkoVainio on 12.1.2018.
 */

public interface DepartureStationParser {
    List<DepartureStation> parseStations(String data);
}
