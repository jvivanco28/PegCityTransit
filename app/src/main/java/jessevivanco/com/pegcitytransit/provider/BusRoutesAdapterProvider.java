package jessevivanco.com.pegcitytransit.provider;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import jessevivanco.com.pegcitytransit.dagger.components.AppComponent;
import jessevivanco.com.pegcitytransit.provider.base.AdapterProvider;
import jessevivanco.com.pegcitytransit.repositories.BusRoutesRepository;
import jessevivanco.com.pegcitytransit.repositories.OnRepositoryDataRetrievedListener;
import jessevivanco.com.pegcitytransit.rest.RetrofitResponseUtils;
import jessevivanco.com.pegcitytransit.rest.models.BusRoute;

public class BusRoutesAdapterProvider implements AdapterProvider<List<BusRoute>> {

    private static final String STATE_KEY_BUS_STOP = "STATE_KEY_BUS_STOP";

    private final String LOG_TAG = getClass().getSimpleName();

    @Inject
    BusRoutesRepository busRoutesRepository;
    @Inject
    Context context;

    private
    @Nullable
    Integer busStop;

    public BusRoutesAdapterProvider(AppComponent injector) {
        injector.injectInto(this);
    }

    @Override
    public void loadData(OnRepositoryDataRetrievedListener<List<BusRoute>> onDataRetrievedCallback) {
        busRoutesRepository.getRoutesForBusStop(busStop)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((busRoutes, throwable) -> {
                    RetrofitResponseUtils.handleResponse(context, busRoutes, LOG_TAG, throwable, onDataRetrievedCallback);
                });
    }

    @Override
    public Bundle onSaveInstanceState(Bundle outState) {
        if (outState != null && busStop != null) {
            outState.putInt(STATE_KEY_BUS_STOP, busStop);
        }
        return outState;
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle state) {
        if (state != null && state.containsKey(STATE_KEY_BUS_STOP)) {
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
