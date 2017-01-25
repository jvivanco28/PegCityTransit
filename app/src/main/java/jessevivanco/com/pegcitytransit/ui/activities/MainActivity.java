package jessevivanco.com.pegcitytransit.ui.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.phrase.Phrase;
import com.squareup.picasso.Picasso;

import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.activities.base.BaseActivity;
import jessevivanco.com.pegcitytransit.ui.adapters.BusStopsAdapter;
import jessevivanco.com.pegcitytransit.ui.adapters.base.BaseAdapter;
import jessevivanco.com.pegcitytransit.ui.fragments.FragmentUtils;
import jessevivanco.com.pegcitytransit.ui.fragments.PermissionDeniedDialog;
import jessevivanco.com.pegcitytransit.ui.item_decorations.DefaultListItemDecorator;
import jessevivanco.com.pegcitytransit.ui.provider.BusStopsProvider;
import jessevivanco.com.pegcitytransit.ui.util.IntentRequestCodes;
import jessevivanco.com.pegcitytransit.ui.util.PermissionUtils;
import jessevivanco.com.pegcitytransit.ui.util.SnackbarUtils;

public class MainActivity
        extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BaseAdapter.OnListLoadedCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,

        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String PERMISSION_DIALOG_TAG = "dialog";

    public static final int MAP_ZOOM_RATIO = 15;

    protected Toolbar toolbar;
    protected ImageView staticMapCoverImage;
    protected FloatingActionButton fab;
    protected DrawerLayout drawer;
    protected NavigationView navigationView;
    protected SwipeRefreshLayout refreshLayout;
    protected RecyclerView recyclerView;

    private BusStopsProvider busStopsProvider;
    private BusStopsAdapter busStopsAdapter;

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupGoogleApiClient();

        // Setup UI elements.
        setupToolbar();
        setupCoverImage();
        setupFab();
        setupDrawer();
        setupNav();
        setupRefresh();
        setupAdapter(savedInstanceState);
        setupRecyclerView();

//        // TODO. MOVE THIS. don't do this until we know if we can use the user's location.
//        // If the restored list is empty, then we'll need to fetch the results.
//        if (busStopsAdapter.getDataCount() == 0) {
//            startFromScratch();
//        }
    }

    protected void setupGoogleApiClient() {
        // Create an instance of GoogleAPIClient.
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        if (googleApiClient != null && (!googleApiClient.isConnected() && !googleApiClient.isConnecting())) {
            googleApiClient.connect();
        }
        super.onStop();
    }

    @Override
    protected void onStop() {
        if (googleApiClient != null && (googleApiClient.isConnected() || googleApiClient.isConnecting())) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    protected void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    protected void setupFab() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view ->
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
        );
    }

    protected void setupCoverImage() {
        staticMapCoverImage = (ImageView) findViewById(R.id.static_map_cover_image);
    }

    protected void setupDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    protected void setupNav() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    protected void setupRefresh() {

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);

        // FYI using method reference here. The callback just delegates to this.refreshList().
        refreshLayout.setOnRefreshListener(this::startFromScratch);
    }

    protected void setupAdapter(@Nullable Bundle savedInstanceState) {
        busStopsProvider = new BusStopsProvider(this, getInjector());
        busStopsAdapter = new BusStopsAdapter(busStopsProvider, savedInstanceState, this);
    }

    protected void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DefaultListItemDecorator(this));

        recyclerView.setAdapter(busStopsAdapter);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Resets all data and starts loading from the beginning.
     */
    protected void startFromScratch() {
        // Note that showing the list view implicitly shows the loading indicator when loading.
        busStopsAdapter.refreshList();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        busStopsAdapter.onSaveInstanceState(outState);
    }

    @Override
    public void onFinishedLoading() {
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onError(String message) {
        if (recyclerView != null) {
            SnackbarUtils.showError(message, recyclerView);
        }
    }

    /**
     * Initiates location services to find the user's current location. If the permission has not yet been granted,
     * then we'll ask for permission.
     */
    private void getUserLocation() {

        Log.v("DEBUG", "Called getUserLocation");

        // If permission has not yet been granted, then ask the user.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this,
                    IntentRequestCodes.LOCATION_PERMISSION_REQUEST_CODE.ordinal(),
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    getString(R.string.location_permission_dialog_title),
                    getString(R.string.location_permission_rational),
                    PERMISSION_DIALOG_TAG);

        } else {

            // TODO zoom? google api client connected?

            // Permission was granted. Get the user's location.
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            if (lastLocation != null) {
//            if (lastLocation != null && busStopsProvider.isDifferentLocation(lastLocation.getLatitude(),
// lastLocation.getLongitude())) {

                busStopsProvider.setLatitude(lastLocation.getLatitude());
                busStopsProvider.setLongitude(lastLocation.getLongitude());

                if (busStopsAdapter.getDataCount() == 0) {
                    startFromScratch();
                }

            } else {
                Log.v("DEBUG", "Last known location is null!");
            }

            Log.v("DEBUG", "url = " + buildStaticMapUrl(busStopsProvider.getLatitude(), busStopsProvider.getLongitude
                    ()));
            Picasso.with(this)
                    .load(buildStaticMapUrl(busStopsProvider.getLatitude(), busStopsProvider.getLongitude()))
                    .fit()
                    .centerCrop()
                    .into(staticMapCoverImage);
        }
    }

    // TODO MOVE THIS
    private String buildStaticMapUrl(Double latitude, Double longitude) {

        return Phrase.from(getResources(), R.string.static_map_url)
                .put("center", latitude + "," + longitude)
                .put("zoom", MAP_ZOOM_RATIO)
                .put("size", Resources.getSystem().getDisplayMetrics().widthPixels + "x" + getResources()
                        .getDimensionPixelSize(R.dimen.default_app_bar_height))
                .put("markers", "color:blue|size:tiny|" + latitude + "," + longitude)
                .put("key", getString(R.string.google_maps_key))
                .format().toString();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        // We're only interested in location services
        if (requestCode != IntentRequestCodes.LOCATION_PERMISSION_REQUEST_CODE.ordinal()) {
            return;
        }

        // If the permission was granted, then let's get the user's location now.
        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Enable the my location layer if the permission has been granted.
            getUserLocation();
        } else {

            // TODO zoom in at a default location in winnipeg?

            // Permission was denied. Let's display a dialog explaining why we need location services, and how to grant
            // the permission if it's permanently denied.
            FragmentUtils.showFragment(this,
                    PermissionDeniedDialog.newInstance(getString(R.string.location_permission_denied)),
                    PERMISSION_DIALOG_TAG);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v("DEBUG", "GOOGLE API CLIENT onConnected");

        // TODO TEST
        getUserLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("DEBUG", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("DEBUG", "onConnectionFailed " + connectionResult.getErrorMessage());
    }
}
