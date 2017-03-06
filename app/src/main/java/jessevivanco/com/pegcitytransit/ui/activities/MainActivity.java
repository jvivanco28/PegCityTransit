package jessevivanco.com.pegcitytransit.ui.activities;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.provider.BusStopsAdapterProvider;
import jessevivanco.com.pegcitytransit.provider.LocationProvider;
import jessevivanco.com.pegcitytransit.ui.activities.base.BaseActivity;
import jessevivanco.com.pegcitytransit.ui.adapters.BusStopsAdapter;
import jessevivanco.com.pegcitytransit.ui.fragments.FragmentUtils;
import jessevivanco.com.pegcitytransit.ui.fragments.PermissionDeniedDialog;
import jessevivanco.com.pegcitytransit.ui.item_decorations.DefaultListItemDecorator;
import jessevivanco.com.pegcitytransit.ui.util.IntentRequestCodes;
import jessevivanco.com.pegcitytransit.ui.util.PermissionUtils;
import jessevivanco.com.pegcitytransit.ui.util.SnackbarUtils;

public class MainActivity
        extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LocationProvider.LocationProviderViewContract {

    private static final String PERMISSION_DIALOG_TAG = "dialog";

    protected Toolbar toolbar;
    protected ImageView staticMapCoverImage;
    protected FloatingActionButton fab;
    protected DrawerLayout drawer;
    protected NavigationView navigationView;
    protected SwipeRefreshLayout refreshLayout;
    protected RecyclerView recyclerView;

    private LocationProvider locationProvider;
    private BusStopsAdapterProvider busStopsProvider;
    private BusStopsAdapter busStopsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup UI elements.
        setupToolbar();
        setupCoverImage();
        setupFab();
        setupDrawer();
        setupNav();
        setupRefresh();
        setupAdapter(savedInstanceState);
        setupRecyclerView();
        setupMapsProvider();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (locationProvider != null) {
            locationProvider.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationProvider != null) {
            locationProvider.stop();
        }
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
        refreshLayout.setOnRefreshListener(this::refreshList);
    }

    protected void setupAdapter(@Nullable Bundle savedInstanceState) {
        busStopsProvider = new BusStopsAdapterProvider(this, getInjector());
        busStopsAdapter = new BusStopsAdapter(this, busStopsProvider, savedInstanceState);
    }

    protected void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DefaultListItemDecorator(this));

        recyclerView.setAdapter(busStopsAdapter);
    }

    protected void setupMapsProvider() {
        locationProvider = new LocationProvider(this, busStopsProvider, this);
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
    @Override
    public void refreshList() {

        // Hide the SwipeRefreshLayout's refresh spinner. We'll just use our adapter's loading spinner instead.
        refreshLayout.setRefreshing(false);

        // Note that showing the list view implicitly shows the loading indicator when loading.
        busStopsAdapter.refreshList(message -> {

            // Display the error if we got one.
            if (message != null) {
                SnackbarUtils.showError(message, recyclerView);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        busStopsAdapter.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (busStopsAdapter != null) {
            busStopsAdapter.onDestroy();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        // We're only interested in location services
        if (requestCode != IntentRequestCodes.LOCATION_PERMISSION_REQUEST_CODE.ordinal()) {
            return;
        }

        // If the permission was granted, then let's get the user's location.
        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Enable the my location layer if the permission has been granted.
            locationProvider.getUserLocation();
        } else {

            // Permission was denied. Let's display a dialog explaining why we need location services, and how to grant
            // the permission if it's permanently denied.
            FragmentUtils.showFragment(this,
                    PermissionDeniedDialog.newInstance(getString(R.string.location_permission_denied)),
                    PERMISSION_DIALOG_TAG);
        }
    }

    @Override
    public void setStaticMapCoverImage(String imageUrl) {
        Picasso.with(this)
                .load(imageUrl)
                .fit()
                .centerCrop()
                .into(staticMapCoverImage);
    }

    @Override
    public void onRequestPermission(int intentRequestCode,
                                    String permission,
                                    String dialogTitle,
                                    String dialogRationale) {

        PermissionUtils.requestPermission(this,
                intentRequestCode,
                permission,
                dialogTitle,
                dialogRationale,
                PERMISSION_DIALOG_TAG);
    }
}
