package jessevivanco.com.pegcitytransit.ui.presenters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.squareup.phrase.Phrase;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.dagger.components.AppComponent;
import jessevivanco.com.pegcitytransit.data.repositories.BusRoutesRepository;
import jessevivanco.com.pegcitytransit.data.repositories.BusStopRepository;
import jessevivanco.com.pegcitytransit.data.repositories.PreferencesRepository;
import jessevivanco.com.pegcitytransit.data.rest.RestApi;
import jessevivanco.com.pegcitytransit.data.util.DisposableUtil;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;

public class TransmitMapPresenter {

    private static final String TAG = TransmitMapPresenter.class.getSimpleName();

    private static final String SERVICE_ADVISORY_CODE_BLUE = "blue";
    private static final String SERVICE_ADVISORY_CODE_RED = "red";

    /**
     * Default lat and long will only be used if the user has GPS disabled.
     */
    private final double DEFAULT_LAT;
    private final double DEFAULT_LONG;
    private final long SEARCH_AREA_MARKER_DELAY_MILLIS;

    @Inject
    BusStopRepository stopsRepository;
    @Inject
    BusRoutesRepository routesRepository;
    @Inject
    PreferencesRepository preferencesRepository;
    @Inject
    RestApi restApi;
    @Inject
    Context context;

    private ViewContract viewContract;

    private Disposable serviceAdvisorySubscription;
    private Disposable subscription;

    public TransmitMapPresenter(AppComponent injector, ViewContract viewContract) {
        injector.injectInto(this);

        this.viewContract = viewContract;

        // Load out default lat and long values.
        DEFAULT_LAT = Double.parseDouble(context.getString(R.string.downtown_winnipeg_latitude));
        DEFAULT_LONG = Double.parseDouble(context.getString(R.string.downtown_winnipeg_longitude));
        SEARCH_AREA_MARKER_DELAY_MILLIS = context.getResources().getInteger(R.integer.search_area_marker_delay_millis);
    }

    public void checkServiceAdvisories() {

        DisposableUtil.dispose(serviceAdvisorySubscription);

        serviceAdvisorySubscription = restApi.getScheduleStatus()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        scheduleStatus -> {
                            String statusCode = scheduleStatus.getStatus().getValue();

                            // TODO Shoud we show the message from the status?
                            if (statusCode.equals(SERVICE_ADVISORY_CODE_BLUE) || statusCode.equals(SERVICE_ADVISORY_CODE_RED)) {
                                viewContract.showServiceAdvisoryWarningDialog(context.getString(R.string.warning),
                                        Phrase.from(context.getString(R.string.service_advisory_warning)).put("code", statusCode).format().toString());
                            }
                        }, throwable -> {
                            Log.e(TAG, "An error occurred while checking service advisory.", throwable);
                        }
                );
    }

    public void loadBusStopsAroundCoordinates(@Nullable Double latitude, @Nullable Double longitude) {

        DisposableUtil.dispose(subscription);

        int mapSearchRadius = preferencesRepository.getMapSearchRadius();

        // NOTE: If lat, long, and radius are not supplied, then we just resort to the default values.
        subscription = stopsRepository.getBusStopsNearLocation(
                latitude != null ? latitude : DEFAULT_LAT,
                longitude != null ? longitude : DEFAULT_LONG,
                mapSearchRadius)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    viewContract.clearMarkers();
                    // Imeediately focus on the drawn circle.
                    viewContract.showSearchRadius(latitude, longitude, mapSearchRadius, true);
                    viewContract.showStopsLoadingIndicator(true);
                })
                .doFinally(() -> viewContract.showStopsLoadingIndicator(false))
                .subscribe(
                        busStops -> {
                            if (busStops != null && busStops.size() > 0) {
                                // Don't need to focus on the area a second time. Just show the markers.
                                viewContract.showBusStops(busStops, SEARCH_AREA_MARKER_DELAY_MILLIS, false);
                            } else {
                                viewContract.showErrorMessage(context.getString(R.string.no_bus_stops_in_that_area));
                            }
                        },
                        throwable -> {
                            Log.e(TAG, "An error occurred while searching bus stops.", throwable);
                            viewContract.showErrorMessage(context.getString(R.string.error_loading_bus_stops));
                        }
                );
    }

    public void loadBusRoutesForStop(@NonNull BusStopViewModel busStopFilter) {
        DisposableUtil.dispose(subscription);

        subscription = routesRepository.getRoutesForBusStop(busStopFilter.getKey())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        busRoutes -> {
                            busStopFilter.setRoutes(busRoutes);
                            viewContract.showBusRoutesForStop(busStopFilter);
                        },
                        throwable -> {
                            Log.e(TAG, "An error occurred while loading the bus routes", throwable);
                            viewContract.showErrorMessage(context.getString(R.string.error_loading_bus_routes));
                        }
                );
    }

    public void loadBusStopsForBusRoute(@NonNull BusRouteViewModel route) {
        DisposableUtil.dispose(subscription);

        subscription = stopsRepository.getBusStopsForRoute(route.getKey())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    viewContract.clearMarkers();
                    viewContract.clearSearchRadius();
                    viewContract.showRouteLoadingIndicator(true);
                })
                .doFinally(() -> viewContract.showRouteLoadingIndicator(false))
                .subscribe(
                        busStops -> viewContract.showBusStops(busStops, 0, true),
                        throwable -> {
                            Log.e(TAG, "Error loading bus stops for bus route", throwable);
                            viewContract.showErrorMessage(Phrase.from(context, R.string.error_loading_route).put("route", route.getNumber()).format().toString());
                        }
                );
    }

    public void loadSavedBusStops() {
        DisposableUtil.dispose(subscription);

        subscription = stopsRepository.getSavedBusStops()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    viewContract.clearMarkers();
                    viewContract.clearSearchRadius();
                })
                .subscribe(
                        busStops -> {
                            if (busStops != null && busStops.size() > 0) {
                                viewContract.showBusStops(busStops, SEARCH_AREA_MARKER_DELAY_MILLIS, true);
                            } else {
                                viewContract.showErrorMessage(context.getString(R.string.no_saved_stops));
                            }
                        },
                        throwable -> {
                            // No REST calls for this, so maybe an I/O exception.
                            Crashlytics.logException(throwable);
                            Log.e(TAG, "Error loading saved bus stops", throwable);
                            viewContract.showErrorMessage(context.getString(R.string.error_loading_saved_stops));
                        }
                );
    }

    /**
     * Kills all active subscriptions.
     */
    public void tearDown() {
        DisposableUtil.dispose(subscription);
        DisposableUtil.dispose(serviceAdvisorySubscription);
    }

    public interface ViewContract extends ErrorMessageViewContract {

        void showServiceAdvisoryWarningDialog(String title, String message);

        void showBusStops(List<BusStopViewModel> busStops, long delayMarkerVisibilityMillis, boolean focusInMap);

        void showStopsLoadingIndicator(boolean visible);

        void showRouteLoadingIndicator(boolean visible);

        void showBusRoutesForStop(BusStopViewModel busStop);

        void showSearchRadius(Double latitude, Double longitude, Integer searchRadius, boolean focusInMap);

        void clearMarkers();

        void clearSearchRadius();
    }
}