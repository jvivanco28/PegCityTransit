package jessevivanco.com.pegcitytransit.data.repositories;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import jessevivanco.com.pegcitytransit.data.rest.RestApi;
import jessevivanco.com.pegcitytransit.data.rest.models.BusRoute;


public class BusRoutesRepository {

    private RestApi restApi;

    public BusRoutesRepository(RestApi restApi) {
        this.restApi = restApi;
    }

    // TODO We for sure need a way to cache this
    public Single<List<BusRoute>> getRoutesForBusStop(Long busStopKey) {
        return restApi.getRoutesForBusStop(busStopKey)
                .subscribeOn(Schedulers.io())
                .map(listWinnipegTransitResponse -> listWinnipegTransitResponse.getElement());
    }
}
