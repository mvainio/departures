package com.home.mikkovainio.departures;

import java.util.List;

/**
 * Created by MikkoVainio on 13.1.2018.
 */

public interface DepartureDataParser {
    public List<DepartureData> parseDepartureData(String data, String station);
}
