package jessevivanco.com.pegcitytransit.ui.provider;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.List;

import javax.inject.Inject;

import jessevivanco.com.pegcitytransit.PegCityTransitApp;
import jessevivanco.com.pegcitytransit.dagger.components.AppComponent;
import jessevivanco.com.pegcitytransit.repositories.BusRoutesRepository;
import jessevivanco.com.pegcitytransit.repositories.OnDataRetrievedCallback;
import jessevivanco.com.pegcitytransit.rest.models.BusRoute;

public class BusRoutesProvider implements Provider<List<BusRoute>> {

    private static final String STATE_KEY_BUS_STOP = "STATE_KEY_BUS_STOP";

    @Inject
    BusRoutesRepository busRoutesRepository;

    private
    @Nullable
    Integer busStop;

    public BusRoutesProvider(AppComponent injector) {
        injector.injectFields(this);
    }

    @Override
    public void loadData(OnDataRetrievedCallback<List<BusRoute>> onDataRetrievedCallback) {
        busRoutesRepository.getBusStopsNearLocation(busStop, onDataRetrievedCallback);
    }

    @Override
    public Bundle onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putInt(STATE_KEY_BUS_STOP, busStop);
        }
        return outState;
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle state) {
        if (state != null) {
            busStop = state.getInt(STATE_KEY_BUS_STOP);
        }
    }

    @Nullable
    public Integer getBusStop() {
        return busStop;
    }

    public void setBusStop(@Nullable Integer busStop) {
        this.busStop = busStop;
    }
}
