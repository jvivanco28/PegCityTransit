package jessevivanco.com.pegcitytransit.data.repositories;

import android.support.annotation.Nullable;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import jessevivanco.com.pegcitytransit.data.rest.RestApi;
import jessevivanco.com.pegcitytransit.data.rest.models.BusStop;
import jessevivanco.com.pegcitytransit.data.rest.models.base.WinnipegTransitResponse;

public class BusStopRepository {

    private RestApi restApi;

    public BusStopRepository(RestApi restApi) {
        this.restApi = restApi;
    }

    public Single<List<BusStop>> getBusStopsNearLocation(Double latitude,
                                                         Double longitude,
                                                         @Nullable Integer radius) {
        // Fetch the results from the API first
        return fetchBusStopsNearLocation(latitude, longitude, radius);
    }

    private Single<List<BusStop>> fetchBusStopsNearLocation(Double latitude,
                                                            Double longitude,
                                                            @Nullable Integer radius) {
        return restApi.getBusStopsNearLocation(latitude, longitude, radius)
                .subscribeOn(Schedulers.io())
                .map(WinnipegTransitResponse::getElement);
        // ^FYI method reference above is the same as this
        // .map(busStopsList -> busStopsList.getElement();
        // ^FYI 2, the map() function just converts the previous return type to a new type.
        // In this case, we're converting from type BusStopList to type List<BusStop>.
    }
}
