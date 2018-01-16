package com.home.mikkovainio.departures;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
/**
 * Created by MikkoVainio on 12.1.2018.
 */

public class DigiTrafficDepartureStationTest {

    @Test
    public void emptyDataTest() {
        DigiTrafficDepartureStationParser parser = new DigiTrafficDepartureStationParser();
        assertEquals(0, parser.parseStations(null).size());
    }

    @Test
    public void testOneStation() {
        String oneStationData = "[{\"passengerTraffic\":true,\"type\":\"STATION\",\"stationName\":\"Kerava asema\",\"stationShortCode\":\"KE\",\"stationUICCode\":20,\"countryCode\":\"FI\",\"longitude\":25.10650900000000,\"latitude\":60.40445700000000}]";
        DigiTrafficDepartureStationParser parser = new DigiTrafficDepartureStationParser();
        List<DepartureStation> list = parser.parseStations(oneStationData);
        assertEquals(1, list.size());
        assertEquals("KE", list.get(0).getId());
        assertEquals("Kerava", list.get(0).getStationName());
    }

    @Test
    public void testMultipleStations() {
        String multipleStationData = "[{\"passengerTraffic\":false,\"type\":\"STATION\",\"stationName\":\"Ahonpää\",\"stationShortCode\":\"AHO\",\"stationUICCode\":1343,\"countryCode\":\"FI\",\"longitude\":25.01206600000000,\"latitude\":64.55181700000000},{\"passengerTraffic\":false,\"type\":\"STATION\",\"stationName\":\"Ahvenus\",\"stationShortCode\":\"AHV\",\"stationUICCode\":1000,\"countryCode\":\"FI\",\"longitude\":22.49759200000000,\"latitude\":61.29240700000000},{\"passengerTraffic\":true,\"type\":\"STOPPING_POINT\",\"stationName\":\"Ainola\",\"stationShortCode\":\"AIN\",\"stationUICCode\":628,\"countryCode\":\"FI\",\"longitude\":25.10200100000000,\"latitude\":60.45689700000000}]";
        DigiTrafficDepartureStationParser parser = new DigiTrafficDepartureStationParser();
        List<DepartureStation> list = parser.parseStations(multipleStationData);
        assertEquals(3, list.size());
    }
}
