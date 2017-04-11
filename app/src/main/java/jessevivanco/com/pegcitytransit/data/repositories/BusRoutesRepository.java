package jessevivanco.com.pegcitytransit.data.repositories;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import jessevivanco.com.pegcitytransit.data.rest.RestApi;
import jessevivanco.com.pegcitytransit.data.rest.models.base.WinnipegTransitResponse;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;


public class BusRoutesRepository {

    private RestApi restApi;

    public BusRoutesRepository(RestApi restApi) {
        this.restApi = restApi;
    }

    // TODO We for sure need a way to cache these results
    public Single<List<BusRouteViewModel>> getRoutesForBusStop(Long busStopKey) {
        return restApi.getRoutesForBusStop(busStopKey)
                .map(WinnipegTransitResponse::getElement)
                .flatMapObservable(Observable::fromIterable)
                .map(BusRouteViewModel::createFromBusRoute)
                .toList();
    }
}
