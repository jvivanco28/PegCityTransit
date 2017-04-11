package jessevivanco.com.pegcitytransit.data.repositories;

import android.support.annotation.Nullable;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import jessevivanco.com.pegcitytransit.data.rest.RestApi;
import jessevivanco.com.pegcitytransit.data.rest.models.base.WinnipegTransitResponse;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;

public class BusStopRepository {

    private RestApi restApi;

    public BusStopRepository(RestApi restApi) {
        this.restApi = restApi;
    }

    public Single<List<BusStopViewModel>> getBusStopsNearLocation(Double latitude,
                                                                  Double longitude,
                                                                  @Nullable Integer radius) {

        return restApi.getBusStopsNearLocation(latitude, longitude, radius)
                .subscribeOn(Schedulers.io())
                .map(WinnipegTransitResponse::getElement)
                .flatMapObservable(Observable::fromIterable)
                .map(BusStopViewModel::createFromBusStop)
                .toList();

        // Notes for myself:
        // - Method references are the same as this:
        // .map(busStopsList -> busStopsList.getElement();
        // - the map() function just converts the previous return type to a new type.
    }

    public Single<List<BusStopViewModel>> getBusStopsForRoute(Long routeKey) {

        return restApi.getBusStopsForRoute(routeKey)
                .map(WinnipegTransitResponse::getElement)
                .flatMapObservable(Observable::fromIterable)
                .map(BusStopViewModel::createFromBusStop)
                .toList();
    }
}
