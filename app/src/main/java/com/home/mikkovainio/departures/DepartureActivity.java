package com.home.mikkovainio.departures;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class DepartureActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    ListView stations;
    SimpleCursorAdapter adapter;
    String station;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departure);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.departure_swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSupportLoaderManager().restartLoader(0, null, DepartureActivity.this);
            }
        });

        stations = (ListView) findViewById(R.id.departures_list);
        adapter = new SimpleCursorAdapter(getBaseContext(), R.layout.departure_item, null,
                new String[] {DeparturesContract.DepartureColumns.SCHEDULED_DEPARTURE, DeparturesContract.DepartureColumns.TRAIN,
                        DeparturesContract.DepartureColumns.TRACK, DeparturesContract.DepartureColumns.ESTIMATED_DEPARTURE,
                        DeparturesContract.DepartureColumns.DESTINATION}, new int[] {R.id.time, R.id.train, R.id.track, R.id.est_time, R.id.destination}, 0);
        stations.setAdapter(adapter);


        Intent intent = getIntent();
        station = intent.getExtras().getString("station");
        setTitle(intent.getExtras().getString("stationName"));

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DeparturesContract.DEPARTURES_URI, null, null, new String[] {station}, null);
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
