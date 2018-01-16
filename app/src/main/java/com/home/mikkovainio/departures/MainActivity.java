package com.home.mikkovainio.departures;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ListView stations;
    SimpleCursorAdapter adapter;
    LocationManager locationManager;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.stations);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.stations_swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSupportLoaderManager().restartLoader(0, null, MainActivity.this);
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        stations = (ListView) findViewById(R.id.list_view);
        adapter = new SimpleCursorAdapter(getBaseContext(), R.layout.stations_list, null, new String[] { DeparturesContract.StationColumns.STATION_NAME }, new int[] {R.id.station_name}, 0);
        stations.setAdapter(adapter);

        stations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = (Cursor)adapter.getItem(i);
                String station = cursor.getString(cursor.getColumnIndex(DeparturesContract.StationColumns.STATION_ID));
                String stationName = cursor.getString(cursor.getColumnIndex(DeparturesContract.StationColumns.STATION_NAME));
                Intent intent = new Intent(MainActivity.this, DepartureActivity.class);
                intent.putExtra("station", station);
                intent.putExtra("stationName", stationName);
                startActivity(intent);
            }
        });

        if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make((View)findViewById(R.id.list_view), R.string.permission_request_rationale, Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION}, 1);
                }
            }).show();
        } else {
            getSupportLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != 1) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortOrder = null;
        String[] selectionArgs = null;
        if (locationAvailable()) {
            sortOrder = DeparturesContract.STATION_SORT_BY_LOCATION;
            @SuppressLint("MissingPermission") Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            selectionArgs = new String[] {String.valueOf(lastKnownLocation.getLatitude()), String.valueOf(lastKnownLocation.getLongitude())};
        }
        return new CursorLoader(this, DeparturesContract.STATIONS_URI, null, null, selectionArgs, sortOrder);
    }

    private boolean locationAvailable() {
        return ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

}
