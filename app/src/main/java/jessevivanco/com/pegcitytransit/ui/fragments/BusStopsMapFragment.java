package jessevivanco.com.pegcitytransit.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.rest.models.BusStop;
import jessevivanco.com.pegcitytransit.ui.adapters.BusStopInfoWindowAdapter;
import jessevivanco.com.pegcitytransit.ui.adapters.BusStopsAdapter;
import jessevivanco.com.pegcitytransit.ui.fragments.base.BaseFragment;
import jessevivanco.com.pegcitytransit.ui.item_decorations.BusStopListItemDecoration;
import jessevivanco.com.pegcitytransit.ui.presenters.BusStopsPresenter;

public class BusStopsMapFragment extends BaseFragment implements OnMapReadyCallback, BusStopsPresenter.ViewContract {

    @BindView(R.id.root_container)
    ViewGroup rootContainer;

    @BindView(R.id.bus_stops_recycler_view)
    RecyclerView busStopsRecyclerView;

    private BusStopsAdapter busStopsAdapter;
    private BusStopsPresenter busStopsPresenter;

    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private BusStopInfoWindowAdapter busStopInfoWindowAdapter;

    public static BusStopsMapFragment newInstance() {
        return new BusStopsMapFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setupMap();
        setupAdapter();
        setupRecyclerView();

        // TODO get your coordinates
        busStopsPresenter.loadBusStops(null, null, null);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_bus_stops_map;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        this.googleMap = googleMap;

        // TODO get real GPS coordinates
        // TODO display your location with a circle or something.
        // Default marker if we don't have a location
        LatLng downtownWinnipeg = new LatLng(Double.valueOf(getString(R.string.default_lat)), Double.valueOf(getString(R.string.default_long)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(downtownWinnipeg, getResources().getInteger(R.integer.default_map_zoom)));

        googleMap.setInfoWindowAdapter(busStopInfoWindowAdapter);
    }

    private void setupMap() {
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        busStopInfoWindowAdapter = new BusStopInfoWindowAdapter(getActivity(), getInjector());
    }

    protected void setupAdapter() {
        busStopsPresenter = new BusStopsPresenter(getInjector(), this);
        busStopsAdapter = new BusStopsAdapter(busStopsPresenter);
    }

    private void setupRecyclerView() {
        busStopsRecyclerView.setHasFixedSize(true);
        busStopsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        busStopsRecyclerView.addItemDecoration(new BusStopListItemDecoration(getActivity()));
        busStopsRecyclerView.setAdapter(busStopsAdapter);
    }

    @Override
    public void showBusStops(List<BusStop> busStops) {

        // Display the list of bus stops in the RecyclerView
        busStopsAdapter.setBusStops(busStops);

        // Display each bus stop in the map with their GPS coordinates. Also keep a HashMap of
        // markers for each bus stop so we can figure out which marker points to which bus stop.
        HashMap<Marker, BusStop> markerToKeyHashMap = new HashMap<>();

        for (BusStop stop : busStops) {
            LatLng latLng = stop.getCentre().getGeographic().getLatLng();
            if (latLng != null) {
                Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng)
                        .title(String.valueOf(stop.getKey()))
                        .snippet(stop.getName())
                );
                markerToKeyHashMap.put(marker, stop);
            }
        }
        busStopInfoWindowAdapter.setMarkerToBusStopHashMap(markerToKeyHashMap);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(rootContainer, message, Snackbar.LENGTH_LONG).show();
    }
}
