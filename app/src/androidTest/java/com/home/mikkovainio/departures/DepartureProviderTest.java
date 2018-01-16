package com.home.mikkovainio.departures;

import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by MikkoVainio on 13.1.2018.
 */
@RunWith(AndroidJUnit4.class)
public class DepartureProviderTest extends ProviderTestCase2<DeparturesProvider> {

    private MockContentResolver mockResolver;

    public DepartureProviderTest() {
        super(DeparturesProvider.class, DeparturesContract.AUTHORITY);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        Context context = InstrumentationRegistry.getTargetContext();
        setContext(context);
        mockResolver = getMockContentResolver();
    }

    @Test
    public void testStationQuery(){
        Cursor c = mockResolver.query(DeparturesContract.STATIONS_URI, null, null, null, null);
        assertEquals(3, c.getColumnCount());
    }

    @Test
    public void testDepartureDataQuery() {
        Cursor c = mockResolver.query(DeparturesContract.DEPARTURES_URI, null,null, new String[] {"KE"}, null);
        assertEquals(6, c.getColumnCount());
        assertEquals(10, c.getCount());
    }


}
