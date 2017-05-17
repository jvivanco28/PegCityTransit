package jessevivanco.com.pegcitytransit.ui.views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
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
import jessevivanco.com.pegcitytransit.ui.view_models.BusStopViewModel;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;

public class BusStopScheduleBottomSheet extends CoordinatorLayout implements BusStopSchedulePresenter.ViewContract {

    private static final String STATE_KEY_PROGRESS_BAR_VISIBILITY = "progress_bar";
    private static final String STATE_KEY_BUS_STOP = "bus_stop";

    @BindView(R.id.bottom_sheet_toolbar_close_button)
    Button closeButton;

    @BindView(R.id.schedule_recycler_view)
    RecyclerView stopScheduleRecyclerView;
    ScheduledStopAdapter stopScheduleAdapter;
    BusStopSchedulePresenter stopSchedulePresenter;

    @BindView(R.id.bottom_sheet_toolbar_title)
    TextView bottomSheetToolbarTitle;

    @BindView(R.id.toolbar_fav_stop)
    LottieAnimationView favStopButton;

    @BindView(R.id.bottom_sheet_progress_bar)
    ProgressBar bottomSheetProgressBar;

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

    public void initialize(@Nullable Bundle savedInstanceState,
                           AppComponent injector) {

        stopSchedulePresenter = new BusStopSchedulePresenter(injector, this);
        stopScheduleAdapter = new ScheduledStopAdapter(savedInstanceState);

        stopScheduleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        stopScheduleRecyclerView.addItemDecoration(new VerticalListItemDecoration(getResources().getDimensionPixelSize(R.dimen.material_spacing_small), getResources().getDimensionPixelSize(R.dimen.material_spacing_small)));
        stopScheduleRecyclerView.setAdapter(stopScheduleAdapter);

        bottomSheetProgressBar.setVisibility(savedInstanceState != null ?
                savedInstanceState.getInt(STATE_KEY_PROGRESS_BAR_VISIBILITY, View.GONE) :
                View.GONE);

        busStop = savedInstanceState != null ?
                Parcels.unwrap(savedInstanceState.getParcelable(STATE_KEY_BUS_STOP)) :
                null;

        bottomSheetToolbarTitle.setText(busStop != null ?
                busStop.getName() :
                null);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_KEY_PROGRESS_BAR_VISIBILITY, bottomSheetProgressBar.getVisibility());
        outState.putParcelable(STATE_KEY_BUS_STOP, Parcels.wrap(busStop));
        stopScheduleAdapter.onSaveInstanceState(outState);
    }

    public void loadScheduleForBusStop(BusStopViewModel busStop) {
        this.busStop = busStop;

        bottomSheetToolbarTitle.setText(busStop.getName());
        stopSchedulePresenter.loadScheduleForBusStop(busStop.getKey());
    }

    public void setOnCloseButtonClickedListener(OnClickListener listener) {
        closeButton.setOnClickListener(listener);
    }

    @Override
    public void showErrorMessage(String msg) {

        // TODO show error state in adapter
        Log.e("DEBUG", msg);
    }

    @Override
    public void showScheduledStops(List<ScheduledStopViewModel> scheduledStops) {
        stopScheduleAdapter.setScheduledStops(scheduledStops);
    }

    @Override
    public void showLoadingScheduleIndicator(boolean visible) {
        if (visible) {
            bottomSheetProgressBar.setVisibility(View.VISIBLE);
        } else {
            bottomSheetProgressBar.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.toolbar_fav_stop)
    public void toggleFavStop() {
        // TODO Figure out if the stop is already saved or not.
        favStopButton.playAnimation();
        stopSchedulePresenter.saveBusStop(busStop);
//        favStopButton.reverseAnimation();
    }
}
