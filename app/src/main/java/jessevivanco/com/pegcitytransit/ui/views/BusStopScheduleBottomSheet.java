package jessevivanco.com.pegcitytransit.ui.views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import jessevivanco.com.pegcitytransit.ui.item_decorations.VerticalListItemDecoration;
import jessevivanco.com.pegcitytransit.ui.presenters.BusStopSchedulePresenter;
import jessevivanco.com.pegcitytransit.ui.view_holders.NoResultsCellViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;
import jessevivanco.com.pegcitytransit.ui.views.layout_manager.OneShotAnimatedLinearLayoutManager;

public class BusStopScheduleBottomSheet extends CoordinatorLayout implements BusStopSchedulePresenter.ViewContract, NoResultsCellViewHolder.OnRefreshButtonClickedListener {

    private static final String TAG = BusStopScheduleBottomSheet.class.getSimpleName();

    private static final String STATE_KEY_PROGRESS_BAR_VISIBILITY = "progress_bar";
    private static final String STATE_KEY_BUS_STOP = "bus_stop";

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

    private OnFavStopRemovedListener onFavStopRemovedListener;
    private BusStopViewModel busStop;

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
        LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_schedule, this, true);
        ButterKnife.bind(this);
    }

    public void initialize(OnFavStopRemovedListener onFavStopRemovedListener,
                           @Nullable Bundle savedInstanceState,
                           AppComponent injector) {

        this.onFavStopRemovedListener = onFavStopRemovedListener;

        stopSchedulePresenter = new BusStopSchedulePresenter(injector, this);
        stopScheduleAdapter = new ScheduledStopAdapter(savedInstanceState, this);

        layoutManager = new OneShotAnimatedLinearLayoutManager(getContext(), stopScheduleRecyclerView);
        stopScheduleRecyclerView.setLayoutManager(layoutManager);
        stopScheduleRecyclerView.addItemDecoration(new VerticalListItemDecoration(getResources().getDimensionPixelSize(R.dimen.material_spacing_small), getResources().getDimensionPixelSize(R.dimen.material_spacing_small)));

        stopScheduleRecyclerView.setAdapter(stopScheduleAdapter);

        bottomSheetProgressBar.setVisibility(savedInstanceState != null ?
                savedInstanceState.getInt(STATE_KEY_PROGRESS_BAR_VISIBILITY, View.GONE) :
                View.GONE);

        busStop = savedInstanceState != null ?
                Parcels.unwrap(savedInstanceState.getParcelable(STATE_KEY_BUS_STOP)) :
                null;

        displayBusStopInfo(busStop);
    }

    private void displayBusStopInfo(@Nullable BusStopViewModel busStop) {
        if (busStop != null) {
            bottomSheetToolbarTitle.setText(busStop.getName());
            favStopButton.setProgress(busStop.isSavedStop() ? 1 : 0);
        } else {
            bottomSheetToolbarTitle.setText(null);
            favStopButton.setProgress(0);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_KEY_PROGRESS_BAR_VISIBILITY, bottomSheetProgressBar.getVisibility());
        outState.putParcelable(STATE_KEY_BUS_STOP, Parcels.wrap(busStop));
        stopScheduleAdapter.onSaveInstanceState(outState);
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
    public void showScheduledStops(List<ScheduledStopViewModel> scheduledStops) {
        layoutManager.setAnimateNextLayout(scheduledStops != null);
        stopScheduleAdapter.setList(scheduledStops);
    }

    @Override
    public void onRefreshButtonClicked() {
        stopSchedulePresenter.loadScheduleForBusStop(busStop.getKey());
    }

    @Override
    public void showLoadingScheduleIndicator(boolean visible) {
        if (visible) {
            bottomSheetProgressBar.setVisibility(View.VISIBLE);
        } else {
            bottomSheetProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void showErrorMessage(String msg) {
        stopScheduleAdapter.setNoResultsMessage(msg);
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
