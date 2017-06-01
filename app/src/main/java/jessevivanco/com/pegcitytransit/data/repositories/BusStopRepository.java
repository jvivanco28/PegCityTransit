package jessevivanco.com.pegcitytransit.data.repositories;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.util.LongSparseArray;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.reflect.TypeToken;
import com.iainconnor.objectcache.CacheManager;
import com.squareup.phrase.Phrase;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.rest.RestApi;
import jessevivanco.com.pegcitytransit.data.rest.models.base.WinnipegTransitResponse;
import jessevivanco.com.pegcitytransit.data.util.CacheUtil;
import jessevivanco.com.pegcitytransit.data.util.ListUtil;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;

public class BusStopRepository {

    private static final String TAG = BusStopRepository.class.getSimpleName();

    // Routes change from time to time.
    private static final int CACHE_EXPIRY = CacheManager.ExpiryTimes.ONE_DAY.asSeconds();
    private static final String CACHE_KEY_SAVED_STOPS = "saved_stops";

    private Context context;
    private RestApi restApi;
    @Nullable
    private CacheManager cacheManager;
    private final Type busStopsTypeToken;

    // This only exists so that we can quickly lookup which stops are saved by their keys.
    private LongSparseArray<BusStopViewModel> savedStopsCacheMap;

    public BusStopRepository(Context context, RestApi restApi, @Nullable CacheManager cacheManager) {
        this.context = context;
        this.restApi = restApi;
        this.cacheManager = cacheManager;
        this.busStopsTypeToken = new TypeToken<List<BusStopViewModel>>() {
        }.getType();

        loadSavedStopsCacheMap();
    }

    private void loadSavedStopsCacheMap() {
        getSavedBusStops()
                .map(this::listToLongSparseArray)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        busStopViewModelLongSparseArray -> this.savedStopsCacheMap = busStopViewModelLongSparseArray,
                        throwable -> {
                            Crashlytics.logException(throwable);
                            Log.e(TAG, "Error loading saved stops from cache.", throwable);
                        }
                );
    }

    /**
     * Converts a list of bus stops to a {@link LongSparseArray} with the intention of being able
     * to quickly lookup a bus stop by its key.
     * <p>
     * NOTE: I would have just cached a {@link LongSparseArray} of {@link BusStopViewModel} types
     * but GSON was having some issues deserializing. This is just a workaround.
     */
    private LongSparseArray<BusStopViewModel> listToLongSparseArray(List<BusStopViewModel> list) {
        LongSparseArray<BusStopViewModel> sparseArray = new LongSparseArray<>();

        if (list != null) {
            for (BusStopViewModel element : list) {
                sparseArray.put(element.getKey(), element);
            }
        }
        return sparseArray;
    }

    private boolean isBusStopSaved(Long key) {
        return savedStopsCacheMap.get(key) != null;
    }

    public Single<List<BusStopViewModel>> getBusStopsNearLocation(Double latitude,
                                                                  Double longitude,
                                                                  @Nullable Integer radius) {

        return restApi.getBusStopsNearLocation(latitude, longitude, radius)
                .map(WinnipegTransitResponse::getElement)
                .flatMapObservable(Observable::fromIterable)
                // Create the view model. We're also setting a flag which tells us if the bus stop saved under "my stops".
                .map(busStop -> BusStopViewModel.createFromBusStop(busStop, isBusStopSaved(busStop.getKey())))
                .toList();

        // Notes for myself:
        // - Method references are the same as this:
        // .map(busStopsList -> busStopsList.getElement();
        // - the map() function just converts the previous return type to a new type.
    }

    public Single<List<BusStopViewModel>> getBusStopsForRoute(Long routeKey) {

        return Single.defer(() -> {

            final String CACHE_KEY = Phrase.from(context, R.string.cache_key_stops_for_route).put("route", String.valueOf(routeKey)).format().toString();

            // Grab the cached list of bus stops.
            List<BusStopViewModel> cachedBusStops = CacheUtil.getFromCache(cacheManager,
                    CACHE_KEY,
                    busStopsTypeToken);

            if (cachedBusStops != null) {
                return Single.just(cachedBusStops);
            } else {
                return restApi.getBusStopsForRoute(routeKey)
                        .map(WinnipegTransitResponse::getElement)
                        .flatMapObservable(Observable::fromIterable)
                        // Create the view model. We're also setting a flag which tells us if the bus stop saved under "my stops".
                        .map(busStop -> BusStopViewModel.createFromBusStop(busStop, isBusStopSaved(busStop.getKey())))
                        .toList()
                        .doOnSuccess(busStopViewModels -> {
                            if (cacheManager != null) {
                                cacheManager.put(CACHE_KEY, busStopViewModels, CACHE_EXPIRY, false);
                            }
                        });
            }
        });
    }

    public Single<List<BusStopViewModel>> getSavedBusStops() {
        return Single.defer(() -> {

            // If already loaded, then we don't need to load from ObjectCache.
            if (savedStopsCacheMap != null) {
                return Single.just(ListUtil.asList(savedStopsCacheMap));

            } else {
                List<BusStopViewModel> savedBusStops = CacheUtil.getFromCache(cacheManager,
                        CACHE_KEY_SAVED_STOPS,
                        busStopsTypeToken);

                return savedBusStops != null ?
                        Single.just(savedBusStops) :
                        // We can't return null, so just return an empty HashMap if we don't have any saved stops.
                        Single.just(new ArrayList<>());
            }
        });
    }

    public Single<List<BusStopViewModel>> saveBusStop(BusStopViewModel busStop) {

        return Single.defer(() -> {
            if (busStop == null) {
                throw new IllegalArgumentException("Can not add a null bus stop to cache.");
            }
            // Don't allow routes to be saved for bus stops. Since there is no expiry
            // time for saved stops, we need to be able to refresh the bus routes for
            // each bus stop.
            busStop.setRoutes(null);
            busStop.setSavedStop(true);

            savedStopsCacheMap.put(busStop.getKey(), busStop);
            return Single.just(ListUtil.asList(savedStopsCacheMap));

        }).doOnSuccess(busStopViewModels -> {
            if (cacheManager != null) {
                // No expiry on saved stops
                cacheManager.put(CACHE_KEY_SAVED_STOPS, busStopViewModels);
            }
        });
    }

    public Single<List<BusStopViewModel>> removeSavedStop(BusStopViewModel busStop) {

        return Single.defer(() -> {
            if (busStop == null) {
                throw new IllegalArgumentException("Can not remove a null bus stop from cache.");
            }
            // Remove the bus stop.
            savedStopsCacheMap.delete(busStop.getKey());
            // Return the new list of saved stops.
            return Single.just(ListUtil.asList(savedStopsCacheMap));
        }).doOnSuccess(busStopViewModels -> {
            if (cacheManager != null) {
                // No expiry on saved stops
                cacheManager.put(CACHE_KEY_SAVED_STOPS, busStopViewModels);
            }
        });
    }

    public Single<List<BusStopViewModel>> searchBusStops(String query) {
        return restApi.searchBusStops(Phrase.from(context.getString(R.string.search_stops_url)).put("query", query).format().toString())
                .map(WinnipegTransitResponse::getElement)
                .flatMapObservable(Observable::fromIterable)
                // Create the view model. We're also setting a flag which tells us if the bus stop saved under "my stops".
                .map(busStop -> BusStopViewModel.createFromBusStop(busStop, isBusStopSaved(busStop.getKey())))
                .toList();
    }
}
