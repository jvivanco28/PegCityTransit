package jessevivanco.com.pegcitytransit.data.repositories;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import jessevivanco.com.pegcitytransit.data.rest.RestApi;
import jessevivanco.com.pegcitytransit.data.rest.models.BusRoute;
import jessevivanco.com.pegcitytransit.data.rest.models.BusStop;
import jessevivanco.com.pegcitytransit.data.rest.models.base.WinnipegTransitResponse;

public class BusStopRepository {

    private RestApi restApi;

    public BusStopRepository(RestApi restApi) {
        this.restApi = restApi;
    }

    public Single<RealmResults<BusStop>> getBusStopsNearLocation(Double latitude,
                                                                 Double longitude,
                                                                 @Nullable Integer radius) {
        // Fetch the results from the API first
        return fetchBusStopsNearLocation(latitude, longitude, radius)
                // Then save the results to the database.
                .flatMap(busStops -> saveToDatabase(busStops)
                        // Query what we just saved, and return the query results. This should just
                        // be the same as what we fetched from the API with the possibility of
                        // a list of bus routes as additional info.
                        .andThen(queryBusStop(getKeysFromList(busStops))));
    }

    /**
     * Saves a bus stop with its list of routes to the database.
     *
     * @param busStop
     * @param routes
     * @return
     */
    public Completable saveBusStopWithRoutes(BusStop busStop, List<BusRoute> routes) {
        return Completable.fromAction(() -> {
            Log.v("DEBUG", "saving ROUTES for bus stop");
            busStop.setBusRoutes(new RealmList<>(routes.toArray(new BusRoute[routes.size()])));
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Takes a list of bus stops and returns a list of keys for each bus stop.
     *
     * @param busStops
     * @return
     */
    private Long[] getKeysFromList(List<BusStop> busStops) {
        List<Long> keys = new ArrayList<>();
        for (BusStop stop : busStops) {
            keys.add(stop.getKey());
        }
        return keys.toArray(new Long[keys.size()]);
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

    private Completable saveToDatabase(List<BusStop> busStops) {
        return Completable.fromAction(() -> {

            // TODO doAsync?
            Realm realm = Realm.getDefaultInstance();

            Log.v("DEBUG", "saving bus stops " + busStops);
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(busStops);
            realm.commitTransaction();

            // All database actions need to be done on the main thread.
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    private Single<RealmResults<BusStop>> queryBusStop(Long[] keys) {
        return Single.fromCallable(() -> {
            Log.v("DEBUG", "querying busStops");
            Realm realm = Realm.getDefaultInstance();

            return realm.where(BusStop.class)
                    .in("key", keys)
                    .findAll();
        }).subscribeOn(AndroidSchedulers.mainThread());
    }
}
