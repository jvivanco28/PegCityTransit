package jessevivanco.com.pegcitytransit.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import org.parceler.Parcels;

import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.activities.base.BaseActivity;
import jessevivanco.com.pegcitytransit.ui.fragments.TransitMapFragment;

public class BusRouteMapActivity extends BaseActivity implements TransitMapFragment.OnMapReadyListener {

    public static final String ARG_KEY_BUS_ROUTE = "route";

    private TransitMapFragment transitMapFragment;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getInjector().injectInto(this);

        if (savedInstanceState == null) {
            Log.v("DEBUG", "111");

            transitMapFragment = TransitMapFragment.newInstance(this);
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, transitMapFragment).commit();
        } else {
            Log.v("DEBUG", "222");

            transitMapFragment = (TransitMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            transitMapFragment.setMapReadyListener(this);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_bus_route_map;
    }


    @Override
    public void onMapReady() {
        // TODO need to handle orientation changes
        transitMapFragment.loadBusStopsForBusRoute(Parcels.unwrap(getIntent().getParcelableExtra(ARG_KEY_BUS_ROUTE)));
    }
}
