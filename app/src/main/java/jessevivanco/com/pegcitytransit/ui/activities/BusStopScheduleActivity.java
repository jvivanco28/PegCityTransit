package jessevivanco.com.pegcitytransit.ui.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.ui.activities.base.BaseActivity;
import jessevivanco.com.pegcitytransit.ui.adapters.ScheduledStopAdapter;
import jessevivanco.com.pegcitytransit.ui.item_decorations.VerticalListItemDecoration;
import jessevivanco.com.pegcitytransit.ui.presenters.BusStopScheduleListPresenter;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;

@Deprecated
public class BusStopScheduleActivity extends BaseActivity implements BusStopScheduleListPresenter.ViewContract, SwipeRefreshLayout.OnRefreshListener {

    public static final String ARG_KEY_BUS_STOP = "bus_stop";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.cover_image)
    ImageView coverImage;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    ScheduledStopAdapter scheduledStopAdapter;
    BusStopScheduleListPresenter stopSchedulePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        BusStopViewModel busStop = Parcels.unwrap(getIntent().getParcelableExtra(ARG_KEY_BUS_STOP));

        setupToolbar();
        setupAdapter(savedInstanceState);
        setupRecyclerView();
        setupRefreshLayout();
        setupCoverImage(busStop);

        if (savedInstanceState == null) {
            stopSchedulePresenter.loadScheduleForBusStop(busStop.getKey());
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_bus_stop_schedule;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        scheduledStopAdapter.onSaveInstanceState(outState);
    }

    protected void setupToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // FYI can't set the toolbar title directly with the toolbar reference: we need to call the getter first.
        // http://stackoverflow.com/questions/26486730/in-android-app-toolbar-settitle-method-has-no-effect-application-name-is-shown
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
        }
    }

    private void setupAdapter(Bundle savedInstanceState) {
        stopSchedulePresenter = new BusStopScheduleListPresenter(getInjector(), this);
        scheduledStopAdapter = new ScheduledStopAdapter(stopSchedulePresenter, savedInstanceState);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new VerticalListItemDecoration(getResources().getDimensionPixelSize(R.dimen.material_spacing_small), getResources().getDimensionPixelSize(R.dimen.material_spacing_small)));
        recyclerView.setAdapter(scheduledStopAdapter);
    }

    private void setupRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void setupCoverImage(BusStopViewModel busStop) {

        Picasso.with(this)
                .load(stopSchedulePresenter.generateMapImageUrl(getResources(), busStop))
                .fit()
                .centerCrop()
                .into(coverImage);
    }

    @Override
    public void onRefresh() {

        BusStopViewModel busStop = Parcels.unwrap(getIntent().getParcelableExtra(ARG_KEY_BUS_STOP));
        stopSchedulePresenter.loadScheduleForBusStop(busStop.getKey());
    }

    @Override
    public void showScheduledStops(List<ScheduledStopViewModel> scheduledStops) {


        scheduledStopAdapter.setScheduledStops(scheduledStops);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showErrorMessage(String msg) {
        Snackbar.make(swipeRefreshLayout, msg, Snackbar.LENGTH_LONG).show();
        swipeRefreshLayout.setRefreshing(false);
    }
}
