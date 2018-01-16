package com.home.mikkovainio.departures;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.*;
/**
 * Created by MikkoVainio on 13.1.2018.
 */

public class DigiTrafficDepartureDataParserTest {

    private DigiTrafficDepartureDataParser parser;

    @Before
    public void setUp()
    {
        parser = new DigiTrafficDepartureDataParser();
    }

    @Test
    public void testEmptyData()
    {
        assertEquals(0, parser.parseDepartureData("", "KE").size());
    }

    @Test
    public void testOneDepartureTrain() throws IOException {
        assertEquals(1, parser.parseDepartureData(readResource("oneTrain.json"), "KE").size());
    }

    @Test
    public void testTrainName() throws IOException {
        List<DepartureData> list = parser.parseDepartureData(readResource("oneTrain.json"), "KE");
        assertEquals("Z",list.get(0).getTrain());
    }

    @Test
    public void testTrack() throws IOException {
        List<DepartureData> list = parser.parseDepartureData(readResource("oneTrain.json"), "KE");
        assertEquals("4",list.get(0).getTrack());
    }

    @Test
    public void testDestination() throws IOException {
        List<DepartureData> list = parser.parseDepartureData(readResource("oneTrain.json"), "KE");
        assertEquals("LH",list.get(0).getDestination());
    }

    @Test
    public void testMultipleTrains() throws IOException {
        List<DepartureData> list = parser.parseDepartureData(readResource("multipleTrains.json"), "KE");
        assertEquals(5,list.size());
    }

    private String readResource(String resourceName) throws IOException {
        ClassLoader loader = getClass().getClassLoader();
        URL url = loader.getResource(resourceName);
        File file = new File(url.getPath());
        FileInputStream fin = new FileInputStream(file);

        BufferedReader reader = new BufferedReader(new InputStreamReader(fin, "UTF8"));
        StringBuffer buffer = new StringBuffer();
        String line = null;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        fin.close();
        return buffer.toString();
    }
}
