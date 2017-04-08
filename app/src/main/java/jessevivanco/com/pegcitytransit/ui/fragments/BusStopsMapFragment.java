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
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.rest.models.BusStop;
import jessevivanco.com.pegcitytransit.ui.adapters.BusStopsAdapter;
import jessevivanco.com.pegcitytransit.ui.fragments.base.BaseFragment;
import jessevivanco.com.pegcitytransit.ui.presenters.BusStopsPresenter;

public class BusStopsMapFragment extends BaseFragment implements OnMapReadyCallback, BusStopsPresenter.ViewContract {

    @BindView(R.id.root_container)
    ViewGroup rootContainer;

    @BindView(R.id.bus_stops_recycler_view)
    RecyclerView busStopsRecyclerView;

    private BusStopsAdapter busStopsAdapter;
    private BusStopsPresenter busStopsPresenter;

    private SupportMapFragment mapFragment;

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

        busStopsPresenter.loadBusStops(null, null, null);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_bus_stops_map;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        // Default marker if we don't have a location
        LatLng downtownWinnipeg = new LatLng(Double.valueOf(getString(R.string.default_lat)), Double.valueOf(getString(R.string.default_long)));
        googleMap.addMarker(new MarkerOptions().position(downtownWinnipeg));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(downtownWinnipeg));
    }

    private void setupMap() {
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
    }

    protected void setupAdapter() {
        busStopsPresenter = new BusStopsPresenter(getInjector(), this);
        busStopsAdapter = new BusStopsAdapter(busStopsPresenter);
    }

    private void setupRecyclerView() {
        busStopsRecyclerView.setHasFixedSize(true);
        busStopsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        busStopsRecyclerView.setAdapter(busStopsAdapter);
    }

    @Override
    public void showBusStops(List<BusStop> busStops) {
        busStopsAdapter.setBusStops(busStops);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(rootContainer, message, Snackbar.LENGTH_LONG).show();
    }
}
