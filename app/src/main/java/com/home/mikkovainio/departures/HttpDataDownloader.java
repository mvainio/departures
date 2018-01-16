package com.home.mikkovainio.departures;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by MikkoVainio on 12.1.2018.
 */

public class HttpDataDownloader implements DataDownloader {
    @Override
    public String DownloadData(String url) {
        try {
            URL targetUrl = new URL(url);
            HttpsURLConnection connection = null;
            InputStream in = null;
            String result = null;

            try {
                connection = (HttpsURLConnection) targetUrl.openConnection();
                in = connection.getInputStream();
                return readStream(in);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
              connection.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String readStream(InputStream stream) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuffer buffer = new StringBuffer();
        String read;
        while ((read = reader.readLine()) != null) {
            buffer.append(read);
        }
        reader.close();
        return buffer.toString();
    }
}
