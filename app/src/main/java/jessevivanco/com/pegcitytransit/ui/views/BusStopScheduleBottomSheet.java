package jessevivanco.com.pegcitytransit.ui.views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.data.dagger.components.AppComponent;
import jessevivanco.com.pegcitytransit.ui.adapters.ScheduledStopAdapter;
import jessevivanco.com.pegcitytransit.ui.callbacks.OnBusRouteSelectedListener;
import jessevivanco.com.pegcitytransit.ui.item_decorations.VerticalListItemDecoration;
import jessevivanco.com.pegcitytransit.ui.presenters.BusStopSchedulePresenter;
import jessevivanco.com.pegcitytransit.ui.presenters.ViewState;
import jessevivanco.com.pegcitytransit.ui.view_holders.ScheduledStopCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_models.BusRouteViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;
import jessevivanco.com.pegcitytransit.ui.views.layout_manager.OneShotAnimatedLinearLayoutManager;

public class BusStopScheduleBottomSheet extends LinearLayout implements BusStopSchedulePresenter.ViewContract, ScheduledStopCellViewHolder.OnBusRouteNumberClickedListener {

    private static final String TAG = BusStopScheduleBottomSheet.class.getSimpleName();

    private static final String STATE_KEY_PROGRESS_BAR_VISIBILITY = "progress_bar";
    private static final String STATE_KEY_BUS_STOP = "bus_stop";
    private static final String STATE_KEY_VIEW_STATE = BusStopScheduleBottomSheet.class.getSimpleName() + "_view_state";

    @BindView(R.id.bottom_sheet_toolbar_close_button)
    Button closeButton;

    @BindView(R.id.schedule_recycler_view)
    RecyclerView stopScheduleRecyclerView;
    OneShotAnimatedLinearLayoutManager layoutManager;
    ScheduledStopAdapter stopScheduleAdapter;
    BusStopSchedulePresenter stopSchedulePresenter;

    @BindView(R.id.bottom_sheet_toolbar_title)
    TextView bottomSheetToolbarTitle;

    @BindView(R.id.toolbar_fav_stop)
    LottieAnimationView favStopButton;

    @BindView(R.id.bottom_sheet_progress_bar)
    ProgressBar bottomSheetProgressBar;

    @BindView(R.id.loading_view)
    FrameLayout loadingCell;

    @BindView(R.id.error_state_cell)
    ErrorStateCell errorStateCell;

    @BindView(R.id.error_state_cell_container)
    ViewGroup errorCellContainer;

    private ViewState viewState;
    private OnFavStopRemovedListener onFavStopRemovedListener;
    private BusStopViewModel busStop;
    private OnBusRouteSelectedListener onBusRouteSelectedListener;

    public BusStopScheduleBottomSheet(Context context) {
        super(context);
        inflate();
    }

    public BusStopScheduleBottomSheet(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate();
    }

    public BusStopScheduleBottomSheet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate();
    }

    private void inflate() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_bottom_sheet_schedule, this, true);
        ButterKnife.bind(this);
        setOrientation(VERTICAL);
    }

    public void initialize(OnFavStopRemovedListener onFavStopRemovedListener,
                           OnBusRouteSelectedListener onBusRouteSelectedListener,
                           @Nullable Bundle savedInstanceState,
                           AppComponent injector) {

        this.onFavStopRemovedListener = onFavStopRemovedListener;
        this.onBusRouteSelectedListener = onBusRouteSelectedListener;

        // Setup recycler view and adapter
        stopSchedulePresenter = new BusStopSchedulePresenter(injector, this);
        stopScheduleAdapter = new ScheduledStopAdapter(this, savedInstanceState);

        layoutManager = new OneShotAnimatedLinearLayoutManager(getContext(), stopScheduleRecyclerView);
        stopScheduleRecyclerView.setLayoutManager(layoutManager);
        stopScheduleRecyclerView.addItemDecoration(new VerticalListItemDecoration(getResources().getDimensionPixelSize(R.dimen.material_spacing_small), getResources().getDimensionPixelSize(R.dimen.material_spacing_small)));

        stopScheduleRecyclerView.setAdapter(stopScheduleAdapter);

        // Setup refresh listener
        errorStateCell.setOnRefreshButtonClickedListener(this::refresh);

        // Restore instance state
        bottomSheetProgressBar.setVisibility(savedInstanceState != null ?
                savedInstanceState.getInt(STATE_KEY_PROGRESS_BAR_VISIBILITY, View.GONE) :
                View.GONE);

        busStop = savedInstanceState != null ?
                Parcels.unwrap(savedInstanceState.getParcelable(STATE_KEY_BUS_STOP)) :
                null;
        viewState = savedInstanceState != null ?
                ViewState.values()[savedInstanceState.getInt(STATE_KEY_VIEW_STATE, ViewState.LIST.ordinal())] :
                ViewState.LIST;

        errorStateCell.onRestoreInstanceState(savedInstanceState);

        displayBusStopInfo(busStop);
        showViewState(viewState);
    }

    private void displayBusStopInfo(@Nullable BusStopViewModel busStop) {
        if (busStop != null) {
            bottomSheetToolbarTitle.setText(busStop.getName());
            bottomSheetToolbarTitle.setSelected(true);
            favStopButton.setProgress(busStop.isSavedStop() ? 1 : 0);
        } else {
            bottomSheetToolbarTitle.setText(null);
            favStopButton.setProgress(0);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_KEY_PROGRESS_BAR_VISIBILITY, bottomSheetProgressBar.getVisibility());
        outState.putParcelable(STATE_KEY_BUS_STOP, Parcels.wrap(busStop));
        outState.putInt(STATE_KEY_VIEW_STATE, viewState.ordinal());
        stopScheduleAdapter.onSaveInstanceState(outState);
        errorStateCell.onSaveInstanceState(outState);
    }

    public void loadScheduleForBusStop(BusStopViewModel busStop) {
        this.busStop = busStop;

        displayBusStopInfo(busStop);
        stopSchedulePresenter.loadScheduleForBusStop(busStop.getKey());
    }

    public void setOnCloseButtonClickedListener(OnClickListener listener) {
        closeButton.setOnClickListener(listener);
    }

    @Override
    public void setScheduledStops(List<ScheduledStopViewModel> scheduledStops) {
        layoutManager.setAnimateNextLayout(scheduledStops != null);
        stopScheduleAdapter.setList(scheduledStops);
    }

    @Override
    public void showErrorMessage(String msg) {
        errorStateCell.setNoResultsText(msg);
    }

    @Override
    public void showViewState(ViewState viewState) {
        this.viewState = viewState;

        stopScheduleRecyclerView.setVisibility(viewState == ViewState.LIST ? VISIBLE : GONE);
        errorCellContainer.setVisibility(viewState == ViewState.ERROR ? VISIBLE : GONE);
        bottomSheetProgressBar.setVisibility(viewState == ViewState.LOADING ? VISIBLE : GONE);
        loadingCell.setVisibility(viewState == ViewState.LOADING ? VISIBLE : GONE);
    }

    @Override
    public void onBusRouteNumberClicked(Integer busRouteNumber) {
        // FYI: bus route keys and numbers and the same thing.
        stopSchedulePresenter.loadBusRoute(Long.valueOf(busRouteNumber));
    }

    @Override
    public void onBusRouteLoaded(BusRouteViewModel busRouteViewModel) {
        onBusRouteSelectedListener.onBusRouteSelected(busRouteViewModel);
    }

    @OnClick(R.id.toolbar_fav_stop)
    public void toggleFavStop() {

        if (busStop != null) {
            if (busStop.isSavedStop()) {
                favStopButton.setSpeed(2);
                favStopButton.reverseAnimation();
                stopSchedulePresenter.removeSavedBusStop(busStop, onFavStopRemovedListener);
            } else {
                favStopButton.setSpeed(1);
                favStopButton.playAnimation();
                stopSchedulePresenter.saveBusStop(busStop);
            }
        } else {
            throw new IllegalStateException("Can't save a null bus stop!");
        }
    }

    @OnClick(R.id.bottom_sheet_toolbar_refresh_button)
    public void refresh() {
        stopSchedulePresenter.loadScheduleForBusStop(busStop.getKey());
    }

    public void tearDown() {
        stopSchedulePresenter.tearDown();
    }

    /**
     * Listener invoked when a stop is removed from the user's list of saved stops.
     */
    public interface OnFavStopRemovedListener {

        void onFavStopRemoved(BusStopViewModel busStop);
    }
}
