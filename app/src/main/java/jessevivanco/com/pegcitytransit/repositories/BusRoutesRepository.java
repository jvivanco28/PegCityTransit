package jessevivanco.com.pegcitytransit.repositories;

import java.util.List;

import io.reactivex.Single;
import jessevivanco.com.pegcitytransit.rest.RestApi;
import jessevivanco.com.pegcitytransit.rest.models.BusRoute;
import jessevivanco.com.pegcitytransit.rest.models.base.WinnipegTransitResponse;

public class BusRoutesRepository {

    private RestApi restApi;

    public BusRoutesRepository(RestApi restApi) {
        this.restApi = restApi;
    }

    public Single<List<BusRoute>> getRoutesForBusStop(Integer busStop) {
        return restApi.getRoutesForBusStop(busStop)
                .map(WinnipegTransitResponse::getElement);
    }
}
