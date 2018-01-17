package com.home.mikkovainio.departures;


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
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView stations;
    private SimpleCursorAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.stations);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.stations_swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLocationAndLoadStations(true);
            }
        });

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
            refreshLocationAndLoadStations(false);

        }
    }

    private boolean locationAvailable() {
        return ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("MissingPermission")
    private void refreshLocationAndLoadStations(final boolean restartLoader) {
        if (locationAvailable()) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        lastKnownLocation = location;
                    }
                    loadStations(MainActivity.this, restartLoader);
                }
            });
        } else {
            loadStations(MainActivity.this, restartLoader);
        }
    }

    private void loadStations(LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks, boolean restartLoader) {
        if (restartLoader) {
            getSupportLoaderManager().restartLoader(0, null, loaderCallbacks);
        } else {
            getSupportLoaderManager().initLoader(0, null, loaderCallbacks);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != 1) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        refreshLocationAndLoadStations(false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortOrder = null;
        String[] selectionArgs = null;
        if (lastKnownLocation != null) {
            sortOrder = DeparturesContract.STATION_SORT_BY_LOCATION;
            selectionArgs = new String[] {String.valueOf(lastKnownLocation.getLatitude()), String.valueOf(lastKnownLocation.getLongitude())};
        }
        return new CursorLoader(this, DeparturesContract.STATIONS_URI, null, null, selectionArgs, sortOrder);
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
