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

    public Single<List<BusRoute>> getRoutesForBusStop(Integer busStop) {
        return restApi.getRoutesForBusStop(busStop)
                .subscribeOn(Schedulers.io())
                .map(listWinnipegTransitResponse -> listWinnipegTransitResponse.getElement());
    }

//    /**
//     * Query the database for bus routes the stop at the given <code>busStop</code> number.
//     *
//     * @param busStop
//     * @return
//     */
//    private Observable<RealmResults<BusRoute>> queryRoutes(Integer busStop) {
//        return Observable.fromCallable(() -> realm.where(BusRoute.class)
////                .equalTo("number", busStop)
//                .findAllAsync())
//                // We need to access realm from the main thread.
//                .subscribeOn(AndroidSchedulers.mainThread());
//    }
//
//    private Single<List<BusRoute>> fetchRoutesForBusStop(Integer busStop) {
//        return restApi.getRoutesForBusStop(busStop)
//                .subscribeOn(Schedulers.io())
//                .map(listWinnipegTransitResponse -> listWinnipegTransitResponse.getElement());
//    }
//
//    private Completable saveToDatabase(List<BusRoute> busRoutes) {
//        return Completable.fromAction(() -> {
//
//            Log.v("DEBUG", "saving new bus routes " + busRoutes);
//            realm.beginTransaction();
//            realm.copyToRealmOrUpdate(busRoutes);
//            realm.commitTransaction();
//
//            // All database actions need to be done on the main thread.
//        }).subscribeOn(AndroidSchedulers.mainThread());
//    }

//    public Observable<RealmResults<BusRoute>> getRoutesForBusStop(Integer busStop) {
//
//        Log.v("DEBUG", "about to query routes for stop " + busStop);
//        return queryRoutes(busStop)
//                .flatMap(busRoutes -> {
//                    Log.v("DEBUG", "from query: " + busRoutes);
//
//                    // If data is stale, then refresh from the API.
//                    if (busRoutes == null || busRoutes.isEmpty()) {
//
//                        Log.v("DEBUG", "About to refresh from API");
//                        return fetchRoutesForBusStop(busStop)
//                                .flatMapCompletable(newBusRoutes -> saveToDatabase(newBusRoutes))
//                                .andThen(queryRoutes(busStop));
//
//                    } else {
//                        Log.v("DEBUG", "Returning queried data only.");
//
//                        // Else just return the queried data.
//                        return Observable.just(busRoutes);
//                    }
//                });
//    }
}
