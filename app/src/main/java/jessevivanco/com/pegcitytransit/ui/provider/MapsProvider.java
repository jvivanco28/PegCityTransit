package jessevivanco.com.pegcitytransit.ui.provider;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.phrase.Phrase;

import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.contracts.MapsProviderViewContract;
import jessevivanco.com.pegcitytransit.ui.util.IntentRequestCodes;

public class MapsProvider implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final int MAP_ZOOM_RATIO;

    private Context context;
    private GoogleApiClient googleApiClient;

    private BusStopsProvider busStopsProvider;

    private MapsProviderViewContract view;

    public MapsProvider(Context context,
                        BusStopsProvider busStopsProvider,
                        MapsProviderViewContract view) {

        MAP_ZOOM_RATIO = context.getResources().getInteger(R.integer.default_map_zoom);

        this.context = context;
        this.busStopsProvider = busStopsProvider;
        this.view = view;

        setupGoogleApiClient(context);
    }


    private void setupGoogleApiClient(Context context) {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    /**
     * Builds the URL for displaying the user's location with Google's static maps.
     *
     * @param latitude
     * @param longitude
     * @return
     */
    private String buildStaticMapUrl(Double latitude, Double longitude) {

        Resources res = context.getResources();

        return Phrase.from(res, R.string.static_map_url)
                .put("center", latitude + "," + longitude)
                .put("zoom", MAP_ZOOM_RATIO)
                .put("size", Resources.getSystem().getDisplayMetrics().widthPixels + "x" + res
                        .getDimensionPixelSize(R.dimen.default_app_bar_height))
                .put("markers", "color:blue|size:tiny|" + latitude + "," + longitude)
                .put("key", context.getString(R.string.google_maps_key))
                .format().toString();
    }


    /**
     * Starts running the <code>GoogleApiClient</code> which is used for determining the user's location.
     */
    public void start() {
        if (googleApiClient != null && (!googleApiClient.isConnected() && !googleApiClient.isConnecting())) {
            googleApiClient.connect();
        }
    }

    /**
     * Stops running the <code>GoogleApiClient</code> which is used for determining the user's location.
     */
    public void stop() {
        if (googleApiClient != null && (googleApiClient.isConnected() || googleApiClient.isConnecting())) {
            googleApiClient.disconnect();
        }
    }

    /**
     * Initiates location services to find the user's current location. If the permission has not yet been granted,
     * then we'll ask for permission.
     */
    public void getUserLocation() {

        // If permission has not yet been granted, then ask the user.
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission to access the location is missing.
            view.onRequestPermission(IntentRequestCodes.LOCATION_PERMISSION_REQUEST_CODE.ordinal(),
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    context.getString(R.string.location_permission_dialog_title),
                    context.getString(R.string.location_permission_rational));

        } else {

            // TODO zoom? google api client connected?

            // Permission was granted. Get the user's location.
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            // If the new location is different than the last known location, then refresh the list.
            if (lastLocation != null && busStopsProvider.isDifferentLocation(lastLocation.getLatitude(), lastLocation.getLongitude())) {

                // Set the user's location and then refresh the list.
                busStopsProvider.setLatitude(lastLocation.getLatitude());
                busStopsProvider.setLongitude(lastLocation.getLongitude());

                view.refreshList();

            }

            view.setStaticMapCoverImage(buildStaticMapUrl(busStopsProvider.getLatitude(), busStopsProvider.getLongitude()));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        // GPS connected. Find the user's location.
        getUserLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Do nothing.
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Do nothing.
    }
}
